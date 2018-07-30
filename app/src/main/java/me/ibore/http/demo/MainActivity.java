package me.ibore.http.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import ibore.android.henna.Henna;
import ibore.android.henna.HennaProxy;
import ibore.android.henna.Response;
import ibore.android.henna.StringConverter;
import ibore.android.henna.interceptor.HttpLogInterceptor;

public class MainActivity extends AppCompatActivity {

    private Henna xHttp;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String url = "test{path}";
        String test = "{path}";
        Log.d("----", url.replace(test, "test"));

        HttpLogInterceptor logInterceptor = new HttpLogInterceptor();
        logInterceptor.setPrintLevel(HttpLogInterceptor.Level.BODY);
        logInterceptor.setColorLevel(Level.WARNING);

        xHttp = new Henna.Builder()
                .header("ce", "ddd")
                .header("dddd", "dddddd")
                .maxRetry(3)
                .addInterceptor(logInterceptor)
                .converter(StringConverter.create())
                .builder();

        List<String> strings = new ArrayList<>();
        strings.add("111111111111");
        strings.add("222222222222");
        strings.add("333333333333");


        HennaProxy proxy = new HennaProxy(xHttp, "http://www.so.com/");
        ApiService apiService = proxy.create(ApiService.class);
        apiService.getSo("test","so.com")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Response<String>>() {

                    @Override
                    public void onNext(Response<String> stringResponse) {
                        Log.d("----", stringResponse.body());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String s = xHttp.<String>post("http://www.so.com/")
                            .tag(this)
                            .header("test1", "test")
                            .param("test", "test")
                            .param("test", "fddsfdsfsf")
                            .uploadListener(new ProgressListener() {
                                @Override
                                public void onProgress(Progress progress) {
                                    Log.d("----", "uploadListener:" + progress.getPercent());
                                }
                            })

                            .progress(true)
                            .execute(new ProgressListener() {
                                @Override
                                public void onProgress(Progress progress) {
                                    Log.d("----", "downloadListener:" + progress.getPercent());
                                }
                            });
                    Log.d("----", s);
                } catch (HttpException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
//        Observable<String> observable = xHttp.<String>get("http://101.199.121.249/softdl.360tpcdn.com/auto/20180309/102615199_2f0a7c0426fa87ac8112aff10789ed08.exe")
//                .tag(this)
//                .headers("test1", "test")
//                .params("test", "test")
//                .params("test", "fddsfdsfsf")
//                /*.uploadListener(new ProgressListener() {
//                    @Override
//                    public void onProgress(Progress progress) {
//                        Log.d("----", "uploadListener:" + progress.getPercent());
//                    }
//                })*/
//                .download(new ProgressListener() {
//                    @Override
//                    public void onProgress(Progress progress) {
//                        Log.d("----", "downloadListener:" + progress.getPercent());
//                    }
//                })
//                .adapter();
//
//        ((Observable<String>)
//                xHttp.<String>get("http://101.199.121.249/softdl.360tpcdn.com/auto/20180309/102615199_2f0a7c0426fa87ac8112aff10789ed08.exe")
//                        .tag(this)
//                        .headers("test1", "test")
//                        .params("test", "test")
//                        .params("test", "fddsfdsfsf")
//                        /*.uploadListener(new ProgressListener() {
//                            @Override
//                            public void onProgress(Progress progress) {
//                                Log.d("----", "uploadListener:" + progress.getPercent());
//                            }
//                        })*/
//                        .download(new ProgressListener() {
//                            @Override
//                            public void onProgress(Progress progress) {
//                                Log.d("----", "downloadListener:" + progress.getPercent());
//                            }
//                        })
//                        .adapter())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(new DisposableObserver<String>() {
//                    @Override
//                    public void onNext(String s) {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onComplete() {
//
//                    }
//                });
        /*xHttp.<String>get("http://101.199.121.249/softdl.360tpcdn.com/auto/20180309/102615199_2f0a7c0426fa87ac8112aff10789ed08.exe")
                .tag(this)
                .header("test1", "test")
                .param("test", "test")
                .param("test", "fddsfdsfsf")
                *//*.uploadListener(new ProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        Log.d("----", "uploadListener:" + progress.getPercent());
                    }
                })*//*
                .progress(true)
                .observable(new ProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        Log.d("----", "downloadListener:" + progress.getPercent());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {

                    @Override
                    protected void onStart() {
                        super.onStart();
                        Log.d("----", "onStart");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d("----", s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("----", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("----", "onComplete");
                    }
                });*/



                /*.enqueue(new HennaListener<String>() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        Log.d("----", "onStart");
                    }

                    @Override
                    public void onSuccess(String s) {
                        Log.d("----", s);
                    }

                    @Override
                    public void onProgress(Progress progress) {
                        Log.d("----", "--progress:" + progress.getPercent());
                    }

                    @Override
                    public void onError(HttpException e) {
                        Log.d("----", "StringListener_onError");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        Log.d("----", "onFinish");
                    }
                });
*/
    }

    public boolean b = true;

    @Override
    protected void onDestroy() {
        /*xHttp.cancelTag(this);*/
        compositeDisposable.dispose();
        super.onDestroy();
    }


}
