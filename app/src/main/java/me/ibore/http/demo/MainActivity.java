package me.ibore.http.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.logging.Level;

import me.ibore.http.XHttp;
import me.ibore.http.exception.HttpException;
import me.ibore.http.interceptor.HttpLogInterceptor;
import me.ibore.http.listener.StringListener;
import me.ibore.http.progress.Progress;
import me.ibore.http.progress.ProgressListener;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private XHttp xHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        HttpLogInterceptor logInterceptor = new HttpLogInterceptor("OkHttp");
        logInterceptor.setPrintLevel(HttpLogInterceptor.Level.BODY);
        logInterceptor.setColorLevel(Level.WARNING);

        xHttp = new XHttp.Builder()
                .header("ce", "ddd")
                .header("dddd", "dddddd")
                .param("ddd", "ddss")
                .maxRetry(3)
                .addInterceptor(logInterceptor)
                .builder();

        xHttp.get("http://hot.m.shouji.360tpcdn.com/170710//com.qihoo.appstore_300070091.apk")
//        xHttp.post("http://www.so.com/")
                .tag(this)
                .header("test1", "test")
                .param("test", "test")
                .param("test", "fddsfdsfsf")
                /*.upload(new ProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        Log.d("----", "progress:" + progress.getPercent());
                    }

                })*/
                .progress(true)
                .enqueue(new StringListener() {
                    @Override
                    public void onSuccess(String s) {
//                        Log.d("----",s);
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        Log.d("----", "--progress:" + progress.getPercent());
                    }

                    @Override
                    public void onError(HttpException e) {
                        e.printStackTrace();
                        Log.d("----", "StringListener_onError");

                        if (b) {
                            xHttp.get("http://hot.m.shouji.360tpcdn.com/170710//com.qihoo.appstore_300070091.apk")
                                    .enqueue(new StringListener() {
                                        @Override
                                        public void onSuccess(String s) {

                                        }

                                        @Override
                                        public void onError(HttpException e) {

                                        }
                                    });
                        }

                    }
                });

    }

    public boolean b = true;
    @Override
    protected void onDestroy() {
        xHttp.cancelTag(this);
        super.onDestroy();
    }


}
