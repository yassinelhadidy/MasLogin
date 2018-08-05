package com.masary.yassin.masarypaymentapp.models

import com.masary.yassin.masarypaymentapp.BuildConfig

/**
 * Created by yassin on 03/05/18.
 *
 */
private const val MACHINE_CONFIGURATIN = "@@ip_address=@@machine_id=358240051111110@@imsi=Android%2D%2D89014103211118510720@@device_type=Mobiwire@@sw_version=92@@browser=172.16.10.180%2FMobile%2DWS%2DClient@@platform=Android@@"

data class Configuration(val ipAdd: String, val iMiEI: String, val iMSI: String, val deviceType: String) {
    fun machineConfiguration(): String {
        return "@@ip_address=" + ipAdd + "@@machine_id=" + iMiEI + "@@imsi=" + iMSI + "@@device_type=" + deviceType + "@@sw_version=" +
                BuildConfig.MWS_VERSION + "@@browser=" + BuildConfig.BASE_URL + "@@platform=" + BuildConfig.PLATFORM
    }

    constructor() :this("","","","")
}