package me.dio.credit_application_system.exception

data class BusinessException(override val message: String? ) : RuntimeException(message) {

}
