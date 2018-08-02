package com.masary.yassin.masarypaymentapp.models

/**
 * Created by yassin on 07/27/18.
 * SupportTransaction
 */
interface SupportTransaction {
    fun <T> doInTransaction(operation: () -> T): T
}