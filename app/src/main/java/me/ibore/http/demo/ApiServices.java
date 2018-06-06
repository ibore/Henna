package me.ibore.http.demo;

import java.io.File;

import retrofit2.http.Field;


public interface ApiServices {


    void get(@Field("dddd")File file);
}
