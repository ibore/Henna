package me.ibore.http.demo;


import io.reactivex.Observable;
import me.ibore.http.annotation.GET;
import me.ibore.http.annotation.Param;

public interface ApiService {

    @GET("s")
    Observable<String> getSo(@Param("q") String q);

}
