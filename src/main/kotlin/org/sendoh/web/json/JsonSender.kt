package org.sendoh.web.json

import io.undertow.server.HttpServerExchange
import io.undertow.util.Headers
import java.nio.ByteBuffer


interface JsonSender {
    fun sendJson(exchange: HttpServerExchange, obj: Any) {
        exchange.responseHeaders.put(Headers.CONTENT_TYPE, "application/json")
        exchange.responseSender.send(ByteBuffer.wrap(JsonSerde.serializer()!!.toByteArray(obj)))
    }
}
