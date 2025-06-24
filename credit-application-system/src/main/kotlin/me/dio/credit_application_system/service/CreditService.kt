package me.dio.credit_application_system.service

import me.dio.credit_application_system.entity.Credit
import me.dio.credit_application_system.exception.BusinessException
import me.dio.credit_application_system.repository.CreditRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreditService(
    private val creditRepository: CreditRepository,
    private val customerService: CustomerService
): ICreditService {
    override fun save(credit: Credit): Credit {
        credit.apply {
            customer = customerService.findById(credit.customer?.id!!)
        }
        return this.creditRepository.save(credit)
    }

    override fun findAllByCustomerId(customerId: Long): List<Credit>
    = this.creditRepository.findAllByCustomerId(customerId).let {
        if (it.isNullOrEmpty()) throw BusinessException("Id $customerId not found")
        else it
    }

    override fun findByCreditCode(customerId: Long, creditCode: String): Credit {
        val credit: Credit = this.creditRepository.findByCreditCode(UUID.fromString(creditCode))
            ?: throw BusinessException("Credit code $creditCode not found")
        return if (credit.customer?.id == customerId) credit else
            throw IllegalArgumentException("Contact admin")
    }
}