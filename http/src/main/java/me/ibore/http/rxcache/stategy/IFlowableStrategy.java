package me.ibore.http.rxcache.stategy;

import org.reactivestreams.Publisher;

import java.lang.reflect.Type;

import io.reactivex.Flowable;
import me.ibore.http.rxcache.RxCache;
import me.ibore.http.rxcache.data.CacheResult;

public interface IFlowableStrategy {

    <T> Publisher<CacheResult<T>> flow(RxCache rxCache, String key, Flowable<T> source, Type type);

}
