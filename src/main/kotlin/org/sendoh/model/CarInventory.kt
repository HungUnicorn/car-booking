package org.sendoh.model

import java.math.BigDecimal

data class CarInventory(val licenseNo: String, val location: Location, val rentPerMinute: BigDecimal)

data class Location(val lat: Double, val lng: Double)
