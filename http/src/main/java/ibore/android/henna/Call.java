package ibore.android.henna;

public interface Call<T> extends Cloneable {

    Response<T> execute() throws Exception;

    void enqueue(HennaListener<T> listener);

    boolean isExecuted();

    void cancel();

    boolean isCanceled();

    Call<T> clone();

    Request<T, ? extends Request> request();

}
