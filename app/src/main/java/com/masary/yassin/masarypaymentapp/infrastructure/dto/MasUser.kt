package com.masary.yassin.masarypaymentapp.infrastructure.dto

import com.masary.yassin.masarypaymentapp.models.User

/**
 * Created by yassin on 07/27/18.
 * MasUser
 */
data class MasUser(val name: String,
                   val username: String?,
                   val password: String?,
                   val deviceType: String) {
    companion object {
        fun User.toSgUser(): MasUser = MasUser(name,
                username,
                password,deviceType)
    }

    fun toUser(): User {
        return User(name,
                username,
                password,deviceType)
    }
}