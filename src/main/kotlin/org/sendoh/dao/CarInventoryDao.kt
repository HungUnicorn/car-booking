package org.sendoh.dao

import org.sendoh.model.CarInventory

interface CarInventoryDao {
    fun getAll(): Collection<CarInventory>
    fun get(licenseNo: String): CarInventory?
}
