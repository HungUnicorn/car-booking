package org.sendoh.service

import org.sendoh.dao.CustomerDao
import org.sendoh.exception.CarRentalException
import org.sendoh.model.Customer
import org.sendoh.request.NewCustomerRequest
import java.math.BigDecimal


class CustomerServiceImpl : CustomerService {
    var customerDao: CustomerDao

    constructor(customerDao: CustomerDao) {
        this.customerDao = customerDao
    }

    override fun register(newCustomerRequest: NewCustomerRequest): Customer {
        val customer = Customer(newCustomerRequest.user, newCustomerRequest.balance)

        if (customerDao.get(customer.id) != null) {
            throw CarRentalException("Email ${customer.user.email} already registered")
        }

        customerDao.save(customer)
        return customer
    }

    override fun get(id: String): Customer? {
        return customerDao.get(id)
    }

    override fun getAll(): List<Customer> {
        return customerDao.getAll()
    }

    override fun payRent(id: String, rent: BigDecimal) {
        val customer = customerDao.get(id) ?: throw CarRentalException("Customer $id does not exist")
        val paidBalance = customer.balance.minus(rent)

        val paidCustomer = Customer(customer.user, paidBalance)
        customerDao.update(paidCustomer)
    }

    override fun canRent(id: String): Boolean {
        val customer = customerDao.get(id) ?: return false
        if (isBalancePositive(customer.balance)) {
            return true
        }
        return false
    }

    private fun isBalancePositive(balance: BigDecimal) = balance > BigDecimal.ZERO
}
