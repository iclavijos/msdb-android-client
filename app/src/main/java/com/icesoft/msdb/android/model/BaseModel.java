package com.icesoft.msdb.android.model;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.icesoft.msdb.android.exception.MSDBException;

public abstract class BaseModel {

    private static final String TAG = "BaseModel";
    private final JsonMapper mapper = new JsonMapper();

    public String toJSON() {
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "Couldn't serialize instance " + this.toString());
            throw new MSDBException(e);
        }
    }

    public <T> T toObject(String json) {
        return (T) toObject(this.getClass(), json);
    }

    private <T> T toObject(Class<T> type, String json) {
        try {
            return mapper.readValue(json, type);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "Couldn't deserialize instance " + json);
            throw new MSDBException(e);
        }
    }
}
