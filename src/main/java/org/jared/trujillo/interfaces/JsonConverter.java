package org.jared.trujillo.interfaces;

public interface JsonConverter {

    String toJson(Object src);
    <T> T fromJson(String json, Class<T> classOfT);

}
