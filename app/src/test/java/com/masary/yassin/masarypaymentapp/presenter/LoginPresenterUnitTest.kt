package com.masary.yassin.masarypaymentapp.presenter

import com.masary.yassin.masarypaymentapp.models.CustomerInfo
import com.masary.yassin.masarypaymentapp.models.User
import com.masary.yassin.masarypaymentapp.models.exception.UnauthorizedException
import com.masary.yassin.masarypaymentapp.models.services.LoginService
import com.masary.yassin.masarypaymentapp.ui.merchantlogin.LoginActivity
import com.masary.yassin.masarypaymentapp.ui.merchantlogin.LoginPresenter
import com.nhaarman.mockito_kotlin.*
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

/**
 *Created by yassin on 8/1/18.
 */
@Config
@RunWith(ParameterizedRobolectricTestRunner::class)
class LoginPresenterUnitTest(private val setupTestParameter: SetupTestParameter) {

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{index}: {0}")
        fun data(): List<Array<*>> = listOf(arrayOf(object : SetupTestParameter {
            override fun setup(): TestParameter<User> {
                val mockLoginService = mock<LoginService>()
                val mockView = mock<LoginActivity>()
                val loginPresenter = LoginPresenter(mockLoginService)
                loginPresenter.setView(mockView)
                val customerInfo = CustomerInfo(0, 1, 755771, 0, 755771, 8, 2,
                        "Y", "F", "01004605609", "Michael Jacoub", "مايكل يعقوب",
                        "N", "no", "دلوقتي اتصالات عملت اقوي شحنه في مصر من 5 لـ 25 جنيه تشحنها زي ما تحب رصيد، أو دقايق لكل الشبكات، أو ميكس لكل الشبكات (انترنت - دقايق - رسائل)")


                val validUserMap = hashMapOf(
                        User("مايكل يعقوب", "8", "123456789", "mobiwire") to customerInfo
                )

                val inValidUserMap = hashMapOf(
                        User("مينا", "885", "123456789", "android") to UnauthorizedException("Invalid User ID or Password"),
                        User("مينا", "88", "123456789", "android") to UnauthorizedException("In-Active Customer")
                )

                return object : TestParameter<User> {
                    override fun getCorrectUsers(): Set<User> = validUserMap.keys

                    override fun loginWithCorrectCredentialsTestCase(user: User) {
                        whenever(mockLoginService.login(user.username!!, user.password!!, user.deviceType))
                                .thenReturn(Observable.just(customerInfo))

                        loginPresenter.login(user.username!!, user.password!!, user.deviceType)
                        verify(mockView, only()).showLoading()
                        verify(mockView, only()).hideLoading()
                        verify(mockView, only()).showValid(eq(1))
                    }

                    override fun getInCorrectUsers(): Set<User> = inValidUserMap.keys

                    override fun loginWithICorrectCredentialsTestCase(user: User) {
                        whenever(mockLoginService.login(user.username!!, user.password!!, user.deviceType))
                                .thenReturn(Observable.error { inValidUserMap[user] })
                        loginPresenter.login(user.username!!, user.password!!, user.deviceType)
                        verify(mockView).showLoading()
                        verify(mockView).hideLoading()
                        verify(mockView).showError(inValidUserMap[user] as Throwable)
                    }
                }
            }
        }))
    }

    @Test
    fun testloginWithCorrectCredentialsTestCase() {
//        val testParameter = setupTestParameter.setup()
//        val users= testParameter.getCorrectUsers()
//        users.forEach {
//            testParameter.loginWithCorrectCredentialsTestCase(it)
//        }

        val testParameter = setupTestParameter.setup()
        val testObserver = TestObserver<Any>()
        Observable.fromIterable(testParameter.getCorrectUsers()
                .map {
                    val triple = testParameter.loginWithCorrectCredentialsTestCase(it)
                })
                .subscribeOn(Schedulers.io())
                .subscribe(testObserver)

        testObserver.awaitTerminalEvent(1, TimeUnit.MINUTES)
        testObserver.assertSubscribed()
                .assertNoErrors()
                .assertComplete()
    }

    @Test
    fun testloginWithICorrectCredentialsTestCase() {
//        val testParameter = setupTestParameter.setup()
//        testParameter.duplicatedScannedBarcodeAndFoundInSheet()

        val testParameter = setupTestParameter.setup()
        val testObserver = TestObserver<Any>()
        Observable.fromIterable(testParameter.getInCorrectUsers()
                .map {
                    val triple = testParameter.loginWithICorrectCredentialsTestCase(it)
                })
                .subscribeOn(Schedulers.io())
                .subscribe(testObserver)

        testObserver.awaitTerminalEvent(1, TimeUnit.MINUTES)
        testObserver.assertSubscribed()
                .assertNoErrors()
                .assertComplete()
    }

    interface TestParameter<User> {
        fun getCorrectUsers(): Set<User>
        fun loginWithCorrectCredentialsTestCase(user: User)
        fun getInCorrectUsers(): Set<User>
        fun loginWithICorrectCredentialsTestCase(user: User)
    }

    interface SetupTestParameter {
        fun setup(): TestParameter<User>
    }
}