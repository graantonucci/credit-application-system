package me.dio.credit_application_system.controller

import jakarta.validation.Valid
import me.dio.credit_application_system.dto.CreditDto
import me.dio.credit_application_system.dto.CreditView
import me.dio.credit_application_system.dto.CreditViewList
import me.dio.credit_application_system.entity.Credit
import me.dio.credit_application_system.service.CreditService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.stream.Collectors

@RestController
@RequestMapping("/api/credits")
class CreditResource(
    private val creditService: CreditService
){
    @PostMapping
    fun saveCredit(@RequestBody @Valid creditDto: CreditDto) : ResponseEntity<String> {
        if (creditDto.isValidDate().not())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Credit ${creditDto.dayFirstOfInstallment} is less then 3 months to now")

        val credit: Credit =  this.creditService.save(creditDto.toEntity())
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Credit ${credit.creditCode} - Customer ${credit.customer?.firstName} saved!")
    }

    @GetMapping
    fun findAllByCustomerId(@RequestParam(value = "customerId") customerId: Long): ResponseEntity<List<CreditViewList>> {
        val creditViewList: List<CreditViewList> = this.creditService.findAllByCustomerId(customerId).stream().map {
            credit: Credit -> CreditViewList(credit)
        }.collect(Collectors.toList())
        return ResponseEntity.status(HttpStatus.OK).body(creditViewList)
    }

    @GetMapping("/{creditCode}")
    fun findByCreditCode(@RequestParam(value = "customerId") customerId: Long,
                         @PathVariable creditCode: String) : ResponseEntity<CreditView> {
        val credit: Credit = this.creditService.findByCreditCode(customerId, creditCode)
        return ResponseEntity.status(HttpStatus.OK).body(CreditView(credit))
    }
}
