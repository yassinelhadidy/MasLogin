package com.masary.yassin.masarypaymentapp.infrastructure.dto

/**
 * Created by yassin on 07/27/18.
 * MasErrorResult
 */
data class MasErrorResult(val type: String,
                          val errors: Map<String, List<String>>?)