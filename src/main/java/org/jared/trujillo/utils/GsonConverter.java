package org.jared.trujillo.utils;

import com.google.gson.Gson;
import org.jared.trujillo.interfaces.JsonConverter;

public class GsonConverter implements JsonConverter {

    private final Gson gson = new Gson();

    @Override
    public String toJson(Object src) {
        return gson.toJson(src);
    }

    @Override
    public <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}
