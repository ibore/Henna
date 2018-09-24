package me.ibore.henna;

import java.io.IOException;

import me.ibore.henna.exception.ConvertException;

public interface Converter<T> {

    T convert(okhttp3.Response value) throws IOException, ConvertException;

}