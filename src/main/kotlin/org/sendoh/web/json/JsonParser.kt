package org.sendoh.web.json

import com.fasterxml.jackson.core.type.TypeReference
import io.undertow.server.HttpServerExchange


interface JsonParser {
    fun <T> parseJson(exchange: HttpServerExchange, typeRef: TypeReference<T>): T {
        return JsonSerde.serializer()!!.fromInputStream(exchange.inputStream, typeRef)
    }
}
