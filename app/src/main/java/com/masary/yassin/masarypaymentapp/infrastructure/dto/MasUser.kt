package com.masary.yassin.masarypaymentapp.infrastructure.dto

import com.masary.yassin.masarypaymentapp.models.User

/**
 * Created by yassin on 07/27/18.
 * MasUser
 */
data class MasUser(val name: String,
                   val username: String?,
                   val password: String?) {
    companion object {
        fun User.toMasUser(): MasUser = MasUser(name,
                username,
                password)
    }

    fun toUser(): User {
        return User(name,
                username,
                password)
    }
}