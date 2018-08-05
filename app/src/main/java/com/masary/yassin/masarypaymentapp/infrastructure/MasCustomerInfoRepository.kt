package com.masary.yassin.masarypaymentapp.infrastructure

import com.masary.yassin.masarypaymentapp.models.CustomerInfo
import com.masary.yassin.masarypaymentapp.models.PostRepository
import com.masary.yassin.masarypaymentapp.models.User
import io.reactivex.Observable
import retrofit2.HttpException

/**
 *Created by yassin on 7/29/18.
 */

class MasCustomerInfoRepository(private val masaryRestService: MasaryRestService,private val configurationRepository: ConfigurationRepository) : PostRepository<User, CustomerInfo> {
    override fun insert(entity: User): Observable<out CustomerInfo> {
    return configurationRepository.get(11).flatMap {
        configuration ->
        masaryRestService.login(userName =  entity.username,password =  entity.password,machineConfing =  configuration.machineConfiguration())
                .map {
                    it.custInfo[0].toCustomerInfo()
                } .onErrorResumeNext { e: Throwable ->
                    if (e is HttpException && e.response().code() == 404) {
                        Observable.empty()
                    } else {
                        // FIXME: We need to Handle Masary errors.
                        Observable.error(InfrastructureException(e))
                    }
                }
    }
    }
}