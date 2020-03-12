package org.sendoh.dao

import org.sendoh.model.CarRental

interface CarRentalDao {
    fun getCarLicenseNumbers(): Set<String>

    fun get(carLicenseNo: String): CarRental?
    fun putIfAbsent(carLicenseNo: String, carRental: CarRental)
    fun remove(carLicenseNo: String)
    fun contains(carLicenseNo: String): Boolean
}
