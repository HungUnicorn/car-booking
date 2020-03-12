package org.sendoh.route

import io.undertow.server.HttpServerExchange
import org.sendoh.web.exchange.Exchange
import org.sendoh.web.json.JsonSerde.JsonSerdeException
import org.sendoh.web.ApiHandler
import org.sendoh.exception.CarRentalException
import org.sendoh.model.Customer
import org.sendoh.request.NewCustomerRequest
import org.sendoh.exchange.CustomerExchange
import org.sendoh.service.CustomerService

/**
 * Define how to handle customer request and api response
 *
 * */
class CustomerRoute {
    private var apiHandler: ApiHandler
    private var customerExchange: CustomerExchange
    private var customerService: CustomerService

    constructor(apiHandler: ApiHandler, customerExchange: CustomerExchange,
                customerService: CustomerService) {
        this.customerExchange = customerExchange
        this.customerService = customerService
        this.apiHandler = apiHandler
    }

    fun post(exchange: HttpServerExchange) {
        try {
            val newCustomerRequest: NewCustomerRequest = customerExchange.newCustomer(exchange)
            val uuid = customerService.register(newCustomerRequest)
            apiHandler.ok(exchange, uuid)
        } catch (e: JsonSerdeException) {
            apiHandler.badRequest(exchange, "Invalid customer with error: $e")
        } catch (e: CarRentalException) {
            apiHandler.badRequest(exchange, "Invalid customer with error: $e")
        }
    }

    fun get(exchange: HttpServerExchange) {
        val uuid: String = customerExchange.uuid(exchange)
        val customer: Customer? = customerService.get(uuid)

        if (customer == null) {
            apiHandler.notFound(exchange, "Customer $uuid not found")
            return
        }
        apiHandler.ok(exchange, customer)
    }

    fun list(exchange: HttpServerExchange) {
        val customers: List<Customer> = customerService.getAll()
        Exchange.body().sendJson(exchange, customers)
    }
}
