package com.masary.yassin.masarypaymentapp.infrastructure

import com.masary.yassin.masarypaymentapp.infrastructure.dto.MasCustInfo.Companion.toMasCustInfo
import com.masary.yassin.masarypaymentapp.infrastructure.dto.MasCustLoginResponse
import com.masary.yassin.masarypaymentapp.models.CustomerInfo
import com.masary.yassin.masarypaymentapp.models.User
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

/**
 * Created by yassin on 07/27/18.
 * Test All PostRepository
 */
@Config
@RunWith(ParameterizedRobolectricTestRunner::class)
class PostRepositoryUnitTest(private val setupTestParameter: SetupTestParameter<*>) {
    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{index}: {0}")
        fun data(): List<Array<*>> = listOf(
                arrayOf(object : SetupTestParameter<CustomerInfo> {
                    override fun setup(): TestParameter<CustomerInfo> {
                        val customerInfo = CustomerInfo(0, 1, 755771, 0, 755771, 8, 2,
                                "Y", "F", "01004605609", "Michael Jacoub", "مايكل يعقوب",
                                "N", "no",
                                "دلوقتي اتصالات عملت اقوي شحنه في مصر من 5 لـ 25 جنيه تشحنها زي ما تحب رصيد، أو دقايق لكل الشبكات، أو ميكس لكل الشبكات (انترنت - دقايق - رسائل)")

                        val machineConfing = "@@ip_address=@@machine_id=358240051111110@@imsi=Android%2D%2D89014103211118510720@@device_type=Mobiwire@@sw_version=92@@browser=172.16.10.180%2FMobile%2DWS%2DClient@@platform=Android@@"
                        val codeMWS = "49"
                        val userName = "8"
                        val pass = "123456789"
                        val mockMasaryRestService =
                                Mockito.mock(MasaryRestService::class.java)

                        val customerInfoRepository =
                                MasCustomerInfoRepository(mockMasaryRestService)

                        val validUserMap = hashMapOf(
                                User("مايكل يعقوب", userName, pass, "mobiwire") to customerInfo
                        )

                        val inValidUserMap = hashMapOf(
                                User("مينا", "885", "123456789", "android") to InfrastructureException("Invalid User ID or Password"),
                                User("مينا", "88", "123456789", "android") to InfrastructureException("In-Active Customer")
                        )
                        return object : TestParameter<CustomerInfo> {
                            override fun getCorrectDataForInsertion(): Set<User> = validUserMap.keys

                            override fun successfulInsertionTransaction(user: User): Triple<Observable<out CustomerInfo>, User, CustomerInfo?> {
                                Mockito.`when`(mockMasaryRestService
                                        .login(codeMWS, user.username, user.password, machineConfing))
                                        .thenReturn(Observable.just(MasCustLoginResponse(listOf(validUserMap[user]!!.toMasCustInfo()), "")))
                                val observable = customerInfoRepository.insert(user)
                                return Triple(observable, user, validUserMap[user])
                            }

                            override fun getInCorrectDataForInsertion(): Set<User> = inValidUserMap.keys

                            override fun failureInsertionTransaction(user: User): Triple<Observable<out CustomerInfo>, User, Throwable?> {
                                Mockito.`when`(mockMasaryRestService
                                        .login(codeMWS, user.username, user.password, machineConfing))
                                        .thenReturn(Observable.error { InfrastructureException() })
                                val observable = customerInfoRepository.insert(user)
                                return Triple(observable, user, inValidUserMap[user])
                            }

                            override fun checkInsert() {
                                Assert.assertEquals(customerInfo.id, 0)
                                Assert.assertEquals(customerInfo.groupId, 8)
                                Assert.assertEquals(customerInfo.arUserName, "مايكل يعقوب")
                            }
                        }
                    }
                }))

        override fun toString() = CustomerInfo::class.java.simpleName!!
    }

    @Test
    fun testInsertNonExistingEntityIntoRepository() {
        val testParameter = setupTestParameter.setup()
        val testObserver = TestObserver<Triple<Any?, User, Any?>>()
        Observable.fromIterable(testParameter.getCorrectDataForInsertion()
                .map {
                    val triple = testParameter.successfulInsertionTransaction(it)
                    triple.first.map {
                        Triple(it, triple.second, triple.third)
                    }
                })
                .flatMap { it }
                .subscribeOn(Schedulers.io())
                .subscribe(testObserver)
        testObserver.awaitTerminalEvent(1, TimeUnit.MINUTES)
        testObserver.values().forEach {
            org.junit.Assert.assertEquals(it.first, it.third)
//                Assert.assertEquals(it.third, false)
        }
        testParameter.checkInsert()
    }

    @Test
    fun testInsertDuplicateEntityIntoRepository() {
        val testParameter = setupTestParameter.setup()
        val testObserver = TestObserver<Triple<Notification<out Any?>, User, Throwable?>>()
        Observable.fromIterable(testParameter.getInCorrectDataForInsertion()
                .map {
                    val triple = testParameter.failureInsertionTransaction(it)
                    triple.first.materialize().map { Triple(it, triple.second, triple.third) }
                })
                .flatMap { it }
                .subscribeOn(Schedulers.io())
                .subscribe(testObserver)

        testObserver.awaitTerminalEvent(1, TimeUnit.MINUTES)
        testObserver.assertSubscribed()
                .assertNoErrors()
                .assertComplete()
        testObserver.values().forEach {
            org.junit.Assert.assertTrue(it.second.toString(), it.first.isOnError)
            org.junit.Assert.assertTrue(it.second.toString(), it.first.error is InfrastructureException)
//            Assert.assertEquals(it.second.toString(),
//                    it.third?.message, it.first.error?.cause?.message)  //FIXME : initiate throwable object
        }
    }

    interface TestParameter<out T> {
        fun getCorrectDataForInsertion(): Set<User>
        fun successfulInsertionTransaction(user: User): Triple<Observable<out T>, User, CustomerInfo?>
        fun getInCorrectDataForInsertion(): Set<User>
        fun failureInsertionTransaction(user: User): Triple<Observable<out T>, User, Throwable?>
        fun checkInsert()
    }

    interface SetupTestParameter<out T> {
        fun setup(): TestParameter<T>
    }
}