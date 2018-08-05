package com.masary.yassin.masarypaymentapp.infrastructure

import android.content.Context
import android.content.SharedPreferences
import android.support.test.InstrumentationRegistry
import com.google.gson.Gson
import com.masary.yassin.masarypaymentapp.BuildConfig
import com.masary.yassin.masarypaymentapp.models.Configuration
import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.util.concurrent.TimeUnit

private const val DATA_ERROR = "Data Error!"

/**
 * Created by yassin on 7/20/18.
 * Test All GetRepository
 */
@RunWith(Parameterized::class)
class GetRepositoryTest(private val setupTestParameter: SetupTestParameter<*>) {
    companion object {
        @Suppress("unused")
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): List<Array<*>> = listOf(
                arrayOf(object : SetupTestParameter<Configuration> {
                    override fun setup(): TestParameter<Configuration> {
                        val sharedPreference: SharedPreferences = InstrumentationRegistry.getContext().getSharedPreferences(BuildConfig.KEY_PREFERENCE, Context.MODE_PRIVATE)
                        val config = listOf(
                                Configuration("", "582400789110@@", "Android%2D%2D89014103211118510720@@", "Mobiwire@@"),
                                Configuration("", "54465878@@78@@", "Android%2D%2D89014103211118510720@@", "Android@@"),
                                Configuration("", "88787888xsa8@@", "Android%2D%2D89041032111184510720@@", "Mobiwire@@"),
                                Configuration("", "582400541110@@", "Android%2D%2D89014103211118510720@@", "Android@@")
                        )
                        val jsonString = Gson().toJson(config[0])
                        sharedPreference.edit().putString(BuildConfig.KEY_CONFIG, jsonString).apply()
                        val configRepository = ConfigurationRepository(sharedPreference)


                        val validUserMap = hashMapOf(
                                1 to config[0],
                                2 to Configuration()
                        )
                        val inValidUserMap = hashMapOf(
                                10 to InfrastructureException(DATA_ERROR)
                        )
                        return object : TestParameter<Configuration> {
                            override fun getCorrectData(): Set<Int> = validUserMap.keys

                            override fun successfulTransaction(id: Int): Triple<Observable<out Configuration>, Int, Configuration?> {
                                return when (id) {
                                    1 -> {
                                        Triple(configRepository.get(id), id, validUserMap[id])
                                    }
                                    else -> {
                                        sharedPreference.edit().clear().apply()
                                        Triple(configRepository.get(id), id, validUserMap[id])
                                    }
                                }
                            }

                            override fun getInCorrectDataForThrowException(): Set<Int> = inValidUserMap.keys

                            override fun failureTransaction(id: Int): Triple<Observable<out Configuration>, Int, Throwable?> {
                                sharedPreference.edit().putInt(BuildConfig.KEY_CONFIG, 0).apply()
                                return Triple(configRepository.get(id), id, inValidUserMap[id])
                            }
                        }
                    }

                    override fun toString(): String = ConfigurationRepository::class.java.simpleName!!
                }))
    }

    @Test
    fun testGetNonOrExistingEntityFromRepository() {

        val testParameter = setupTestParameter.setup()
        val testObserver = TestObserver<Triple<Any?, Int, Any?>>()
        Observable.fromIterable(testParameter.getCorrectData()
                .map {
                    val triple = testParameter.successfulTransaction(it)
                    triple.first.map { Triple(it, triple.second, triple.third) }
                })
                .flatMap { it }
                .subscribeOn(Schedulers.io())
                .subscribe(testObserver)

        testObserver.awaitTerminalEvent(1, TimeUnit.MINUTES)
        testObserver.assertSubscribed()
                .assertNoErrors()
                .assertComplete()
        testObserver.values().forEach {
            Assert.assertEquals(it.second.toString(), it.third, it.first)
        }
    }

    @Test
    fun testGetEntityFromRepositoryWithException() {

        val testParameter = setupTestParameter.setup()
        val testObserver = TestObserver<Triple<Notification<out Any?>, Int, Throwable?>>()
        Observable.fromIterable(testParameter.getInCorrectDataForThrowException()
                .map {
                    val triple = testParameter.failureTransaction(it)
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
            Assert.assertEquals(it.second.toString(),
                    it.third?.message, it.first.error?.cause?.message)
        }

    }


    interface TestParameter<out T> {
        fun getCorrectData(): Set<Int>
        fun successfulTransaction(id: Int): Triple<Observable<out T>, Int, T?>
        fun getInCorrectDataForThrowException(): Set<Int>
        fun failureTransaction(id: Int): Triple<Observable<out T>, Int, Throwable?>
    }

    interface SetupTestParameter<out T> {
        fun setup(): TestParameter<T>
    }
}