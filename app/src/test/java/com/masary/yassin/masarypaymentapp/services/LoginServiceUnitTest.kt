package com.masary.yassin.masarypaymentapp.services

import com.masary.yassin.masarypaymentapp.infrastructure.MasCustomerInfoRepository
import com.masary.yassin.masarypaymentapp.models.CustomerInfo
import com.masary.yassin.masarypaymentapp.models.User
import com.masary.yassin.masarypaymentapp.models.exception.UnauthorizedException
import com.masary.yassin.masarypaymentapp.models.services.LoginService
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.HttpException
import retrofit2.Response
import java.util.concurrent.TimeUnit

/**
 *Created by yassin on 7/29/18.
 */
@Config
@RunWith(ParameterizedRobolectricTestRunner::class)
class LoginServiceUnitTest(private val setupTestParameter: SetupTestParameter<*>) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{index}: {0}")
        fun data(): List<Array<*>> = listOf(arrayOf(object : SetupTestParameter<CustomerInfo> {
            override fun setup(): TestParameter<CustomerInfo> {

                val customerInfo = CustomerInfo(0, 1, 755771, 0, 755771, 8, 2,
                        "Y", "F", "01004605609", "Michael Jacoub", "مايكل يعقوب",
                        "N", "no", "دلوقتي اتصالات عملت اقوي شحنه في مصر من 5 لـ 25 جنيه تشحنها زي ما تحب رصيد، أو دقايق لكل الشبكات، أو ميكس لكل الشبكات (انترنت - دقايق - رسائل)")

                val mockMasCustomerInfoRepository = Mockito.mock(MasCustomerInfoRepository::class.java)

                val login = LoginService(mockMasCustomerInfoRepository)

                val validUserMap = hashMapOf(
                        User("مايكل يعقوب", "87", "123456789", "mobiwire") to customerInfo
                )

                val inValidUserMap = hashMapOf(
                        User("مينا", "885", "123456789", "android") to UnauthorizedException("Invalid User ID or Password"),
                        User("مينا", "88", "123456789", "android") to UnauthorizedException("In-Active Customer")
                )

                return object : TestParameter<CustomerInfo> {
                    override fun getCorrectUsers(): Set<User> = validUserMap.keys

                    override fun successfulLogin(user: User): Triple<Observable<out CustomerInfo>, User, CustomerInfo?> {
                        Mockito.`when`(mockMasCustomerInfoRepository.insert(user))
                                .thenReturn(Observable.just(customerInfo))

                        val observable = login.login(user.username!!, user.password!!, user.deviceType)
                        return Triple(observable, user, validUserMap[user])
                    }

                    override fun getInCorrectUsers(): Set<User> = inValidUserMap.keys

                    override fun loginUnauthorizedUser(user: User): Triple<Observable<out CustomerInfo>, User, Throwable?> {
                        val responseBody = ResponseBody.create(null, "{\"type\":\"ValidationError\",\"errors\":{\"message\":[\"Invalid Credentials\"]}}")
                        val errorResponse = Response.error<Any?>(401, responseBody)

                        Mockito.`when`(mockMasCustomerInfoRepository.insert(user))
                                .thenReturn(Observable.error(HttpException(errorResponse)))

                        val observable = login.login(user.username!!, user.password!!, user.deviceType)
                        return Triple(observable, user, inValidUserMap[user])
                    }
                }
            }

            override fun toString() = CustomerInfo::class.java.simpleName!!
        })
        )
    }

    @Test
    fun successfulLogin() {
        val testParameter = setupTestParameter.setup()
        val testObserver = TestObserver<Triple<Any?, Any?, Any?>>()
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
//                Assert.assertEquals(it.third, false)
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
            Assert.assertTrue(it.second.toString(), it.first.error is UnauthorizedException)
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