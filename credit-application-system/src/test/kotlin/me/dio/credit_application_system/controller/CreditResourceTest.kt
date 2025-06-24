package me.dio.credit_application_system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import me.dio.credit_application_system.dto.CreditDto
import me.dio.credit_application_system.dto.CustomerDto
import me.dio.credit_application_system.repository.CreditRepository
import me.dio.credit_application_system.repository.CustomerRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Month
import java.util.*


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CreditResourceTest {
    @Autowired
    private lateinit var creditRepository: CreditRepository
    @Autowired private lateinit var customerRepository: CustomerRepository
    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/credits"
    }

    @BeforeEach
    fun setup() {
        customerRepository.deleteAll()
        creditRepository.deleteAll()
        mockkStatic(UUID::class)
    }
    @AfterEach
    fun tearDown() {
        customerRepository.deleteAll()
        creditRepository.deleteAll()
        unmockkStatic(UUID::class)
    }
    @Test
    fun `should create a credit and return 201 status`(){
        //given
        val customer = customerRepository.save(builderCustomerDto().toEntity())
        val creditDto: CreditDto = builderCreditDto(customerId = customer?.id!!)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        val uuidExpected = UUID.randomUUID()
        every { UUID.randomUUID() } returns uuidExpected

        //then
            mockMvc.perform(
                MockMvcRequestBuilders.post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valueAsString))
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.content().string("Credit ${uuidExpected.toString()} - Customer ${customer?.firstName} saved!"))
    }

    @Test
    fun `should not save a credit with dayFirstOfInstallment is now and return 400 status`(){
        //given
        val customer = customerRepository.save(builderCustomerDto().toEntity())
        val creditDto: CreditDto = builderCreditDto(dayFirstOfInstallment = LocalDate.now(), customerId =  customer?.id!!)
        val valueAsString: String = objectMapper.writeValueAsString(creditDto)
        //when
        mockMvc.perform(MockMvcRequestBuilders.post(CustomerResourceTest.URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `should find customer by id and return 200 status`(){
        //given
        val customer = customerRepository.save(builderCustomerDto().toEntity())
        val credit = creditRepository.save(builderCreditDto(customerId = customer?.id!! ).toEntity())

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.get(URL)
            .param("customerId", customer?.id!!.toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)

    }

    @Test
    fun `should not find customer with invalid id and return 400 status`(){
        //given
        val invalidId: Long = 2L

        mockMvc.perform(MockMvcRequestBuilders.get(URL)
            .param("customerId", invalidId.toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)

    }

    @Test
    fun `should find code and customer by id and return 200 status`(){
        //given
        val customer = customerRepository.save(builderCustomerDto().toEntity())
        val credit = creditRepository.save(builderCreditDto(customerId = customer?.id!! ).toEntity())
        every { UUID.fromString(any()) } returns credit.creditCode

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${credit.creditCode}")
            .param("customerId", customer?.id!!.toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)

    }

    @Test
    fun `should find code by id but customerId not the same and return 5xx status`(){
        //given
        val customer = customerRepository.save(builderCustomerDto().toEntity())
        val credit = creditRepository.save(builderCreditDto(customerId = customer?.id!! ).toEntity())
        every { UUID.fromString(any()) } returns credit.creditCode

        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${credit.creditCode}")
            .param("customerId", "anyId")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)

    }

    @Test
    fun `should not find creditCode with invalid id and return 400 status`(){
        //given
        val invalidId: Long = 2L
        val myGuid: UUID = UUID.randomUUID()
        every { UUID.fromString(any()) } returns myGuid

        mockMvc.perform(MockMvcRequestBuilders.get("$URL/$myGuid")
            .param("customerId", invalidId.toString())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)

    }

    private fun builderCreditDto(
        creditValue: BigDecimal = BigDecimal.TEN,
        dayFirstOfInstallment: LocalDate = LocalDate.of(2025, Month.DECEMBER, 10),
        numberOfInstallments: Int = 0,
        customerId: Long = 1L
    ) = CreditDto(
        creditValue = creditValue,
        dayFirstOfInstallment = dayFirstOfInstallment,
        numberOfInstallments = numberOfInstallments,
        customerId = customerId
    )

    private fun builderCustomerDto(
        firstName: String = "Cami",
        lastName: String = "Cavalcante",
        cpf: String = "37315670084",
        email: String = "camila@gmail.com",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        password: String = "1234",
        zipCode: String = "000000",
        street: String = "Rua da Cami, 123",
    ) = CustomerDto(
        firstName = firstName,
        lastName = lastName,
        cpf = cpf,
        email = email,
        income = income,
        password = password,
        zipCode = zipCode,
        street = street
    )

}