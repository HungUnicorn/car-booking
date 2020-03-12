package org.sendoh.web

import io.undertow.Undertow
import io.undertow.server.HttpHandler
import io.undertow.server.handlers.BlockingHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * Abstract the Undertow server
 */
class SimpleServer private constructor(

        private val undertow: Undertow.Builder) {

    fun start(): Undertow {
        val undertow = undertow.build()
        undertow.start()

        undertow.listenerInfo
                .stream()
                .forEach { listenerInfo: Undertow.ListenerInfo -> logger.info(listenerInfo.toString()) }
        return undertow
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SimpleServer::class.java)
        private const val DEFAULT_PORT = 8080
        private const val DEFAULT_HOST = "0.0.0.0"

        fun simpleServer(handler: HttpHandler): SimpleServer {
            val undertow = Undertow.builder()
                    .addHttpListener(DEFAULT_PORT, DEFAULT_HOST, BlockingHandler(handler))
            return SimpleServer(undertow)
        }
    }
}
