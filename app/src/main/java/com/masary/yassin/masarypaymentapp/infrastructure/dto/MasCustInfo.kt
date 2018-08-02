package com.masary.yassin.masarypaymentapp.infrastructure.dto
import com.masary.yassin.masarypaymentapp.models.CustomerInfo

/**
 *Created by yassin on 7/29/18.
 */
//FIXME : We need to make some field  to remove enough as business needs for masary android app.
data class MasCustInfo(val iDCUSTOMER: Int, val iD_PARENT: Int, val balance: Int, val show_MY_BALANCE: Int, val transfer_2_SAME: Int?,
                       val rep_ALLOWED_BAL: Int?, val current_BALANCE: Int, val reps_Count: Int?, val group_id: Int, val roleID: Int,
                       val SSO: Int?, val bol_IS_ACTIVE: String, val bol_IS_AUTO_ALLOCATE: String, val msisdn: String, val str_DISPLAY_NAME: String,
                       val str_DISPLAY_NAME_ARABIC: String, val can_ADD_EMPLOYEE: String, val warning_Not: String, val notification_Not: String) {
    companion object {
        fun CustomerInfo.toMasCustInfo(): MasCustInfo =
                MasCustInfo(id, idParent, balance, showBalance, null, null, currentBalance, null, groupId,
                        roleId,null, bolISActive, bolIsAutoAllocate, msisdn, engUserName, arUserName, canAddEmployee, warningNot, notificationNot)
    }

    fun toCustomerInfo(): CustomerInfo = CustomerInfo(iDCUSTOMER, iD_PARENT, balance, show_MY_BALANCE, current_BALANCE, group_id,
            roleID, bol_IS_ACTIVE, bol_IS_AUTO_ALLOCATE, msisdn, str_DISPLAY_NAME, str_DISPLAY_NAME_ARABIC, can_ADD_EMPLOYEE,
            warning_Not, notification_Not)
}