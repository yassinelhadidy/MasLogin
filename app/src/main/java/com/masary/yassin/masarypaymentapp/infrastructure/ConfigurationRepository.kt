package com.masary.yassin.masarypaymentapp.infrastructure

import android.content.SharedPreferences
import com.google.gson.Gson
import com.masary.yassin.masarypaymentapp.BuildConfig
import com.masary.yassin.masarypaymentapp.models.Configuration
import com.masary.yassin.masarypaymentapp.models.GetRepository
import com.masary.yassin.masarypaymentapp.models.WriteRepository
import io.reactivex.Observable

/**
 *Created by yassin on 8/3/18.
 */
//  FIXME : We need to store meta data by mobile data store however this is not best implementation maybe must use android keyStore .
class ConfigurationRepository(private val sharedPreferences: SharedPreferences) : WriteRepository<Configuration, Configuration>, GetRepository<Configuration> {
    override fun delete(id: Int): Observable<Unit> = Observable.fromCallable {
        val json = sharedPreferences.getString(BuildConfig.KEY_CONFIG, null)
        val config = Gson().fromJson(json, Configuration::class.java)
        if (config != null) {
            sharedPreferences.edit().remove(BuildConfig.KEY_CONFIG).apply()
        } else {
            throw InfrastructureException("Entity may not be null")
        }

    }

    override fun update(entity: Configuration): Observable<Unit> = Observable.fromCallable {
        val jsonString = Gson().toJson(entity)
        sharedPreferences.edit().putString(BuildConfig.KEY_CONFIG, jsonString).apply()
    }

    override fun get(id: Int): Observable<out Configuration> = Observable.fromCallable {
        val json = sharedPreferences.getString(BuildConfig.KEY_CONFIG, null)
        Gson().fromJson(json, Configuration::class.java)
    }.onErrorResumeNext { throwable: Throwable ->
        when (throwable) {
            is NullPointerException -> Observable.empty<Configuration>()
            else -> Observable.error(InfrastructureException(RuntimeException("Data Error!")))
        }
    }

    override fun insert(entity: Configuration): Observable<out Configuration> = Observable.fromCallable {
        val json = sharedPreferences.getString(BuildConfig.KEY_CONFIG, null)
        val config = Gson().fromJson(json, Configuration::class.java)
        if (entity != config) {
            val jsonString = Gson().toJson(entity)
            sharedPreferences.edit().putString(BuildConfig.KEY_CONFIG, jsonString).apply()
            entity
        } else {
            throw InfrastructureException("Entity already exist")
        }
    }
}