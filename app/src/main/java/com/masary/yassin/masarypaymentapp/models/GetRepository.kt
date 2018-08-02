package com.masary.yassin.masarypaymentapp.models

import io.reactivex.Observable

/**
 * Created by yassin on 07/27/18.
 * GetRepository
 */
interface GetRepository<out T> {
    fun get(id: Int): Observable<out T>
}