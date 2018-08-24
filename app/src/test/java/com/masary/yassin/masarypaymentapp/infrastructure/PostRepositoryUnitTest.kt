package com.masary.yassin.masarypaymentapp.infrastructure

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.masary.yassin.masarypaymentapp.BuildConfig
import com.masary.yassin.masarypaymentapp.infrastructure.dto.MasCustInfo.Companion.toMasCustInfo
import com.masary.yassin.masarypaymentapp.infrastructure.dto.MasCustLoginResponse
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
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

/**
 * Created by yassin on 07/27/18.
 * Test All PostRepository
 */
private const val DATA_ERROR = "Data Error!"

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

                        val userName = "8"
                        val pass = "123456789"

                        val mockMasaryRestService = Mockito.mock(MasaryRestService::class.java)
                        val sharedPreference: SharedPreferences = RuntimeEnvironment.application.getSharedPreferences(null, Context.MODE_PRIVATE)

                        val config = Configuration("", "58240051111110@@", "Android%2D%2D89014103211118510720@@", "Mobiwire@@")
                        val jsonString = Gson().toJson(config)
                        sharedPreference.edit().clear().apply()
                        sharedPreference.edit().putString(BuildConfig.KEY_CONFIG, jsonString).apply()
                        val configRepository = ConfigurationRepository(sharedPreference)

                        val validUserMap = hashMapOf(
                                User("مايكل يعقوب", userName, pass) to customerInfo
                        )

                        val inValidUserMap = hashMapOf(
                                User("مينا", "885", "123456789") to InfrastructureException("Invalid User ID or Password"),
                                User("مينا", "88", "123456789") to InfrastructureException("In-Active Customer")
                        )

                        return object : TestParameter<CustomerInfo> {
                            override fun getCorrectDataForInsertion(): Set<Any> = validUserMap.keys

                            override fun successfulInsertionTransaction(entity: Any): Triple<Observable<out CustomerInfo>, Any, CustomerInfo?> {
                                Mockito.`when`(mockMasaryRestService
                                        .login(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                                        .thenReturn(Observable.just(MasCustLoginResponse(listOf(validUserMap[entity]!!.toMasCustInfo()), "")))
                                val customerInfoRepository =
                                        MasCustomerInfoRepository(mockMasaryRestService, configRepository)
                                val observable = customerInfoRepository.insert(entity as User)
                                return Triple(observable, entity, validUserMap[entity])
                            }

                            override fun getInCorrectDataForInsertion(): Set<Any> = inValidUserMap.keys

                            override fun failureInsertionTransaction(entity: Any): Triple<Observable<out CustomerInfo>, Any, Throwable?> {
                                Mockito.`when`(mockMasaryRestService
                                        .login(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                                        .thenReturn(Observable.error(RuntimeException(DATA_ERROR)))
                                val customerInfoRepository =
                                        MasCustomerInfoRepository(mockMasaryRestService, configRepository)
                                val observable = customerInfoRepository.insert(entity as User)
                                return Triple(observable, entity, inValidUserMap[entity])
                            }

                            override fun checkInsert() {
                                Assert.assertEquals(customerInfo.id, 0)
                                Assert.assertEquals(customerInfo.groupId, 8)
                                Assert.assertEquals(customerInfo.arUserName, "مايكل يعقوب")
                            }
                        }
                    }

                    override fun toString() = MasCustomerInfoRepository::class.java.simpleName!!
                }),
                arrayOf(object : SetupTestParameter<Configuration> {
                    override fun setup(): TestParameter<Configuration> {
                        val sharedPreference: SharedPreferences = RuntimeEnvironment.application.getSharedPreferences(null, Context.MODE_PRIVATE)
                        val configRepository = ConfigurationRepository(sharedPreference)

                        val config = Configuration("1", "58240051111110@@", "Android%2D%2D89014103211118510720@@", "Mobiwire@@")
                        val configFault = Configuration("2", "58240051111110@@", "Android%2D%2D89014103211118510720@@", "Mobiwire@@")

                        val validUserMap = hashMapOf(
                                config to config
                        )
                        val inValidUserMap = hashMapOf(
                                configFault to InfrastructureException("Entity already exist")
                        )
                        return object : TestParameter<Configuration> {
                            override fun getCorrectDataForInsertion(): Set<Any> = validUserMap.keys

                            override fun successfulInsertionTransaction(entity: Any): Triple<Observable<out Configuration>, Any, Configuration?> {
                                val observable = configRepository.insert(entity as Configuration)

                                return Triple(observable, entity, validUserMap[entity])
                            }

                            override fun getInCorrectDataForInsertion(): Set<Any> = inValidUserMap.keys

                            override fun failureInsertionTransaction(entity: Any): Triple<Observable<out Configuration>, Any, Throwable?> {
                                sharedPreference.edit().clear().apply()
                                val observable1 = configRepository.insert(entity as Configuration)
                                val observable2 = configRepository.insert(entity)
                                return Triple(Observable.merge(observable1, observable2), entity, inValidUserMap[entity])
                            }

                            override fun checkInsert() {
                                val json = sharedPreference.getString(BuildConfig.KEY_CONFIG, null)
                                val loadedConfiguration = Gson().fromJson(json, Configuration::class.java)
                                Assert.assertEquals(config, loadedConfiguration)
                            }
                        }
                    }

                    override fun toString() = ConfigurationRepository::class.java.simpleName!!
                })
        )
    }

    @Test
    fun testInsertNonExistingEntityIntoRepository() {
        val testParameter = setupTestParameter.setup()
        val testObserver = TestObserver<Triple<Any?, Any, Any?>>()
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
        }
        testParameter.checkInsert()
    }

    @Test
    fun testInsertDuplicateEntityIntoRepository() {
        val testParameter = setupTestParameter.setup()
        val testObserver = TestObserver<Triple<Notification<out Any?>, Any, Throwable?>>()
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
            Assert.assertTrue(it.second.toString(), it.first.isOnError)
            Assert.assertTrue(it.second.toString(), it.first.error is InfrastructureException)
//            Assert.assertEquals(it.second.toString(),
//                    it.third?.message, it.first.error?.cause?.message)  //FIXME : initiate throwable object
        }
    }

    interface TestParameter<out T> {
        fun getCorrectDataForInsertion(): Set<Any>
        fun successfulInsertionTransaction(entity: Any): Triple<Observable<out T>, Any, T?>
        fun getInCorrectDataForInsertion(): Set<Any>
        fun failureInsertionTransaction(entity: Any): Triple<Observable<out T>, Any, Throwable?>
        fun checkInsert()
    }

    interface SetupTestParameter<out T> {
        fun setup(): TestParameter<T>
    }
}