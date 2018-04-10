package me.ibore.http.request;

import android.content.Context;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;

import me.ibore.http.CacheMode;
import me.ibore.http.api.ApiService;
import me.ibore.http.https.HttpsUtils;
import me.ibore.http.model.HttpHeaders;
import me.ibore.http.model.HttpParams;
import me.ibore.http.rxcache.RxCache;
import me.ibore.http.rxcache.diskconverter.IDiskConverter;
import okhttp3.Cache;
import okhttp3.Cookie;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

public abstract class BaseRequest<R extends BaseRequest> {

    protected Cache cache = null;
    protected CacheMode cacheMode = CacheMode.NO_CACHE;                    //默认无缓存
    protected long cacheTime = -1;                                         //缓存时间
    protected String cacheKey;                                             //缓存Key
    protected IDiskConverter diskConverter;                                //设置Rxcache磁盘转换器
    protected String baseUrl;                                              //BaseUrl
    protected String url;                                                  //请求url
    protected long readTimeOut;                                            //读超时
    protected long writeTimeOut;                                           //写超时
    protected long connectTimeout;                                         //链接超时
    protected int retryCount;                                              //重试次数默认3次
    protected int retryDelay;                                              //延迟xxms重试
    protected int retryIncreaseDelay;                                      //叠加延迟
    protected boolean isSyncRequest;                                       //是否是同步请求
    protected List<Cookie> cookies = new ArrayList<>();                    //用户手动添加的Cookie
    protected final List<Interceptor> networkInterceptors = new ArrayList<>();
    protected HttpHeaders headers = new HttpHeaders();                     //添加的header
    protected HttpParams params = new HttpParams();                        //添加的param
    protected Retrofit retrofit;
    protected RxCache rxCache;                                             //rxcache缓存
    protected ApiService apiManager;                                       //通用的的api接口
    protected OkHttpClient okHttpClient;
    protected Context context;
    private boolean sign = false;                                          //是否需要签名
    private boolean timeStamp = false;                                     //是否需要追加时间戳
    private boolean accessToken = false;                                   //是否需要追加token
    protected HttpUrl httpUrl;
    protected Proxy proxy;
    protected HttpsUtils.SSLParams sslParams;
    protected HostnameVerifier hostnameVerifier;
    protected List<Converter.Factory> converterFactories = new ArrayList<>();
    protected List<CallAdapter.Factory> adapterFactories = new ArrayList<>();
    protected final List<Interceptor> interceptors = new ArrayList<>();


}
