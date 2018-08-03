package me.ibore.http.demo;


import me.ibore.henna.proxy.http.Header;
import me.ibore.henna.proxy.http.Path;
import io.reactivex.Observable;
import me.ibore.henna.Call;
import me.ibore.henna.Response;
import me.ibore.henna.proxy.http.DownloadListener;
import me.ibore.henna.proxy.http.GET;
import me.ibore.henna.proxy.http.POST;
import me.ibore.henna.proxy.http.Param;
import me.ibore.henna.proxy.http.UploadListener;
import me.ibore.henna.ProgressListener;

public interface ApiService {

    @GET("s{path}")
    Observable<Response<String>> getSo(@Header("test") String test, @Path("path") String path, @Param("q") String q);

    @POST()
    Call<String> getSo(@UploadListener ProgressListener uploadListener, @DownloadListener ProgressListener downloadListener, @Param("src") String string);

    @GET()
    Call<String> getSo(@DownloadListener ProgressListener listener, @Param("src") String string);

}
