package com.masary.yassin.masarypaymentapp.models.services

import com.masary.yassin.masarypaymentapp.infrastructure.MasCustomerInfoRepository
import com.masary.yassin.masarypaymentapp.models.CustomerInfo
import com.masary.yassin.masarypaymentapp.models.ModelException
import com.masary.yassin.masarypaymentapp.models.User
import com.masary.yassin.masarypaymentapp.models.exception.UnauthorizedException
import io.reactivex.Observable
import retrofit2.HttpException

class LoginService(private val masCustomerInfoRepository: MasCustomerInfoRepository) {
    fun login(username: String, password: String): Observable<out CustomerInfo> {
        return masCustomerInfoRepository.insert(User("", username, password))
                .map { it }
                .onErrorResumeNext { e: Throwable ->
                    if (e is HttpException && e.code() == 200) {
                        Observable.error(UnauthorizedException(e.message()))
                    } else {//FIXME : We need handle Domain layer Exception.
                        Observable.error(ModelException(e))
                    }
                }
    }
}