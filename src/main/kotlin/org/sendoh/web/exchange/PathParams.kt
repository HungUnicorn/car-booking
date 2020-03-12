package org.sendoh.web.exchange

import io.undertow.server.HttpServerExchange

/**
 * Extract request from exchange
 * */
interface PathParams {
    fun pathParam(exchange: HttpServerExchange, name: String): String? {
        return exchange.queryParameters[name]?.first
    }
}
