package com.masary.yassin.masarypaymentapp.ui.merchantlogin

import com.masary.yassin.masarypaymentapp.ui.BasePresenter
import com.masary.yassin.masarypaymentapp.ui.BaseView

interface LoginContract {

    interface View : BaseView {
        fun showLoading()

        fun hideLoading()

        fun showError(error: Throwable)

        fun showValid(messageKey: Int)
    }

    interface Presenter : BasePresenter<View> {
        fun login(username: String, password: String)
    }
}