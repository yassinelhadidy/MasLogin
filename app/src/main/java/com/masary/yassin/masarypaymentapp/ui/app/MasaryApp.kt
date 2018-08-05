package com.masary.yassin.masarypaymentapp.ui.app

import android.app.Application
import android.content.Context
import com.masary.yassin.masarypaymentapp.BuildConfig
import com.masary.yassin.masarypaymentapp.infrastructure.ConfigurationRepository
import com.masary.yassin.masarypaymentapp.infrastructure.MasaryRestService
import com.masary.yassin.masarypaymentapp.infrastructure.MasaryRestServiceFactory

/**
 *Created by yassin on 7/29/18.
 * we need make some instance as singleton over application.
 */

class MasaryApp : Application() {

    override fun onCreate() {
        super.onCreate()
        getServiceInstance()
        getConfigRepoInstance()
    }

    fun getServiceInstance(): MasaryRestService {
        return MasaryRestServiceFactory(BuildConfig.BASE_URL).service
    }

    fun getConfigRepoInstance(): ConfigurationRepository {
        return ConfigurationRepository(getSharedPreferences(BuildConfig.KEY_PREFERENCE, Context.MODE_PRIVATE))
    }
}