package com.masary.yassin.masarypaymentapp.ui.util

import android.content.Context
import cn.pedant.SweetAlert.SweetAlertDialog
import com.masary.yassin.masarypaymentapp.R


/**
 * Created by yassin on 20/05/18.
 *
 */
open class ErrorDialog {

    companion object {
        fun show(context: Context, message: String) {
            val sweetAlertDialog = SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
            sweetAlertDialog.setTitleText(context.getString(R.string.title_failure))
                    .setContentText(message)
                    .setConfirmClickListener {
                        sweetAlertDialog.dismissWithAnimation()
                    }.show()
        }
    }
}