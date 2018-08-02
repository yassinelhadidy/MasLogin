package com.masary.yassin.masarypaymentapp.ui.merchantlogin

import com.masary.yassin.masarypaymentapp.models.services.LoginService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginPresenter(private val loginService: LoginService) : LoginContract.Presenter {

    private lateinit var view: LoginContract.View

    override fun login(username: String, password: String, device: String) {
        view.showLoading()
        loginService.login(username, password, device)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.hideLoading()
                    view.showValid(1)
                }
                        , { e ->
                    view.hideLoading()
                    view.showError(e)

                })
    }

    override fun setView(view: LoginContract.View) {
        this.view = view
    }

    override fun subscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun unsubscribe() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}