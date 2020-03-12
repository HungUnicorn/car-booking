package org.sendoh.dao

import org.sendoh.model.Customer
import org.sendoh.model.User
import java.math.BigDecimal
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

class InMemoryCustomerDao : CustomerDao {
    private val storage: ConcurrentMap<String, Customer> = ConcurrentHashMap()

    init {
        val testExample = Customer(User("123", "a@car", "1234"), BigDecimal.TEN)
        storage.putIfAbsent(testExample.id, testExample)
    }

    override fun get(id: String): Customer? {
        return storage[id]
    }

    override fun getAll(): List<Customer> {
        return storage.values.toList()
    }

    override fun update(customer: Customer) {
        storage.replace(customer.id, customer)
    }

    override fun save(customer: Customer) {
        storage.putIfAbsent(customer.id, customer)
    }
}
