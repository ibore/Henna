package me.ibore.http.callback;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class JsonCallback<T> extends Callback<T> {

    private StringBuffer stringBuffer;

}
