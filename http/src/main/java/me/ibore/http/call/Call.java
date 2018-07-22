package me.ibore.http.call;

import java.io.IOException;

import me.ibore.http.Response;
import me.ibore.http.listener.HennaListener;
import me.ibore.http.request.Request;
import okhttp3.ResponseBody;

public interface Call<T> extends Cloneable {

    Response<T> execute() throws Exception;

    void enqueue(HennaListener<T> listener);

    boolean isExecuted();

    void cancel();

    boolean isCanceled();

    Call<T> clone();

    Request<T, ? extends Request> request();

    public interface Adapter<T, E> {

        E adapter(Call<T> call);

    }

    public interface Converter<T> {

        T convert(ResponseBody value) throws IOException;

        Converter<ResponseBody> DEFAULT = value -> value;

    }
}
