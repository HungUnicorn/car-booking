package org.sendoh.web.exchange

import org.sendoh.web.json.JsonParser
import org.sendoh.web.json.JsonSender


object Exchange {
    private val BODY: BodyImpl = object : BodyImpl {}
    fun body(): BodyImpl {
        return BODY
    }

    private val PATHPARAMS: PathParamImpl = object : PathParamImpl {}
    fun pathParams(): PathParamImpl {
        return PATHPARAMS
    }

    interface BodyImpl : JsonSender, JsonParser
    interface PathParamImpl : PathParams
}
