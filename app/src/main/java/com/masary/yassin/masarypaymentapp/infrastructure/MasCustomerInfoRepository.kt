package com.masary.yassin.masarypaymentapp.infrastructure

import com.masary.yassin.masarypaymentapp.models.CustomerInfo
import com.masary.yassin.masarypaymentapp.models.PostRepository
import com.masary.yassin.masarypaymentapp.models.User
import io.reactivex.Observable

/**
 *Created by yassin on 7/29/18.
 */
//FIXME : We need remove this config fields from infrastructure layer and put in common config class generated from gradle script for security.
private const val MACHINE_CONFIGURATIN = "@@ip_address=@@machine_id=358240051111110@@imsi=Android%2D%2D89014103211118510720@@device_type=Mobiwire@@sw_version=92@@browser=172.16.10.180%2FMobile%2DWS%2DClient@@platform=Android@@"
private const val URL_PORT = "49"

class MasCustomerInfoRepository(private val masaryRestService: MasaryRestService) : PostRepository<User, CustomerInfo> {
    override fun insert(entity: User): Observable<out CustomerInfo> {
        return masaryRestService.login(URL_PORT, entity.username, entity.password, MACHINE_CONFIGURATIN)
                .map {
                    it.custInfo[0].toCustomerInfo()
                }
                .onErrorResumeNext { e: Throwable ->
                    Observable.error(InfrastructureException(e))//FIXME : We need know and handle masary Backend Exception.
                }
    }
}