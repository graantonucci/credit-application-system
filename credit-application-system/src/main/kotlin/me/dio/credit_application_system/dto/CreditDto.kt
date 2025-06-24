package me.dio.credit_application_system.dto

import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotNull
import me.dio.credit_application_system.entity.Credit
import me.dio.credit_application_system.entity.Customer
import java.math.BigDecimal
import java.time.LocalDate

data class CreditDto(
    @field:NotNull(message = "invalid input") val creditValue: BigDecimal,
    @field:Future(message = "invalid date input")  val dayFirstOfInstallment: LocalDate,
    @field:Max(48) val numberOfInstallments: Int,
    @field:NotNull(message = "invalid input") val customerId: Long
) {
    fun toEntity(): Credit = Credit (
        creditValue = this.creditValue,
        dayFirstInstallment = this.dayFirstOfInstallment,
        numberOfInstallments = this.numberOfInstallments,
        customer = Customer(id = this.customerId)
    )

    fun isValidDate() : Boolean = dayFirstOfInstallment > LocalDate.now().plusMonths(3)
}
