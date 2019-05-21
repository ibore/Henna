package me.ibore.http.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.logging.Level;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import me.ibore.henna.Henna;
import me.ibore.henna.Response;
import me.ibore.henna.adapter.rxjava2.RxJava2CallAdapter;
import me.ibore.henna.convert.StringConverter;
import me.ibore.henna.interceptor.HttpInterceptor;
import me.ibore.henna.proxy.HennaProxy;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    private Henna xHenna;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HttpInterceptor logInterceptor = new HttpInterceptor();
        logInterceptor.setPrintLevel(HttpInterceptor.Level.BODY);
        logInterceptor.setColorLevel(Level.WARNING);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .build();
        xHenna = new Henna.Builder()
                .context(this)
                .client(client)
                .maxRetry(3)
                .converter(StringConverter.create())
                .callAdapter(RxJava2CallAdapter.create())
                .builder();

        /*xHenna.<String>get("http://www.so.com/")
                .enqueue(new HennaListener<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.d("----", "onResponse");
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable e) {
                        Log.d("----", e.toString());
                    }
                });*/


        HennaProxy proxy = new HennaProxy(xHenna);
        ApiService apiService = proxy.create(ApiService.class);
        compositeDisposable.add(apiService.getSo("1111")
                .compose(upstream -> upstream.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()))
                .subscribeWith(new DisposableObserver<Response<String>>() {
                    @Override
                    public void onNext(Response<String> stringResponse) {
                        Log.d("----", "onNext");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("----", e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d("----", "onComplete");
                    }
                }));
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
