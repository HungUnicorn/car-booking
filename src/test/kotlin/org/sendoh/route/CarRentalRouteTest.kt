package org.sendoh.route

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import io.undertow.server.HttpServerExchange
import org.junit.Before
import org.junit.Test
import org.sendoh.web.json.JsonSerde.JsonSerdeException
import org.sendoh.web.ApiHandler
import org.sendoh.exception.CarRentalException
import org.sendoh.model.*
import org.sendoh.exchange.CarRentalExchange
import org.sendoh.request.RentCarCustomerRequest
import org.sendoh.service.CarRentalService
import java.math.BigDecimal
import java.time.Instant

class CarRentalRouteTest {
    @RelaxedMockK
    lateinit var apiHandler: ApiHandler

    @MockK
    lateinit var carRentalExchange: CarRentalExchange

    @MockK
    lateinit var carRentalService: CarRentalService

    @RelaxedMockK
    lateinit var exchange: HttpServerExchange

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun post__givenJsonException__badRequest() {
        val request = RentCarCustomerRequest("john1", "A1", Instant.now())

        every { carRentalExchange.post(any()) } throws JsonSerdeException(Exception())

        val carRentalController = CarRentalRoute(apiHandler, carRentalExchange, carRentalService)

        carRentalController.post(exchange)

        verify(exactly = 0) { carRentalService.start(request) }
        verify { apiHandler.badRequest(exchange, "Invalid rental with error: org.sendoh.web.json.JsonSerde\$JsonSerdeException: java.lang.Exception") }
    }

    @Test
    fun post__givenCarRentalException__badRequest() {
        val request = RentCarCustomerRequest("john1", "A1", Instant.now())
        every { carRentalExchange.post(any()) } returns request
        every { carRentalService.start(any()) } throws CarRentalException("Test")

        val carRentalController = CarRentalRoute(apiHandler, carRentalExchange, carRentalService)

        carRentalController.post(exchange)

        verify { apiHandler.badRequest(exchange, "Invalid rental with error: org.sendoh.exception.CarRentalException: Test") }
    }

    @Test
    fun post__givenCarRental__ok() {
        val request = RentCarCustomerRequest("john1", "A1", Instant.now())
        val carRental = CarRental(request.customerId, CarInventory(request.carLicenseNo,
                Location(1.0, 1.0), BigDecimal.ONE), request.requestTime)

        every { carRentalExchange.post(any()) } returns request
        every { carRentalService.start(any()) } returns carRental

        val carRentalController = CarRentalRoute(apiHandler, carRentalExchange, carRentalService)

        carRentalController.post(exchange)

        verify { apiHandler.ok(exchange, carRental) }
    }

    @Test
    fun delete__givenJsonException__badRequest() {
        val request = RentCarCustomerRequest("john1", "A1", Instant.now())

        every { carRentalExchange.delete(any()) } throws JsonSerdeException(Exception())

        val carRentalController = CarRentalRoute(apiHandler, carRentalExchange, carRentalService)

        carRentalController.delete(exchange)

        verify(exactly = 0) { carRentalService.start(request) }
        verify { apiHandler.badRequest(exchange, "Invalid rental with error: org.sendoh.web.json.JsonSerde\$JsonSerdeException: java.lang.Exception") }
    }

    @Test
    fun delete__givenCarRentalException__badRequest() {
        val request = RentCarCustomerRequest("john1", "A1", Instant.now())
        every { carRentalExchange.delete(any()) } returns request
        every { carRentalService.stop(any()) } throws CarRentalException("Test")

        val carRentalController = CarRentalRoute(apiHandler, carRentalExchange, carRentalService)

        carRentalController.delete(exchange)

        verify { apiHandler.badRequest(exchange, "Invalid rental with error: org.sendoh.exception.CarRentalException: Test") }
    }

    @Test
    fun delete__givenRequest__ok() {
        val request = RentCarCustomerRequest("john1", "A1", Instant.now())
        val carRental = CarRental(request.customerId, CarInventory(request.carLicenseNo,
                Location(1.0, 1.0), BigDecimal.ONE), request.requestTime)
        val rentalBill = RentalBill(carRental, BigDecimal.TEN, request.requestTime)

        every { carRentalExchange.delete(any()) } returns request
        every { carRentalService.stop(any()) } returns rentalBill

        val carRentalController = CarRentalRoute(apiHandler, carRentalExchange, carRentalService)

        carRentalController.delete(exchange)

        verify { apiHandler.ok(exchange, rentalBill) }
    }

    @Test
    fun get__givenRentalExisted__ok() {
        val licenseNo = "A1"
        val request = RentCarCustomerRequest("john1", "A1", Instant.now())
        val carRental = CarRental(request.customerId, CarInventory(request.carLicenseNo,
                Location(1.0, 1.0), BigDecimal.ONE), request.requestTime)

        every { carRentalExchange.carLicenseNo(any()) } returns licenseNo
        every { carRentalService.getRental(licenseNo) } returns carRental

        val carRentalController = CarRentalRoute(apiHandler, carRentalExchange, carRentalService)

        carRentalController.get(exchange)

        verify {
            apiHandler.ok(exchange, carRental)
        }
    }

    @Test
    fun get__givenRentalNotExisted__notFound() {
        val licenseNo = "A1"
        val request = RentCarCustomerRequest("john1", "A1", Instant.now())
        val carRental = CarRental(request.customerId, CarInventory(request.carLicenseNo,
                Location(1.0, 1.0), BigDecimal.ONE), request.requestTime)

        every { carRentalExchange.carLicenseNo(any()) } returns licenseNo
        every { carRentalService.getRental(licenseNo) } returns null

        val carRentalController = CarRentalRoute(apiHandler, carRentalExchange, carRentalService)

        carRentalController.get(exchange)

        verify {
            apiHandler.notFound(exchange, "Rent car $licenseNo not found")
        }
    }

    @Test
    fun listWithDistance__givenNoCar__notFound() {
        val request = LocationWithDistance(Location(1.0, 1.0), 5500.0)

        every { carRentalExchange.locationWithDistance(any()) } returns request
        every { carRentalService.listUnRentedNearbyCars(request.location, request.distanceInMeter) } returns listOf()

        val carRentalController = CarRentalRoute(apiHandler, carRentalExchange, carRentalService)

        carRentalController.listWithDistance(exchange)

        verify {
            apiHandler.notFound(exchange, "No car to rent within ${request.distanceInMeter}m")

        }
    }

    @Test
    fun listWithDistance__givenCars__ok() {
        val request = LocationWithDistance(Location(1.0, 1.0), 5500.0)
        val carInventory = CarInventory("A1", Location(1.1, 1.1), BigDecimal.ONE)

        every { carRentalExchange.locationWithDistance(any()) } returns request
        every { carRentalService.listUnRentedNearbyCars(request.location, request.distanceInMeter) } returns listOf(carInventory)

        val carRentalController = CarRentalRoute(apiHandler, carRentalExchange, carRentalService)

        carRentalController.listWithDistance(exchange)

        verify {
            apiHandler.ok(exchange, listOf(carInventory))

        }
    }
}
