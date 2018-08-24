package com.masary.yassin.masarypaymentapp.ui.merchantlogin

import android.app.ProgressDialog
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.masary.yassin.masarypaymentapp.R
import com.masary.yassin.masarypaymentapp.infrastructure.ConfigurationRepository
import com.masary.yassin.masarypaymentapp.infrastructure.MasCustomerInfoRepository
import com.masary.yassin.masarypaymentapp.models.services.LoginService
import com.masary.yassin.masarypaymentapp.ui.app.MasaryApp
import com.masary.yassin.masarypaymentapp.ui.exception.ErrorMessageFactory
import com.masary.yassin.masarypaymentapp.ui.helpers.Helper
import com.masary.yassin.masarypaymentapp.ui.helpers.Helper.Companion.isThereInternetConnection
import com.masary.yassin.masarypaymentapp.ui.helpers.Helper.Companion.showSnackBar
import com.masary.yassin.masarypaymentapp.ui.util.SchedulerProvider

open class LoginActivity : AppCompatActivity(), LoginContract.View {
    //FIXME: We need to use Injection Factory.
    private lateinit var loginPresenter: LoginContract.Presenter
    //FIXME: I use this dialog temporary however is deprecated due to sweetDialog currently bug https://github.com/pedant/sweet-alert-dialog.
    private lateinit var progressDialog: ProgressDialog
    private lateinit var loginConstraint: View

    override fun initView() {
        progressDialog = ProgressDialog(this)
//        progressDialog.progressHelper.barColor = Color.parseColor("#A5DC86")
        loginConstraint = findViewById<View>(R.id.login_layout)
        val etUsername = findViewById<EditText>(R.id.et_user_name)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        btnLogin.setOnClickListener {
            loginUser(etUsername.text.toString(), etPassword.text.toString())
        }
    }

    private fun loginUser(userName: String, pass: String) {
        if (!isThereInternetConnection(this)) {
            showSnackBar(loginConstraint, this.getString(R.string.exception_message_no_connection))
        } else {
            if (userName.isEmpty() || pass.isEmpty()) {
                showSnackBar(loginConstraint, this.getString(R.string.empty_login_fields))
            } else {
                loginPresenter.login(userName, pass)
            }
        }
    }

    override fun showLoading() {
//        progressDialog.titleText = "Loading"
//        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading")
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    override fun hideLoading() {
        progressDialog.dismiss()
    }

    override fun showError(error: Throwable) {
        val errorMessage = ErrorMessageFactory.create(this, error)
        showSnackBar(loginConstraint, errorMessage)
    }

    override fun showValid(messageKey: Int) {
        when (messageKey) {
            1 -> {
                showSnackBar(loginConstraint, this.getString(R.string.auth_user))
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        initPresenter()
        loginPresenter.setView(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initPresenter() {
        //FIXME: Create instance of presenter
        val masaryRestService = (applicationContext as MasaryApp).getServiceInstance()
        val masCustomerInfoRepository = MasCustomerInfoRepository(masaryRestService, getDeviceConfiguration())
        loginPresenter = LoginPresenter(LoginService(masCustomerInfoRepository), SchedulerProvider)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDeviceConfiguration(): ConfigurationRepository {
        val configurationRepository = (applicationContext as MasaryApp).getConfigRepoInstance()
        return if (Helper.getImeisAndIpAdd(this) != null) {
            val configuration = Helper.getImeisAndIpAdd(this)
            configurationRepository.insert(configuration!!)
            configurationRepository
        } else {
            configurationRepository
        }
    }
}