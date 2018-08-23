package com.masary.yassin.masarypaymentapp.ui.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.support.design.widget.Snackbar
import android.telephony.TelephonyManager
import android.text.format.Formatter
import android.view.View
import android.widget.TextView
import com.masary.yassin.masarypaymentapp.models.Configuration

/**
 *Created by yassin on 6/13/18.
 */
class Helper {
    companion object {
        fun isThereInternetConnection(context: Context): Boolean {
            val isConnected: Boolean
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting
            return isConnected
        }

        fun showSnackBar(view: View, message: String) {
            val snackbar = Snackbar
                    .make(view, message, Snackbar.LENGTH_LONG)

            // Changing message text color
            snackbar.setActionTextColor(Color.RED)
            // Changing action button text color
            val sbView = snackbar.view
            val textView = sbView.findViewById<View>(android.support.design.R.id.snackbar_text) as TextView
            textView.setTextColor(Color.YELLOW)
            snackbar.show()
        }

        @SuppressLint("HardwareIds")
        fun getImeisAndIpAdd(context: Context): Configuration? {
            if (context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                val mngr = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                val iMEI = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mngr.imei
                } else {
                    mngr.deviceId
                }
                val iMSI = mngr.simOperatorName.trim { it <= ' ' } + "--" + mngr.simSerialNumber.trim { it <= ' ' }
                val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                //FIXME: IS Deprecated  and lint errors.
                val iPAddress = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
                return if (iMEI != null && iPAddress != null) {
                    Configuration(iMEI, iMSI, iPAddress, mngr.phoneType.toString())
                } else {
                    null
                }
            }
            return null
        }
    }
}