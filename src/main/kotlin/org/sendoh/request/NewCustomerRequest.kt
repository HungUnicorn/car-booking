package org.sendoh.request

import com.fasterxml.jackson.core.type.TypeReference
import org.sendoh.model.User
import java.math.BigDecimal

class NewCustomerRequest(val user: User, val balance: BigDecimal) {
    companion object {
        private val TYPE_REF: TypeReference<NewCustomerRequest> = object : TypeReference<NewCustomerRequest>() {}

        fun typeRef(): TypeReference<NewCustomerRequest> {
            return TYPE_REF
        }
    }
}
