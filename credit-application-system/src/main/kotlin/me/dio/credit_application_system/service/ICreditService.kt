package me.dio.credit_application_system.service

import me.dio.credit_application_system.entity.Credit

interface ICreditService {
    fun save(credit: Credit): Credit
    fun findAllByCustomerId(customerId: Long): List<Credit>
    fun findByCreditCode(customerId: Long, creditCode: String): Credit
}