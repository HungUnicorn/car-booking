package org.sendoh.dao

import org.sendoh.model.Customer

interface CustomerDao {
    fun get(id: String): Customer?
    fun getAll(): List<Customer>
    fun save(customer: Customer)
    fun update(customer: Customer)
}
