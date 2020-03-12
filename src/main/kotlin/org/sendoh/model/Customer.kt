package org.sendoh.model

import java.math.BigDecimal
import java.util.*

data class Customer(val user: User, val balance: BigDecimal) {
    val id: String get() = UUID.nameUUIDFromBytes(user.email.toByteArray()).toString()

    override fun equals(other: Any?) = (other is Customer)
                && id == other.id

    override fun hashCode(): Int {
        return id.hashCode()
    }
}
