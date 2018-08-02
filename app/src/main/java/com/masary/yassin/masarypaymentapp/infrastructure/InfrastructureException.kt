package com.masary.yassin.masarypaymentapp.infrastructure

/**
 * Created by yassin on 07/27/18.
 * InfrastructureException
 */
class InfrastructureException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}