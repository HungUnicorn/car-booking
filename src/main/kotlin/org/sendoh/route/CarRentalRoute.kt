package org.sendoh.route

import io.undertow.server.HttpServerExchange
import org.sendoh.web.json.JsonSerde.JsonSerdeException
import org.sendoh.web.ApiHandler
import org.sendoh.exception.CarRentalException
import org.sendoh.exchange.CarRentalExchange
import org.sendoh.service.CarRentalService

/**
 * Define how to handle car rental request and api response
 *
 * */
class CarRentalRoute {
    private var apiHandler: ApiHandler
    private var carRentalExchange: CarRentalExchange
    private var carRentalService: CarRentalService

    constructor(apiHandler: ApiHandler, carRentalExchange: CarRentalExchange,
                carRentalService: CarRentalService) {
        this.apiHandler = apiHandler
        this.carRentalExchange = carRentalExchange
        this.carRentalService = carRentalService
    }

    fun post(exchange: HttpServerExchange) {
        try {
            val request = carRentalExchange.post(exchange)
            val carRental = carRentalService.start(request)
            apiHandler.ok(exchange, carRental)
        } catch (e: JsonSerdeException) {
            apiHandler.badRequest(exchange, "Invalid rental with error: $e")
        } catch (e: CarRentalException) {
            apiHandler.badRequest(exchange, "Invalid rental with error: $e")
        }
    }

    fun delete(exchange: HttpServerExchange) {
        try {
            val request = carRentalExchange.delete(exchange)
            val carRental = carRentalService.stop(request)
            apiHandler.ok(exchange, carRental)
        } catch (e: JsonSerdeException) {
            apiHandler.badRequest(exchange, "Invalid rental with error: $e")
        } catch (e: CarRentalException) {
            apiHandler.badRequest(exchange, "Invalid rental with error: $e")
        }
    }

    fun get(exchange: HttpServerExchange) {
        val carLicenseNo = carRentalExchange.carLicenseNo(exchange)
        val rental = carRentalService.getRental(carLicenseNo)

        if (rental == null) {
            apiHandler.notFound(exchange, "Rent car $carLicenseNo not found")
            return
        }
        apiHandler.ok(exchange, rental)
    }

    fun listWithDistance(exchange: HttpServerExchange) {
        val locationWithDistance = carRentalExchange.locationWithDistance(exchange)

        val cars = carRentalService.listUnRentedNearbyCars(locationWithDistance.location,
                locationWithDistance.distanceInMeter.toDouble())

        if (cars.isEmpty()) {
            apiHandler.notFound(exchange, "No car to rent within ${locationWithDistance.distanceInMeter}m")
            return
        }
        apiHandler.ok(exchange, cars)
    }
}
