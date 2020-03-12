package org.sendoh.exchange

import io.undertow.server.HttpServerExchange
import org.sendoh.web.exchange.Exchange
import org.sendoh.model.LocationWithDistance
import org.sendoh.request.RentCarCustomerRequest


class CarRentalExchange {
    fun carLicenseNo(exchange: HttpServerExchange): String {
        return Exchange.pathParams().pathParam(exchange, "carLicenseNo")!!
    }

    fun post(exchange: HttpServerExchange): RentCarCustomerRequest {
        return Exchange.body().parseJson(exchange, RentCarCustomerRequest.typeRef())
    }

    fun delete(exchange: HttpServerExchange) : RentCarCustomerRequest {
        return Exchange.body().parseJson(exchange, RentCarCustomerRequest.typeRef())
    }

    fun locationWithDistance(exchange: HttpServerExchange) : LocationWithDistance{
        return Exchange.body().parseJson(exchange, LocationWithDistance.typeRef())
    }
}
