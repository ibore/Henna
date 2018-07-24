package me.ibore.http.demo;


import io.reactivex.Observable;
import me.ibore.henna.Call;
import me.ibore.henna.Response;
import me.ibore.henna.http.DownloadListener;
import me.ibore.henna.http.GET;
import me.ibore.henna.http.Header;
import me.ibore.henna.http.POST;
import me.ibore.henna.http.Param;
import me.ibore.henna.http.UploadListener;
import me.ibore.henna.progress.ProgressListener;

public interface ApiService {

    @GET("s")
    Observable<Response<String>> getSo(@Param("q") String q);

    @POST()
    Call<String> getSo(@UploadListener ProgressListener uploadListener, @DownloadListener ProgressListener downloadListener, @Param("src") String string);

    @GET()
    Call<String> getSo(@DownloadListener ProgressListener listener, @Param("src") String string);

}
