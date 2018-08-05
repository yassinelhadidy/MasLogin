package com.masary.yassin.masarypaymentapp.ui.merchantlogin

import com.masary.yassin.masarypaymentapp.models.services.LoginService
import com.masary.yassin.masarypaymentapp.ui.util.BaseSchedulerProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class LoginPresenter(private val loginService: LoginService, private val scheduleProvider: BaseSchedulerProvider) : LoginContract.Presenter {

    private lateinit var view: LoginContract.View

    override fun login(username: String, password: String, device: String) {
        view.showLoading()
        loginService.login(username, password, device)
                .subscribeOn(scheduleProvider.io())
                .observeOn(scheduleProvider.ui())
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