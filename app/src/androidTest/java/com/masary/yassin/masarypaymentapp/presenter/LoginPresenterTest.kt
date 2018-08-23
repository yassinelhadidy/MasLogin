package com.masary.yassin.masarypaymentapp.presenter

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.masary.yassin.masarypaymentapp.*
import com.masary.yassin.masarypaymentapp.infrastructure.ConfigurationRepository
import com.masary.yassin.masarypaymentapp.infrastructure.MasCustomerInfoRepository
import com.masary.yassin.masarypaymentapp.infrastructure.MasaryRestServiceFactory
import com.masary.yassin.masarypaymentapp.models.Configuration
import com.masary.yassin.masarypaymentapp.models.CustomerInfo
import com.masary.yassin.masarypaymentapp.models.User
import com.masary.yassin.masarypaymentapp.models.exception.UnauthorizedException
import com.masary.yassin.masarypaymentapp.models.services.LoginService
import com.masary.yassin.masarypaymentapp.ui.merchantlogin.LoginContract
import com.masary.yassin.masarypaymentapp.ui.merchantlogin.LoginPresenter
import com.masary.yassin.masarypaymentapp.ui.util.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.robolectric.RuntimeEnvironment
import java.util.concurrent.TimeUnit

/**
 *Created by yassin on 8/1/18.
 */
@RunWith(Parameterized::class)
class LoginPresenterTest(private val setupTestParameter: SetupTestParameter) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): List<Array<*>> = listOf(arrayOf(object : SetupTestParameter {
            override fun setup(): TestParameter<User> {

                val sharedPreference: SharedPreferences = RuntimeEnvironment.application.getSharedPreferences(BuildConfig.KEY_PREFERENCE, Context.MODE_PRIVATE)

                val config = Configuration("", "58240051111110", "Android%2D%2D89014103211118510720", "Mobiwire")
                val jsonString = Gson().toJson(config)
                sharedPreference.edit().clear().apply()
                sharedPreference.edit().putString(BuildConfig.KEY_CONFIG, jsonString).apply()
                val configRepository = ConfigurationRepository(sharedPreference)

                val mockView = Mockito.mock(LoginContract.View::class.java)
                val masCustomerInfoRepository = MasCustomerInfoRepository(MasaryRestServiceFactory(BuildConfig.BASE_URL).service, configRepository)
                val loginService = LoginService(masCustomerInfoRepository)
                val loginPresenter = LoginPresenter(loginService, SchedulerProvider)
                loginPresenter.setView(mockView)

                val customerInfo = CustomerInfo(0, 1, 755424, 0, 755424, 8, 2,
                        "Y", "F", "01004605609", "Michael Jacoub", "مايكل يعقوب",
                        "N", "no", "دلوقتي اتصالات عملت اقوي شحنه في مصر من 5 لـ 25 جنيه تشحنها زي ما تحب رصيد، أو دقايق لكل الشبكات، أو ميكس لكل الشبكات (انترنت - دقايق - رسائل)")

                val validUserMap = hashMapOf(
                        User("مايكل يعقوب", USER_NAME, PASS) to customerInfo
                )

                val inValidUserMap = hashMapOf(
                        User("مينا", "885", "123456789") to UnauthorizedException("Invalid User ID or Password"),
                        User("مينا", USER_NAME_FAULT, PASS) to UnauthorizedException("In-Active Customer")
                )

                return object : TestParameter<User> {
                    override fun getCorrectUsers(): Set<User> = validUserMap.keys

                    override fun loginWithCorrectCredentialsTestCase(user: User) {
                        loginPresenter.login(user.username!!, user.password!!)
                        Mockito.verify(mockView).showLoading()
                        Mockito.verify(mockView).hideLoading()
                        Mockito.verify(mockView).showValid(ArgumentMatchers.eq(1))
                    }

                    override fun getInCorrectUsers(): Set<User> = inValidUserMap.keys

                    override fun loginWithICorrectCredentialsTestCase(user: User) {
                        loginPresenter.login(user.username!!, user.password!!)
                        Mockito.verify(mockView).showLoading()
                        Mockito.verify(mockView).hideLoading()
                        Mockito.verify(mockView).showError(inValidUserMap[user] as Throwable)
                    }
                }
            }
        }))

        override fun toString() = CustomerInfo::class.java.simpleName!!
    }

    @Test
    fun testloginWithCorrectCredentialsTestCase() {
        val testParameter = setupTestParameter.setup()
        val testObserver = TestObserver<Any>()
        Observable.fromIterable(testParameter.getInCorrectUsers()
                .map {
                    testParameter.loginWithCorrectCredentialsTestCase(it)
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
        val testParameter = setupTestParameter.setup()
        val testObserver = TestObserver<Any>()
        Observable.fromIterable(testParameter.getInCorrectUsers()
                .map {
                    testParameter.loginWithICorrectCredentialsTestCase(it)
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