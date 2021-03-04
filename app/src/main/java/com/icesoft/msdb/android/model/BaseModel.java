package com.icesoft.msdb.android.model;

import com.auth0.android.request.internal.GsonProvider;

public abstract class BaseModel {

    public String toJSON() {
        return GsonProvider.buildGson().toJson(this, this.getClass());
    }

    public <T> T toObject(String json) {
        return (T) toObject(this.getClass(), json);
    }

    private <T> T toObject(Class<T> type, String json) {
        return GsonProvider.buildGson().fromJson(json, type);
    }
}
