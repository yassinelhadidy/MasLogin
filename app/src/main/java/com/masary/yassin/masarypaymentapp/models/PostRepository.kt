package com.masary.yassin.masarypaymentapp.models

import io.reactivex.Observable

/**
 * Created by yassin on 07/27/18.
 * PostRepository
 */
interface PostRepository<in T, out U> {
    fun insert(entity: T): Observable<out U>
}