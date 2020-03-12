package org.sendoh

import org.sendoh.web.ApiHandler
import org.sendoh.dao.InMemoryCarInventoryDao
import org.sendoh.dao.InMemoryCarRentalDao
import org.sendoh.dao.InMemoryCustomerDao
import org.sendoh.exchange.CarRentalExchange
import org.sendoh.exchange.CustomerExchange
import org.sendoh.route.CarRentalRoute
import org.sendoh.route.CustomerRoute
import org.sendoh.service.CarInventoryServiceImpl
import org.sendoh.service.CarRentalServiceImpl
import org.sendoh.service.CustomerServiceImpl

/**
 * Inject implementation to interface
 *
 * */
object Injector {
    val customerService = CustomerServiceImpl(InMemoryCustomerDao())

    val carRentalService = CarRentalServiceImpl(InMemoryCarRentalDao(), customerService,
            CarInventoryServiceImpl(InMemoryCarInventoryDao()))

    val apiHandler = ApiHandler()
    val customerRoute = CustomerRoute(apiHandler, CustomerExchange(),
            customerService)

    val carRentalRoute = CarRentalRoute(apiHandler, CarRentalExchange(), carRentalService)
}
