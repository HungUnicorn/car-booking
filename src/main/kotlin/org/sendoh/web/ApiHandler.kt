package org.sendoh.web

import io.undertow.server.HttpServerExchange
import io.undertow.util.StatusCodes
import org.sendoh.web.exchange.Exchange

/**
 * Handle response status and content
 * */

class ApiHandler {
    fun ok(exchange: HttpServerExchange, obj: Any) {
        exchange.statusCode = StatusCodes.CREATED
        Exchange.body().sendJson(exchange, obj)
    }

    fun notFound(exchange: HttpServerExchange, message: String) {
        val error = ApiError(StatusCodes.NOT_FOUND, message)
        exchange.statusCode = error.statusCode
        Exchange.body().sendJson(exchange, error)
    }

    fun badRequest(exchange: HttpServerExchange, message: String) {
        val error = ApiError(StatusCodes.BAD_REQUEST, message)
        exchange.statusCode = error.statusCode
        Exchange.body().sendJson(exchange, error)
    }

    private class ApiError(val statusCode: Int, val message: String?)
}
