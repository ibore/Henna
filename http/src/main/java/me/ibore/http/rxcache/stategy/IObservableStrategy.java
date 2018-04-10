package me.ibore.http.rxcache.stategy;

import java.lang.reflect.Type;

import io.reactivex.Observable;
import me.ibore.http.rxcache.RxCache;
import me.ibore.http.rxcache.data.CacheResult;

public interface IObservableStrategy {

    <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type);

}
