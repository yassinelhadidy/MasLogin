package com.masary.yassin.masarypaymentapp.infrastructure

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.masary.yassin.masarypaymentapp.*
import com.masary.yassin.masarypaymentapp.models.Configuration
import com.masary.yassin.masarypaymentapp.models.CustomerInfo
import com.masary.yassin.masarypaymentapp.models.User
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.robolectric.RuntimeEnvironment
import java.util.concurrent.TimeUnit

/**
 *Created by yassin on 7/29/18.
 */
@RunWith(Parameterized::class)
class PostRepositoryTest(private val setupTestParameter: SetupTestParameter<*>) {

    companion object {
        @Suppress("unused")
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): List<Array<*>> = listOf(arrayOf(object : SetupTestParameter<CustomerInfo> {
            override fun setup(): TestParameter<CustomerInfo> {
                val customerInfo = CustomerInfo(0, 1, 755424, 0, 755424, 8, 2,
                        "Y", "F", "01004605609", "Michael Jacoub", "مايكل يعقوب",
                        "N", "no", "دلوقتي اتصالات عملت اقوي شحنه في مصر من 5 لـ 25 جنيه تشحنها زي ما تحب رصيد، أو دقايق لكل الشبكات، أو ميكس لكل الشبكات (انترنت - دقايق - رسائل)")
                val sharedPreference: SharedPreferences = RuntimeEnvironment.application.getSharedPreferences(BuildConfig.KEY_PREFERENCE, Context.MODE_PRIVATE)

                val config = Configuration("", "58240051111110", "Android%2D%2D89014103211118510720", "Mobiwire")
                val jsonString = Gson().toJson(config)
                sharedPreference.edit().clear().apply()
                sharedPreference.edit().putString(BuildConfig.KEY_CONFIG, jsonString).apply()
                val configRepository = ConfigurationRepository(sharedPreference)

                val customerInfoRepository = MasCustomerInfoRepository(MasaryRestServiceFactory(BuildConfig.BASE_URL).service, configRepository)
                val validUserMap = hashMapOf(
                        User("مايكل يعقوب", "87", "123456789") to customerInfo
                )

                val inValidUserMap = hashMapOf(
                        User("مينا", "885", "123456789") to InfrastructureException("Invalid User ID or Password"),
                        User("مينا", "88", "123456789") to InfrastructureException("In-Active Customer")
                )

                return object : TestParameter<CustomerInfo> {
                    override fun getCorrectDataForInsertion(): Set<User> = validUserMap.keys

                    override fun successfulInsertionTransaction(user: User): Triple<Observable<out CustomerInfo>, User, CustomerInfo?> {
                        val observable = customerInfoRepository.insert(User("", USER_NAME, PASS))
                        return Triple(observable, user, validUserMap[user])
                    }

                    override fun getInCorrectDataForInsertion(): Set<User> = inValidUserMap.keys

                    override fun failureInsertionTransaction(user: User): Triple<Observable<out CustomerInfo>, User, Throwable?> {
                        val observable = customerInfoRepository.insert(User("", USER_NAME_FAULT, PASS))
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