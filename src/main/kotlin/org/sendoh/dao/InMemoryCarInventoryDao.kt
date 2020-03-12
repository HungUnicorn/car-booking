package org.sendoh.dao

import org.sendoh.model.CarInventory
import org.sendoh.model.Location
import java.math.BigDecimal

class InMemoryCarInventoryDao : CarInventoryDao {
    private val storage: Map<String, CarInventory>

    init {
        val carForRentA1 = CarInventory("A1", Location(0.000000, 0.000000), BigDecimal.TEN)
        val carForRentB2 = CarInventory("B2", Location(0.000000, 0.000001), BigDecimal.ONE)
        val carForRentC3 = CarInventory("C3", Location(0.000000, 0.000003), BigDecimal.TEN)
        val carForRentD4 = CarInventory("D4", Location(60.930432, 116.015625), BigDecimal.ZERO)

        storage = hashMapOf(Pair(carForRentA1.licenseNo, carForRentA1),
                Pair(carForRentB2.licenseNo, carForRentB2),
                Pair(carForRentC3.licenseNo, carForRentC3),
                Pair(carForRentD4.licenseNo, carForRentD4))
    }
    override fun getAll(): Collection<CarInventory> {
        return storage.values
    }

    override fun get(licenseNo: String): CarInventory? {
        return storage[licenseNo]
    }
}
