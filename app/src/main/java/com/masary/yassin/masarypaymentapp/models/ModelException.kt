package com.masary.yassin.masarypaymentapp.models

/**
 * Created by yassin on 07/27/18.
 * InfrastructureException
 */
open class ModelException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}