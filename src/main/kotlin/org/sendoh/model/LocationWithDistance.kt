package org.sendoh.model

import com.fasterxml.jackson.core.type.TypeReference

class LocationWithDistance(val location: Location, val distanceInMeter: Double) {
    companion object {
        private val TYPE_REF: TypeReference<LocationWithDistance> = object : TypeReference<LocationWithDistance>() {}

        fun typeRef(): TypeReference<LocationWithDistance> {
            return TYPE_REF
        }

        private val LIST_TYPE_REF: TypeReference<List<LocationWithDistance>> = object : TypeReference<List<LocationWithDistance>>() {}

        fun listTypeRef(): TypeReference<List<LocationWithDistance>> {
            return LIST_TYPE_REF
        }
    }
}
