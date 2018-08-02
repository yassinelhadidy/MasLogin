package com.masary.yassin.masarypaymentapp.ui.exception

import android.content.Context
import com.masary.yassin.masarypaymentapp.R
import com.masary.yassin.masarypaymentapp.models.exception.UnauthorizedException

/**
 * Factory used to create error messages from an Exception as a condition.
 */
class ErrorMessageFactory {
    companion object {
        fun create(context: Context, t: Throwable): String {
            var message = context.getString(R.string.exception_message_generic)

            when (t) {
                is UnauthorizedException -> {
                    message = if (t.message == "Invalid User ID or Password") context.getString(R.string.exception_message_user_not_found)
                    else context.getString(R.string.exception_message_user_inactive)
                }
            }
            return message
        }
    }
}
