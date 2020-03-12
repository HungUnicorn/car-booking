package org.sendoh.model

import java.time.Instant

data class CarRental(val customerId: String, val carInventory: CarInventory, val rentTime: Instant)
