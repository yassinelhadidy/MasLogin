package com.masary.yassin.masarypaymentapp.models

import io.reactivex.Observable

/**
 * Created by yassin on 07/27/18.
 * DeleteRepository
 */
interface DeleteRepository {
    fun delete(id: Int): Observable<Unit>
}