package me.ibore.http.rxcache.stategy;

import org.reactivestreams.Publisher;

import java.lang.reflect.Type;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import me.ibore.http.rxcache.RxCache;
import me.ibore.http.rxcache.RxCacheHelper;
import me.ibore.http.rxcache.data.CacheResult;

public final class OnlyCacheStrategy implements IStrategy  {

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type) {
        return RxCacheHelper.loadCache(rxCache, key, type,false);
    }

    @Override
    public <T> Publisher<CacheResult<T>> flow(RxCache rxCache, String key, Flowable<T> source, Type type) {
        return RxCacheHelper.loadCacheFlowable(rxCache, key, type,false);
    }
}

