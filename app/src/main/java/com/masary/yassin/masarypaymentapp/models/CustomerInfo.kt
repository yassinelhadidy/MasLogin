package com.masary.yassin.masarypaymentapp.models

/**
 *Created by yassin on 7/29/18.
 */
data class CustomerInfo(val id: Int, val idParent: Int, val balance: Int, val showBalance: Int,
                        val currentBalance: Int, val groupId: Int, val roleId: Int,
                        val bolISActive: String, val bolIsAutoAllocate: String, val msisdn: String, val engUserName: String,
                        val arUserName: String, val canAddEmployee: String, val warningNot: String, val notificationNot: String)