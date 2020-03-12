package org.sendoh.service

import org.apache.lucene.util.SloppyMath
import org.sendoh.dao.CarInventoryDao
import org.sendoh.model.CarInventory
import org.sendoh.model.Location
import java.math.BigDecimal

class CarInventoryServiceImpl : CarInventoryService {
    private val carInventoryDao: CarInventoryDao

    constructor(carInventoryDao: CarInventoryDao) {
        this.carInventoryDao = carInventoryDao
    }

    override fun listNearbyCars(location: Location, distanceInMeter: Double): List<CarInventory> {
        return carInventoryDao.getAll()
                .filter { calculateDistanceInMeter(location, it.location) < distanceInMeter }
                .toList()
    }

    override fun get(licenseNo: String): CarInventory? {
        return carInventoryDao.get(licenseNo)
    }

    private fun calculateDistanceInMeter(it: Location, other: Location): Double = SloppyMath.haversinMeters(it.lat, other.lng,
            other.lat, other.lng)
}
