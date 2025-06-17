package me.dio.credit_application_system.service

import me.dio.credit_application_system.entity.Customer

interface ICustomerService {
    fun save(customer: Customer): Customer
    fun findById(id: Long): Customer
    fun delete(id: Long): Unit
}