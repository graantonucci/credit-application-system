package me.dio.credit_application_system.repository

import me.dio.credit_application_system.entity.Address
import me.dio.credit_application_system.entity.Credit
import me.dio.credit_application_system.entity.Customer
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.*

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CreditRepositoryTest {
    @Autowired lateinit var creditRepository: CreditRepository
    @Autowired lateinit var testEntityManager: TestEntityManager

    private lateinit var customer: Customer
    private lateinit var credit: Credit

    @BeforeEach fun setup(){
        customer = testEntityManager.persist(buildCustomer())
        credit = testEntityManager.persist(buildCredit(customer = customer))
    }

    @Test
    fun `should find credit by credit code`(){
        //given
        val creditCode = UUID.fromString("aa547c0f-9a6a-451f-8c89-afddce916a29")
        credit.creditCode = creditCode
        //when
        val fakeCredit: Credit = creditRepository.findByCreditCode(creditCode)!!
        //then
        Assertions.assertThat(fakeCredit).isNotNull
    }

    @Test
    fun `should find all credits by customer id`(){
        //given
        val customerId: Long = 1L
        //when
        val creditList: List<Credit> = creditRepository.findAllByCustomerId(customerId)
        //then
        Assertions.assertThat(creditList).isNotEmpty
    }

    private fun buildCredit(
        creditValue: BigDecimal = BigDecimal.valueOf(1000),
        dayFirstInstallment: LocalDate = LocalDate.of(2025, Month.APRIL, 22),
        numberOfInstallments: Int = 5,
        customer: Customer
    ): Credit = Credit(
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        customer = customer
    )

    private fun buildCustomer(
        firstName: String = "Cami",
        lastName: String = "Cavalcante",
        cpf: String = "28475934623",
        email: String = "camila@gmail.com",
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua da Cami",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
    ) = Customer (
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street
        ),
        income = income,
    )

}