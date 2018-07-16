package me.ibore.http;

public interface JsonParser {

    String toJson(Object object);

    <T> T toObject(String json);

}
