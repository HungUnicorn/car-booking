package org.sendoh.service

import org.sendoh.model.*
import org.sendoh.request.RentCarCustomerRequest

/**
 * Provide features for customer to rent a car. A customer can only stop renting a car after already rent a car.
 */
interface CarRentalService {
    /**
     * Find all not rented cars within distance in meters given a location
     */
    fun listUnRentedNearbyCars(location: Location, distanceInMeter: Double = 1000.0): List<CarInventory>

    /**
     * Start to rent a car. A customer can only rent a car that exists and not rented yet
     * Customer cannot rent a car without registration or cannot pay the rent
     */
    fun start(rentCarCustomerRequest: RentCarCustomerRequest): CarRental

    /**
     * Get rental by license number of car
     */
    fun getRental(licenseNo: String): CarRental?

    /**
     * Customer stops renting the car and pays the rent.
     * Can only stop if the customer has rented the car and customer exists.
     * Return the rental detail for customer and CRM to check
     */
    fun stop(rentCarCustomerRequest: RentCarCustomerRequest): RentalBill
}
