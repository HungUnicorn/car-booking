package org.sendoh.exchange

import io.undertow.server.HttpServerExchange
import org.sendoh.request.NewCustomerRequest
import org.sendoh.web.exchange.Exchange


class CustomerExchange {
    fun uuid(exchange: HttpServerExchange): String {
        return Exchange.pathParams().pathParam(exchange, "id")!!
    }

    fun newCustomer(exchange: HttpServerExchange): NewCustomerRequest {
        return Exchange.body().parseJson(exchange, NewCustomerRequest.typeRef())
    }
}
