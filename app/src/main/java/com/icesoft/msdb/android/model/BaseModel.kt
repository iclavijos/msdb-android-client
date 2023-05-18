package com.icesoft.msdb.android.model

import android.util.Log
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.json.JsonMapper
import com.icesoft.msdb.android.exception.MSDBException

abstract class BaseModel {
    private val TAG = "BaseModel"
    private val mapper = JsonMapper()

    open fun toJSON(): String? {
        return try {
            mapper.writeValueAsString(this)
        } catch (e: JsonProcessingException) {
            Log.e(TAG, "Couldn't serialize instance $this")
            throw MSDBException(e)
        }
    }

    open fun <T> toObject(json: String): T {
        return toObject(this.javaClass, json) as T
    }

    protected fun <T> toObject(type: Class<T>, json: String): T {
        return try {
            mapper.readValue(json, type)
        } catch (e: JsonProcessingException) {
            Log.e(TAG, "Couldn't deserialize instance $json")
            throw MSDBException(e)
        }
    }
}