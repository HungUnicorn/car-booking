package org.sendoh.request

import com.fasterxml.jackson.core.type.TypeReference
import java.time.Instant

class RentCarCustomerRequest(val customerId: String, val carLicenseNo: String, val requestTime: Instant) {
    companion object {
        private val TYPE_REF: TypeReference<RentCarCustomerRequest> = object : TypeReference<RentCarCustomerRequest>() {}

        fun typeRef(): TypeReference<RentCarCustomerRequest> {
            return TYPE_REF
        }
    }
}
