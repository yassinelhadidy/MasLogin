package com.masary.yassin.masarypaymentapp.models

import io.reactivex.Observable

/**
 * Created by yassin on 07/27/18.
 * Write Repository with Write Only Methods
 */
interface WriteRepository<in T, out U> : PostRepository<T, U>,
        PutRepository<T>, DeleteRepository {

    /* A naive implementation for insertOrUpdate that should
     * be enhanced in subclasses
     */
    fun insertOrUpdate(entity: T): Observable<out U> {
        return insert(entity)
                .map { it }
                .onErrorResumeNext(update(entity).map { _ ->
                    @Suppress("UNCHECKED_CAST")
                    entity as U
                })
    }

}