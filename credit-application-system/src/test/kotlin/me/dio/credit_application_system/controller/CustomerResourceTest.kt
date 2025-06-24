package me.dio.credit_application_system.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.dio.credit_application_system.dto.CustomerDto
import me.dio.credit_application_system.dto.CustomerUpdateDto
import me.dio.credit_application_system.entity.Customer
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.math.BigDecimal
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ContextConfiguration
class CustomerResourceTest {
    @Autowired private lateinit var customerRepository: CustomerRepository
    @Autowired private lateinit var mockMvc: MockMvc
    @Autowired private lateinit var objectMapper: ObjectMapper

    companion object {
        const val URL: String = "/api/customers"
    }

    @BeforeEach fun setup() = customerRepository.deleteAll()
    @AfterEach fun tearDown() = customerRepository.deleteAll()

    @Test
    fun `should create a customer and return 201 status`(){
        //given
        val customerDto: CustomerDto = builderCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)

        //when
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Cami"))
    }

    @Test
    fun `should not save a customer with same CPF and return 409 status`(){
        //given
        customerRepository.save(builderCustomerDto().toEntity())
        val customerDto: CustomerDto = builderCustomerDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        //when
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isConflict)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `should not save a customer with firstName empty and return 400 status`(){
        //given
        val customerDto: CustomerDto = builderCustomerDto(firstName = "")
        val valueAsString: String = objectMapper.writeValueAsString(customerDto)
        //when
        mockMvc.perform(MockMvcRequestBuilders.post(URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().is5xxServerError)
    }

    @Test
    fun `should find customer by id and return 200 status`(){
        //given
        val customer = customerRepository.save(builderCustomerDto().toEntity())
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${customer.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk)

    }

    @Test
    fun `should not find customer with invalid id and return 400 status`(){
        //given
        val invalidId: Long = 2L

        mockMvc.perform(MockMvcRequestBuilders.get("$URL/${invalidId}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)

    }

    @Test
    fun `should delete customer by id`(){
        //given
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.delete("$URL/${customer.id}")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent)

    }

    @Test
    fun `should update a customer and return 200`(){
        //given
        val customer: Customer = customerRepository.save(builderCustomerDto().toEntity())
        val customerUpdateDto: CustomerUpdateDto = builderCustomerUpdateDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDto)
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch("$URL?customerId=${customer.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.firstName").value("Grazi"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.lastName").value("Muniz"))
    }

    @Test
    fun `should not update a customer with invalid id and return 400 status`(){
        //given
        val invalidId: Long = Random().nextLong()
        val customerUpdateDto: CustomerUpdateDto = builderCustomerUpdateDto()
        val valueAsString: String = objectMapper.writeValueAsString(customerUpdateDto)
        //when
        //then
        mockMvc.perform(MockMvcRequestBuilders.patch("$URL?customerId=${invalidId}")
            .contentType(MediaType.APPLICATION_JSON)
            .content(valueAsString))
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

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

    private fun builderCustomerUpdateDto(
        firstName: String = "Grazi",
        lastName: String = "Muniz",
        income: BigDecimal = BigDecimal.valueOf(1000.0),
        zipCode: String = "000000",
        street: String = "Rua da Grazi, 123",
    ) = CustomerUpdateDto(
        firstName = firstName,
        lastName = lastName,
        income = income,
        zipCode = zipCode,
        street = street
    )


}