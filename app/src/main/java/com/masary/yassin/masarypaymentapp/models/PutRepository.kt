package com.masary.yassin.masarypaymentapp.models

import io.reactivex.Observable

/**
 * Created by yassin on 07/27/18.
 * PutRepository
 */
interface PutRepository<in T> {
    fun update(entity: T): Observable<Unit>
}