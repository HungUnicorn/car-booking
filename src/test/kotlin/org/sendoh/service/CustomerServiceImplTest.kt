package org.sendoh.service

import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.sendoh.dao.CustomerDao
import org.sendoh.exception.CarRentalException
import org.sendoh.model.Customer
import org.sendoh.request.NewCustomerRequest
import org.sendoh.model.User
import java.math.BigDecimal

class CustomerServiceImplTest {

    @MockK
    lateinit var customerDao: CustomerDao

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test(expected = CarRentalException::class)
    fun register__givenAlreadyRegistered__carRentalException() {
        val newCustomer = NewCustomerRequest(User("banachi", "a@car", "1234"), BigDecimal.TEN)
        val customer = Customer(newCustomer.user, newCustomer.balance)
        every { customerDao.get(any()) } returns customer

        val customerService = CustomerServiceImpl(customerDao)
        customerService.register(newCustomer)
    }

    @Test
    fun register__givenNewCustomer__save() {
        val newCustomer = NewCustomerRequest(User("banachi", "a@car", "1234"), BigDecimal.TEN)
        val customer = Customer(newCustomer.user, newCustomer.balance)

        every { customerDao.get(any()) } returns null
        every { customerDao.save(customer) } just runs

        val customerService = CustomerServiceImpl(customerDao)
        val register = customerService.register(newCustomer)

        verify {
            customerDao.save(customer)
        }
        assertEquals(customer, register)
    }

    @Test
    fun payRent__givenCustomer__update() {
        val customer = Customer(User("banachi", "a@car", "1234"), BigDecimal.TEN)

        every { customerDao.get(customer.id) } returns customer

        val customerService = CustomerServiceImpl(customerDao)
        val rent = BigDecimal.ONE

        val expectedCustomer = Customer(customer.user, customer.balance.minus(rent))
        every { customerDao.update(expectedCustomer) } just runs

        customerService.payRent(customer.id, rent)

        verify {
            customerDao.update(expectedCustomer)
        }
    }

    @Test(expected = CarRentalException::class)
    fun payRent__givenNotExistedCustomer__carRentalException() {
        val customer = Customer(User("banachi", "a@car", "1234"), BigDecimal.TEN)

        every { customerDao.get(any()) } returns null

        val customerService = CustomerServiceImpl(customerDao)
        val rent = BigDecimal.ONE

        customerService.payRent(customer.id, rent)
    }

    @Test
    fun canRent__givenNotExistedCustomer__rentException() {
        every { customerDao.get(any()) } returns null

        val customerService = CustomerServiceImpl(customerDao)
        val canRent = customerService.canRent("aa")

        assertEquals(false, canRent)
    }

    @Test
    fun canRent__givenNotPositiveBalance__rentException() {
        val customer = Customer(User("banachi", "a@car", "1234"), BigDecimal.valueOf(-1.0))

        every { customerDao.get(customer.id) } returns customer

        val customerService = CustomerServiceImpl(customerDao)
        val canRent = customerService.canRent(customer.id)

        assertEquals(false, canRent)
    }
}
