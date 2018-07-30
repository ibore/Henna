package me.ibore.http.demo;


import ibore.android.henna.http.Path;
import io.reactivex.Observable;
import ibore.android.henna.Call;
import ibore.android.henna.Response;
import ibore.android.henna.http.DownloadListener;
import ibore.android.henna.http.GET;
import ibore.android.henna.http.POST;
import ibore.android.henna.http.Param;
import ibore.android.henna.http.UploadListener;
import ibore.android.henna.ProgressListener;

public interface ApiService {

    @GET("s{path}")
    Observable<Response<String>> getSo(@Path("path") String path, @Param("q") String q);

    @POST()
    Call<String> getSo(@UploadListener ProgressListener uploadListener, @DownloadListener ProgressListener downloadListener, @Param("src") String string);

    @GET()
    Call<String> getSo(@DownloadListener ProgressListener listener, @Param("src") String string);

}
