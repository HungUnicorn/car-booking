package org.sendoh.service

import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.sendoh.dao.CarRentalDao
import org.sendoh.exception.CarRentalException
import org.sendoh.model.*
import org.sendoh.request.RentCarCustomerRequest
import java.math.BigDecimal
import java.time.Instant
import java.time.temporal.ChronoUnit

class CarRentalServiceImplTest {

    @MockK
    lateinit var carRentalDao: CarRentalDao

    @MockK
    lateinit var customerService: CustomerService

    @MockK
    lateinit var carInventoryService: CarInventoryService

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun listUnRentedNearbyCars() {
        val location = Location(0.000000, 0.000000)
        val carInventoryA1 = CarInventory("A1", Location(0.000000, 0.000000), BigDecimal.TEN)
        val carInventoryB2 = CarInventory("B2", Location(0.000000, 0.000001), BigDecimal.ONE)
        val carInventoryC3 = CarInventory("C3", Location(0.000000, 0.000001), BigDecimal.TEN)

        every { carInventoryService.listNearbyCars(any(), any()) } returns listOf(carInventoryA1, carInventoryB2, carInventoryC3)
        every { carRentalDao.contains("A1") } returns false
        every { carRentalDao.contains("B2") } returns true
        every { carRentalDao.contains("C3") } returns false

        val carRentalService = CarRentalServiceImpl(carRentalDao, customerService, carInventoryService)

        val listUnRentedNearbyCars = carRentalService.listUnRentedNearbyCars(location, 1000.0)

        assertEquals(listOf(carInventoryA1, carInventoryC3), listUnRentedNearbyCars)
    }

    @Test
    fun startRent__givenCustomerCanRentAndAlreadyRentedCar__save() {
        val carInventory = CarInventory("A1", Location(0.000000, 0.000000), BigDecimal.TEN)
        val rentCarCustomer = RentCarCustomerRequest("banachi", carInventory.licenseNo, Instant.now())
        val expectedCarRental = CarRental(rentCarCustomer.customerId, carInventory, rentCarCustomer.requestTime)

        every { customerService.canRent(any()) } returns true
        every { carInventoryService.get(any()) } returns carInventory
        every { carRentalDao.contains("A1") } returns false
        every { carRentalDao.putIfAbsent(carInventory.licenseNo, expectedCarRental) } just runs

        val carRentalService = CarRentalServiceImpl(carRentalDao, customerService, carInventoryService)
        val startRent = carRentalService.start(rentCarCustomer)

        verify(exactly = 1) {
            carRentalDao.putIfAbsent(rentCarCustomer.carLicenseNo, expectedCarRental)
        }
        assertEquals(expectedCarRental, startRent)
    }

    @Test(expected = CarRentalException::class)
    fun startRent__givenCustomerCannotRent__rentException() {
        val carInventory = CarInventory("A1", Location(0.000000, 0.000000), BigDecimal.TEN)
        val rentCarCustomer = RentCarCustomerRequest("banachi", carInventory.licenseNo, Instant.now())

        every { customerService.canRent(any()) } returns false
        every { carRentalDao.contains(any()) } returns true

        val carRentalService = CarRentalServiceImpl(carRentalDao, customerService, carInventoryService)
        carRentalService.start(rentCarCustomer)
    }

    @Test(expected = CarRentalException::class)
    fun startRent__givenNotExistedCar__rentException() {
        val carInventory = CarInventory("A1", Location(0.000000, 0.000000), BigDecimal.TEN)
        val rentCarCustomer = RentCarCustomerRequest("banachi", carInventory.licenseNo, Instant.now())

        every { customerService.canRent(any()) } returns true
        every { carInventoryService.get(any()) } returns null

        val carRentalService = CarRentalServiceImpl(carRentalDao, customerService, carInventoryService)
        carRentalService.start(rentCarCustomer)
    }

    @Test(expected = CarRentalException::class)
    fun startRent__givenAlreadyRentedCar__rentException() {
        val carInventory = CarInventory("A1", Location(0.000000, 0.000000), BigDecimal.TEN)
        val customerToRentCar = RentCarCustomerRequest("banachi", carInventory.licenseNo, Instant.now())

        every { customerService.canRent(any()) } returns true
        every { carInventoryService.get(any()) } returns carInventory
        every { carRentalDao.contains(any()) } returns true

        val carRentalService = CarRentalServiceImpl(carRentalDao, customerService, carInventoryService)
        carRentalService.start(customerToRentCar)
    }

    @Test
    fun getRentalDetail() {
    }

    @Test
    fun stopRent__givenValidRentCarCustomer__payRentAndRemoveFromDao() {
        val carInventory = CarInventory("A1", Location(0.000000, 0.000000), BigDecimal.TEN)
        val startRentTime = Instant.now()
        val minutes = 5L
        val rentCarCustomer = RentCarCustomerRequest("banachi", carInventory.licenseNo,
                startRentTime.plus(minutes, ChronoUnit.MINUTES))
        val carRental = CarRental(rentCarCustomer.customerId, carInventory, startRentTime)
        val rent = carInventory.rentPerMinute.times(BigDecimal.valueOf(minutes))

        every { carRentalDao.get(carInventory.licenseNo) } returns carRental
        every { customerService.payRent(rentCarCustomer.customerId, rent) } just runs
        every { carRentalDao.remove(carInventory.licenseNo) } just runs

        val carRentalService = CarRentalServiceImpl(carRentalDao, customerService, carInventoryService)
        val stopRent = carRentalService.stop(rentCarCustomer)

        verify(exactly = 1) {
            customerService.payRent(rentCarCustomer.customerId, rent)
            carRentalDao.remove(rentCarCustomer.carLicenseNo)
        }
        assertEquals(RentalBill(carRental, rent, rentCarCustomer.requestTime), stopRent)
    }

    @Test(expected = CarRentalException::class)
    fun stopRent__givenInValidRentCarLicenseNo__rentException() {
        val carInventory = CarInventory("A1", Location(0.000000, 0.000000), BigDecimal.TEN)
        val rentTime = Instant.now()
        val minutes = 5L
        val rentCarCustomer = RentCarCustomerRequest("banachi", carInventory.licenseNo,
                rentTime.plus(minutes, ChronoUnit.MINUTES))

        every { carRentalDao.get(any()) } returns null

        val carRentalService = CarRentalServiceImpl(carRentalDao, customerService, carInventoryService)
        carRentalService.stop(rentCarCustomer)
    }

    @Test(expected = CarRentalException::class)
    fun stopRent__givenInValidRentCarCustomerId__rentException() {
        val carInventory = CarInventory("A1", Location(0.000000, 0.000000), BigDecimal.TEN)
        val rentTime = Instant.now()
        val minutes = 5L
        val rentCarCustomer = RentCarCustomerRequest("banachi", carInventory.licenseNo,
                rentTime.plus(minutes, ChronoUnit.MINUTES))
        val carRental = CarRental("King", carInventory, rentTime)

        every { carRentalDao.get(carInventory.licenseNo) } returns carRental

        val carRentalService = CarRentalServiceImpl(carRentalDao, customerService, carInventoryService)
        carRentalService.stop(rentCarCustomer)
    }
}
