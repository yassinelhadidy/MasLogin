package com.masary.yassin.masarypaymentapp.infrastructure

import com.masary.yassin.masarypaymentapp.infrastructure.dto.MasCustLoginResponse
import io.reactivex.Observable
import retrofit2.http.*

//private const val X_AUTH_TOKEN = "x-auth-token"

interface MasaryRestService {
    @Headers("Content-Type: application/json")
    @GET("Switch")
    fun login( @Query("par1") port: String?,
               @Query("par2") userName: String?,
               @Query("par3") password: String?,
               @Query("par4") machineConfing: String?): Observable<MasCustLoginResponse>
}