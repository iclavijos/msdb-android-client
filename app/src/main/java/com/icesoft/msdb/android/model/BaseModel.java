package com.icesoft.msdb.android.model;

import com.auth0.android.request.internal.GsonProvider;

public abstract class BaseModel {

    public String toJSON() {
        return GsonProvider.buildGson().toJson(this, this.getClass());
    }
}
