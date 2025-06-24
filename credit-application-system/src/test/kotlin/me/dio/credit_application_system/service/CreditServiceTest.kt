package me.dio.credit_application_system.service

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import me.dio.credit_application_system.entity.Address
import me.dio.credit_application_system.entity.Credit
import me.dio.credit_application_system.entity.Customer
import me.dio.credit_application_system.enummeration.Status
import me.dio.credit_application_system.exception.BusinessException
import me.dio.credit_application_system.repository.CreditRepository
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

@ActiveProfiles("test")
@ExtendWith(MockKExtension::class)
class CreditServiceTest {
    @MockK lateinit var creditRepository: CreditRepository
    @MockK lateinit var customerService: CustomerService
    @InjectMockKs lateinit var creditService: CreditService

    @Test
    fun `should create credit`(){
        //given
        val fakeCredit: Credit = buildCredit()
        every {creditRepository.save(any())} returns fakeCredit
        every {customerService.findById(any())} returns mockk<Customer>()

        //when
        val actual = creditService.save(fakeCredit)

        //then
        Assertions.assertThat(actual).isNotNull
        Assertions.assertThat(actual).isSameAs(fakeCredit)
        verify(exactly = 1) {creditRepository.save(fakeCredit)}
        verify(exactly = 1) {customerService.findById(any())}
    }

    @Test
    fun `should findAllBy customer by id`(){
        //given
        val fakeId: Long = Random().nextLong()
        val fakeCredit  = buildCredit()
        val fakeListCredit: List<Credit> = listOf(fakeCredit)
        every { creditRepository.findAllByCustomerId(fakeId)} returns fakeListCredit
        //when
        val actual = creditService.findAllByCustomerId(fakeId)
        //then
        Assertions.assertThat(actual).isSameAs(fakeListCredit)
        verify(exactly = 1) { creditRepository.findAllByCustomerId(fakeId)}
    }

    @Test
    fun `should not find customer by invalid id and throw BusinessException`(){
        //given
        val fakeCustomerId: Long = 1L
        val fakeId: UUID = UUID.randomUUID()
        every { creditRepository.findByCreditCode(fakeId) } returns null
        //when

        //then
        Assertions.assertThatExceptionOfType(BusinessException::class.java)
            .isThrownBy { creditService.findByCreditCode(fakeCustomerId, fakeId.toString()) }
            .withMessage("Credit code $fakeId not found")
    }

    private fun buildCustomer(
        firstName: String = "Cami",
        lastName: String = "Cavalcante",
        cpf: String = "28475934625",
        email: String = "camila@gmail.com",
        password: String = "12345",
        zipCode: String = "12345",
        street: String = "Rua da Cami",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        id: Long = 1L
    ) = Customer(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        password = password,
        address = Address(
            zipCode = zipCode,
            street = street,
        ),
        income = income,
        id = id
    )

    private fun buildCredit(
        creditCode: UUID = UUID.randomUUID(),
        creditValue: BigDecimal = BigDecimal.TEN,
        dayFirstInstallment: LocalDate = LocalDate.now(),
        numberOfInstallments: Int = 0,
        status: Status = Status.IN_PROGRESS,
        customer: Customer =  buildCustomer(),
        id: Long = 1L
    ) = Credit(
        creditCode = creditCode,
        creditValue = creditValue,
        dayFirstInstallment = dayFirstInstallment,
        numberOfInstallments = numberOfInstallments,
        status = status,
        customer = customer,
        id = id,
    )
}