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
import org.sendoh.model.Customer
import org.sendoh.request.NewCustomerRequest
import org.sendoh.model.User
import org.sendoh.exchange.CustomerExchange
import org.sendoh.service.CustomerService
import java.math.BigDecimal

class CustomerRouteTest {

    @RelaxedMockK
    lateinit var apiHandler: ApiHandler

    @MockK
    lateinit var customerExchange: CustomerExchange

    @MockK
    lateinit var customerService: CustomerService

    @RelaxedMockK
    lateinit var exchange: HttpServerExchange

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun post__givenInvalidCustomerWithJsonException__badRequest() {
        every { customerExchange.newCustomer(exchange) } throws JsonSerdeException(Exception())

        val customerRoute = CustomerRoute(apiHandler, customerExchange, customerService)

        customerRoute.post(exchange)

        verify(exactly = 0) { customerService.register(any()) }
        verify {
            apiHandler.badRequest(exchange,
                    "Invalid customer with error: org.sendoh.web.json.JsonSerde\$JsonSerdeException: java.lang.Exception")
        }
    }

    @Test
    fun post__givenInvalidCustomerWithCarRentalException__badRequest() {
        val newCustomer = NewCustomerRequest(User("123", "c@car", "1"), BigDecimal.TEN)

        every { customerExchange.newCustomer(exchange) } returns newCustomer
        every { customerService.register(any()) } throws CarRentalException("Test")

        val customerRoute = CustomerRoute(apiHandler, customerExchange, customerService)

        customerRoute.post(exchange)

        verify {
            customerService.register(any())
            apiHandler.badRequest(exchange,
                    "Invalid customer with error: org.sendoh.exception.CarRentalException: Test")
        }
    }

    @Test
    fun post__givenValidCustomer__ok() {
        val newCustomer = NewCustomerRequest(User("123", "c@car", "1"), BigDecimal.TEN)
        val customer = Customer(newCustomer.user, newCustomer.balance)

        every { customerExchange.newCustomer(exchange) } returns newCustomer
        every { customerService.register(any()) } returns customer

        val customerRoute = CustomerRoute(apiHandler, customerExchange, customerService)

        customerRoute.post(exchange)
        verify {
            customerService.register(newCustomer)
            apiHandler.ok(exchange, customer)
        }
    }
}
