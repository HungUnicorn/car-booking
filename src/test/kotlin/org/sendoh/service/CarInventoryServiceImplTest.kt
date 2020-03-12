package org.sendoh.service

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.sendoh.dao.CarInventoryDao
import org.sendoh.model.CarInventory
import org.sendoh.model.Location
import java.math.BigDecimal

class CarInventoryServiceImplTest {

    @MockK
    lateinit var carInventoryDao: CarInventoryDao

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun listNearbyCars__givenCars__nearByCars() {
        val location = Location(0.000000, 0.000000)
        val carInventoryA1 = CarInventory("A1", Location(0.000000, 0.000000), BigDecimal.TEN)
        val carInventoryB2 = CarInventory("B2", Location(0.000000, 0.000001), BigDecimal.ONE)
        val carInventoryC3 = CarInventory("C3", Location(0.000000, 0.000003), BigDecimal.TEN)
        val carInventoryD4 = CarInventory("D4", Location(60.930432, 116.015625), BigDecimal.ZERO)

        every { carInventoryDao.getAll() } returns listOf(carInventoryA1, carInventoryB2, carInventoryC3,
                carInventoryD4)

        val carInventoryServiceImpl = CarInventoryServiceImpl(carInventoryDao)

        val listNearbyCars = carInventoryServiceImpl.listNearbyCars(location, 1000.0)
        val expected = listOf(carInventoryA1, carInventoryB2, carInventoryC3)

        assertEquals(expected, listNearbyCars)
    }
}
