package com.masary.yassin.masarypaymentapp.models

import io.reactivex.Observable

/**
 * Created by yassin on 07/27/18.
 * GetAllRepository
 */
interface GetAllRepository<out T> {
    fun getAll(pagination: Pagination): Observable<out List<T>>
}