package org.sendoh.model

import java.math.BigDecimal
import java.time.Instant

data class RentalBill(val carRental: CarRental, val rent: BigDecimal, val rentStopTime: Instant)
