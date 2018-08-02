package com.masary.yassin.masarypaymentapp.ui.app

import android.app.Application
import com.masary.yassin.masarypaymentapp.infrastructure.MasaryRestService
import com.masary.yassin.masarypaymentapp.infrastructure.MasaryRestServiceFactory

/**
 *Created by yassin on 7/29/18.
 * we need make some instance as singleton over application.
 */
private const val IP_ADD = "172.16.10.180"
private const val BASE_URL = "http://$IP_ADD/Mobile-WS-Client/"

class MasaryApp : Application() {

    override fun onCreate() {
        super.onCreate()
        getServiceInstance()
    }

    fun getServiceInstance(): MasaryRestService {
        return MasaryRestServiceFactory(BASE_URL).service
    }
}