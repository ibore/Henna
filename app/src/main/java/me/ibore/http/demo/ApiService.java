package me.ibore.http.demo;


import io.reactivex.Observable;
import me.ibore.http.annotation.GET;
import me.ibore.http.annotation.Param;

public interface ApiService {

    @GET
    Observable<String> getSo(@Param("test") String test);

}
