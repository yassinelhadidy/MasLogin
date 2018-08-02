package com.masary.yassin.masarypaymentapp.services

import com.masary.yassin.masarypaymentapp.MOCK_BASE_URL
import com.masary.yassin.masarypaymentapp.infrastructure.MasCustomerInfoRepository
import com.masary.yassin.masarypaymentapp.infrastructure.MasaryRestServiceFactory
import com.masary.yassin.masarypaymentapp.models.CustomerInfo
import com.masary.yassin.masarypaymentapp.models.ModelException
import com.masary.yassin.masarypaymentapp.models.User
import com.masary.yassin.masarypaymentapp.models.exception.UnauthorizedException
import com.masary.yassin.masarypaymentapp.models.services.LoginService
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.concurrent.TimeUnit

/**
 *Created by yassin on 7/29/18.
 */
@RunWith(Parameterized::class)
class LoginServiceTest(private val setupTestParameter: SetupTestParameter<*>) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): List<Array<*>> = listOf(
                arrayOf(object : SetupTestParameter<CustomerInfo> {
                    override fun setup(): TestParameter<CustomerInfo> {
                        val customerInfoRepository = MasCustomerInfoRepository(MasaryRestServiceFactory(MOCK_BASE_URL).service)
                        val login = LoginService(customerInfoRepository)
                        val customerInfo = CustomerInfo(0, 1, 755622, 0, 755622, 8, 2,
                                "Y", "F", "01004605609", "Michael Jacoub", "مايكل يعقوب",
                                "N", "no", "دلوقتي اتصالات عملت اقوي شحنه في مصر من 5 لـ 25 جنيه تشحنها زي ما تحب رصيد، أو دقايق لكل الشبكات، أو ميكس لكل الشبكات (انترنت - دقايق - رسائل)")

                        val validUserMap = hashMapOf(
                                User("مايكل يعقوب", "8", "123456789", "mobiwire") to customerInfo
                        )

                        val inValidUserMap = hashMapOf(
                                User("مينا", "88", "123456789", "android") to UnauthorizedException("[Invalid Credentials]")
                        )

                        return object : TestParameter<CustomerInfo> {
                            override fun getCorrectUsers(): Set<User> = validUserMap.keys

                            override fun successfulLogin(user: User): Triple<Observable<out CustomerInfo>, User, CustomerInfo?> {
                                val observable = login.login(user.username!!, user.password!!, user.deviceType)
                                return Triple(observable, user, validUserMap[user])
                            }

                            override fun getInCorrectUsers(): Set<User> = inValidUserMap.keys

                            override fun loginUnauthorizedUser(user: User): Triple<Observable<out CustomerInfo>, User, Throwable?> {
                                val observable = login.login(user.username!!, user.password!!, user.deviceType)
                                return Triple(observable, user, inValidUserMap[user])
                            }
                        }
                    }
                    override fun toString() = CustomerInfo::class.java.simpleName!!
                }))
        override fun toString() = CustomerInfo::class.java.simpleName!!
    }

    @Test
    fun successfulLogin() {
        val testParameter = setupTestParameter.setup()
        val testObserver = TestObserver<Triple<Any?, User, Any?>>()
        Observable.fromIterable(testParameter.getCorrectUsers()
                .map {
                    val triple = testParameter.successfulLogin(it)
                    triple.first.map {
                        Triple(it, triple.second, triple.third)
                    }
                })
                .flatMap { it }
                .subscribeOn(Schedulers.io())
                .subscribe(testObserver)
        testObserver.awaitTerminalEvent(1, TimeUnit.MINUTES)
        testObserver.values().forEach {
            Assert.assertEquals(it.first, it.third)
        }
    }

    @Test
    fun loginUnauthorizedUser() {

        val testParameter = setupTestParameter.setup()
        val testObserver = TestObserver<Triple<Notification<out Any?>, User, Throwable?>>()
        Observable.fromIterable(testParameter.getInCorrectUsers()
                .map {
                    val triple = testParameter.loginUnauthorizedUser(it)
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
            Assert.assertTrue(it.second.toString(), it.first.isOnError)
            Assert.assertTrue(it.second.toString(), it.first.error is ModelException)
//            Assert.assertEquals(it.second.toString(),
//                    it.third?.message, it.first.error?.cause?.message)  //FIXME : initiate throwable object
        }
    }

    interface TestParameter<out T> {
        fun getCorrectUsers(): Set<User>
        fun successfulLogin(user: User): Triple<Observable<out T>, User, CustomerInfo?>
        fun getInCorrectUsers(): Set<User>
        fun loginUnauthorizedUser(user: User): Triple<Observable<out T>, User, Throwable?>
    }

    interface SetupTestParameter<out T> {
        fun setup(): TestParameter<T>
    }
}