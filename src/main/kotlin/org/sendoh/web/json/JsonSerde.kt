package org.sendoh.web.json

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.IOException
import java.io.InputStream


/**
 * Define object mapper
 * */
class JsonSerde private constructor(private val mapper: ObjectMapper) {
    companion object {
        private var defaultSerializer: JsonSerde? = null

        fun serializer(): JsonSerde? {
            return defaultSerializer
        }

        init {
            val mapper = ObjectMapper()

            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
            mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            mapper.registerModule(JavaTimeModule())
            mapper.registerModule(KotlinModule())
            defaultSerializer = JsonSerde(mapper)
        }
    }

    class JsonSerdeException constructor(ex: Exception) : RuntimeException(ex)

    private val writer: ObjectWriter = mapper.writer()

    fun <T> fromInputStream(`is`: InputStream?, typeRef: TypeReference<T>?): T {
        return try {
            mapper.readValue(`is`, typeRef)
        } catch (e: IOException) {
            throw JsonSerdeException(e)
        }
    }

    fun toByteArray(obj: Any?): ByteArray {
        return try {
            writer.writeValueAsBytes(obj)
        } catch (e: IOException) {
            throw JsonSerdeException(e)
        }
    }
}
