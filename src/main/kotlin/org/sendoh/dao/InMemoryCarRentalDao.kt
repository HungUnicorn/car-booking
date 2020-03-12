package org.sendoh.dao

import org.sendoh.model.CarInventory
import org.sendoh.model.CarRental
import org.sendoh.model.Location
import org.sendoh.request.RentCarCustomerRequest
import java.math.BigDecimal
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class InMemoryCarRentalDao : CarRentalDao {
    private val storage: ConcurrentMap<String, CarRental> = ConcurrentHashMap()

    init {
        val customerId = "f662a1bf-50ca-30f8-baa1-165eb6716c98"
        val rentCarCustomer = RentCarCustomerRequest(customerId, "A1", Instant.now())
        val carRental = CarRental(customerId,
                CarInventory("A1", Location(0.000000, 0.000000), BigDecimal.TEN),
                Instant.now())

        storage.putIfAbsent(rentCarCustomer.carLicenseNo, carRental)
    }


    override fun getCarLicenseNumbers(): Set<String> {
        return storage.keys
                .toSet()
    }

    override fun get(carLicenseNo: String): CarRental? {
        return storage[carLicenseNo]
    }

    override fun contains(carLicenseNo: String): Boolean {
        return storage.containsKey(carLicenseNo)
    }

    override fun putIfAbsent(carLicenseNo: String, carRental: CarRental) {
        storage.putIfAbsent(carLicenseNo, carRental)
    }

    override fun remove(carLicenseNo: String) {
        storage.remove(carLicenseNo)
    }
}
