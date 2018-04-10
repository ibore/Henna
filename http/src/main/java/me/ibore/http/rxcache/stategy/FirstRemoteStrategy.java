package me.ibore.http.rxcache.stategy;

import org.reactivestreams.Publisher;

import java.lang.reflect.Type;
import java.util.Arrays;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import me.ibore.http.rxcache.CacheTarget;
import me.ibore.http.rxcache.RxCache;
import me.ibore.http.rxcache.RxCacheHelper;
import me.ibore.http.rxcache.data.CacheResult;

public final class FirstRemoteStrategy implements IStrategy {
    private boolean isSync;

    public FirstRemoteStrategy() {
        isSync = false;
    }

    public FirstRemoteStrategy(boolean isSync) {
        this.isSync = isSync;
    }

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type) {
        Observable<CacheResult<T>> cache = RxCacheHelper.loadCache(rxCache, key, type, true);
        Observable<CacheResult<T>> remote;
        if (isSync) {
            remote =  RxCacheHelper.loadRemoteSync(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        } else {
            remote =  RxCacheHelper.loadRemote(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        }
        return Observable
                .concatDelayError(Arrays.asList(remote,cache))
                .take(1);
    }

    @Override
    public <T> Publisher<CacheResult<T>> flow(RxCache rxCache, String key, Flowable<T> source, Type type) {
        Flowable<CacheResult<T>> cache = RxCacheHelper.loadCacheFlowable(rxCache, key, type, true);
        Flowable<CacheResult<T>> remote;
        if (isSync) {
            remote =  RxCacheHelper.loadRemoteSyncFlowable(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        } else {
            remote =RxCacheHelper.loadRemoteFlowable(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        }
        return Flowable
                .concatDelayError(Arrays.asList(remote,cache))
                .take(1);
    }

}