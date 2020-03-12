package org.sendoh


import io.undertow.server.RoutingHandler
import org.sendoh.Injector.carRentalRoute
import org.sendoh.Injector.customerRoute
import org.sendoh.web.SimpleServer
import org.sendoh.web.HealthHandler

/**
 * Define routes and corresponding actions, and start server
 * */
object CarBookingApplication {

    val ROUTES = RoutingHandler()
            .get("/v1/customers", customerRoute::list)
            .get("/v1/customers/{id}", customerRoute::get)
            .post("/v1/customers", customerRoute::post)

            .get("/v1/rentals/{carLicenseNo}", carRentalRoute::get)
            .post("/v1/rentals", carRentalRoute::post)
            .post("/v1/rentals/nearbyCars", carRentalRoute::listWithDistance)
            .delete("/v1/rentals/{carLicenseNo}", carRentalRoute::delete)

            .get("/health", HealthHandler())

    @JvmStatic
    fun main(args: Array<String>) {
        val server: SimpleServer = SimpleServer.simpleServer(ROUTES)
        server.start()
    }
}
