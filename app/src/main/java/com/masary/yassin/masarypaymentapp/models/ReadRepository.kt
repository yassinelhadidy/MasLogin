package com.masary.yassin.masarypaymentapp.models

/**
 * Created by yassin on 07/27/18.
 * Read Repository with Read Only Methods
 */
interface ReadRepository<out T> : GetRepository<T>, GetAllRepository<T>