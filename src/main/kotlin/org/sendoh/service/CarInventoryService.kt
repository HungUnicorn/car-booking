package org.sendoh.service

import org.sendoh.model.CarInventory
import org.sendoh.model.Location

/**
 * Provide features for car, identified by the licenseNo
 */
interface CarInventoryService {
    /**
     * list all cars that given a location within distance in meters
     */
    fun listNearbyCars(location: Location, distanceInMeter: Double = 1000.0): List<CarInventory>

    /**
     * find a car given licenseNo
     */
    fun get(licenseNo: String): CarInventory?
}
