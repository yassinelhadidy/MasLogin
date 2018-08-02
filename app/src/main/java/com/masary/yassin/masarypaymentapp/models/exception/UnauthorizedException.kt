package com.masary.yassin.masarypaymentapp.models.exception

import com.masary.yassin.masarypaymentapp.models.ModelException

class UnauthorizedException : ModelException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}