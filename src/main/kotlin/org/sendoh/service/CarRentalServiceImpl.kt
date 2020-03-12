package org.sendoh.service

import org.sendoh.dao.CarRentalDao
import org.sendoh.exception.CarRentalException
import org.sendoh.model.*
import org.sendoh.request.RentCarCustomerRequest
import java.math.BigDecimal
import java.time.Duration
import java.time.Instant

class CarRentalServiceImpl : CarRentalService {
    private var carRentalDao: CarRentalDao
    private var customerService: CustomerService
    private var carInventoryService: CarInventoryService

    constructor(carRentalDao: CarRentalDao, customerService: CustomerService,
                carInventoryService: CarInventoryService) {
        this.carRentalDao = carRentalDao
        this.customerService = customerService
        this.carInventoryService = carInventoryService
    }

    override fun listUnRentedNearbyCars(location: Location, distanceInMeter: Double): List<CarInventory> {
        return carInventoryService.listNearbyCars(location, distanceInMeter)
                .filter { !isCarAlreadyRented(it.licenseNo) }
                .toList()
    }

    override fun start(rentCarCustomerRequest: RentCarCustomerRequest): CarRental {
        if (!customerService.canRent(rentCarCustomerRequest.customerId)) {
            throw CarRentalException("Customer cannot rent: ${rentCarCustomerRequest.customerId}")
        }

        val carInventory = carInventoryService.get(rentCarCustomerRequest.carLicenseNo)
                ?: throw CarRentalException("Car is not registered: ${rentCarCustomerRequest.carLicenseNo}")

        if (!isCarAlreadyRented(rentCarCustomerRequest.carLicenseNo)) {
            val carRental = CarRental(rentCarCustomerRequest.customerId, carInventory, rentCarCustomerRequest.requestTime)
            carRentalDao.putIfAbsent(rentCarCustomerRequest.carLicenseNo, carRental)
            return carRental
        }
        throw CarRentalException("Car is already rented: ${rentCarCustomerRequest.carLicenseNo}")
    }

    override fun getRental(licenseNo: String): CarRental? {
        return carRentalDao.get(licenseNo)
    }

    override fun stop(rentCarCustomerRequest: RentCarCustomerRequest): RentalBill {
        val carRental = carRentalDao.get(rentCarCustomerRequest.carLicenseNo)
                ?: throw CarRentalException("Car is not rented: ${rentCarCustomerRequest.carLicenseNo}")
        if (carRental.customerId != rentCarCustomerRequest.customerId) {
            throw CarRentalException("Customer ${rentCarCustomerRequest.customerId} hasn't rented car ${rentCarCustomerRequest.carLicenseNo}")
        }

        val rent = calculateRent(carRental, rentCarCustomerRequest.requestTime)

        customerService.payRent(rentCarCustomerRequest.customerId, rent)
        carRentalDao.remove(rentCarCustomerRequest.carLicenseNo)

        return RentalBill(carRental, rent, rentCarCustomerRequest.requestTime)
    }

    private fun isCarAlreadyRented(carLicenseNo: String) = carRentalDao.contains(carLicenseNo)

    private fun calculateRent(carRental: CarRental, rentTime:Instant) = carRental.carInventory.rentPerMinute
            .times(BigDecimal
                    .valueOf(Duration
                            .between(carRental.rentTime, rentTime)
                            .toMinutes()))
}
