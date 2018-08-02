//package com.masary.yassin.masarypaymentapp.infrastructure.infrastructure
//
//import com.masary.yassin.masarypaymentapp.models.SignedToken
//import com.masary.yassin.masarypaymentapp.models.Pagination
//import io.reactivex.Observable
//import io.reactivex.observers.TestObserver
//import io.reactivex.schedulers.Schedulers
//import org.junit.Assert
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.ParameterizedRobolectricTestRunner
//import org.robolectric.annotation.Config
//import java.util.concurrent.TimeUnit
//
///**
// * Created by yassin on 07/27/18.
// * Test All PostRepository
// */
//@Config
//@RunWith(ParameterizedRobolectricTestRunner::class)
//class PostRepoUnitTest(private val setupTestParameter: SetupTestParameter<*>) {
//    companion object {
//        @JvmStatic
//        @ParameterizedRobolectricTestRunner.Parameters(name = "{index}: {0}")
//        fun data(): List<Array<*>> = listOf(
//                arrayOf(object : PostRepoUnitTest.SetupTestParameter<SignedToken>{
//                    override fun setup(): TestParameter<out SignedToken> {
//                        return object : PostRepoUnitTest.TestParameter<SignedToken>{
//                            override fun getDataForInsertionEntity(): Set<SignedToken> {
//                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                            }
//
//                            override fun insertNonExistingEntity(t: SignedToken): Triple<Observable<out List<SignedToken>>, SignedToken, List<SignedToken>> {
//                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                            }
//
//                            override fun insertDuplicateEntity(t: SignedToken): Triple<Observable<out List<SignedToken>>, SignedToken, Throwable> {
//                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                            }
//
//                            override fun checkInsert() {
//                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//                            }
//
//                        }
//                    }
//                    override fun toString() = SignedToken::class.java.simpleName!!
//                })
//        )
//    }
//
//    @Test
//    fun testInsertNonExistingEntityIntoRepository() {
//
////        val testParameter = setupTestParameter.setup()
////        val testObserver = TestObserver<Any>()
////        testParameter.insertNonExistingEntity()
////                .subscribeOn(Schedulers.io())
////                .subscribe(testObserver)
////
////        testObserver.awaitTerminalEvent(1, TimeUnit.MINUTES)
////        testObserver.assertResult(testParameter.data)
////        testParameter.checkInsert()
//
//        val testParameter = setupTestParameter.setup()
//
//        val testObserver = TestObserver<Triple<List<Any?>, *, List<Any?>>>()
//
//        Observable.fromIterable(testParameter.getDataForInsertionEntity()
//                .map {
//                    val triple = testParameter.insertNonExistingEntity(it as )
//                    triple.first.map { Triple(it, triple.second, triple.third) }
//                })
//                .flatMap { it }
//                .subscribeOn(Schedulers.io())
//                .subscribe(testObserver)
//
//
//        testObserver.awaitTerminalEvent(1, TimeUnit.MINUTES)
//        testObserver.assertSubscribed()
//                .assertNoErrors()
//                .assertComplete()
//
//        testObserver.values().forEach {
//            Assert.assertEquals(it.second.toString(), it.third, it.first)
//        }
//        testParameter.checkInsert()
//
//    }
//
//    interface TestParameter<T> {
//        fun getDataForInsertionEntity(): Set<T>
//        fun insertNonExistingEntity(t: T): Triple<Observable<out List<T>>, T, List<T>>
//        fun insertDuplicateEntity(t:T): Triple<Observable<out List<T>>, T, Throwable>
//        fun checkInsert()
//    }
//
//    interface SetupTestParameter<out T> {
//        fun setup(): TestParameter<out T>
//    }
//}