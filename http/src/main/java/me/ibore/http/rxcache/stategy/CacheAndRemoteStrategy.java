package me.ibore.http.rxcache.stategy;

import org.reactivestreams.Publisher;

import java.lang.reflect.Type;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import me.ibore.http.rxcache.RxCache;
import me.ibore.http.rxcache.data.CacheResult;

public final class CacheAndRemoteStrategy implements IStrategy {
    private boolean isSync;

    public CacheAndRemoteStrategy() {
        isSync = false;
    }

    public CacheAndRemoteStrategy(boolean isSync) {
        this.isSync = isSync;
    }

    @Override
    public <T> Observable<CacheResult<T>> execute(RxCache rxCache, String key, Observable<T> source, Type type) {
        Observable<CacheResult<T>> cache = RxCacheHelper.loadCache(rxCache, key, type, true);
        Observable<CacheResult<T>> remote;
        if (isSync) {
            remote = RxCacheHelper.loadRemoteSync(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        } else {
            remote = RxCacheHelper.loadRemote(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        }
        return Observable.concat(cache, remote)
                .filter(new Predicate<CacheResult<T>>() {
                    @Override
                    public boolean test(@NonNull CacheResult<T> result) throws Exception {
                        return result != null && result.getData() != null;
                    }
                });
    }

    @Override
    public <T> Publisher<CacheResult<T>> flow(RxCache rxCache, String key, Flowable<T> source, Type type) {
        Flowable<CacheResult<T>> cache = RxCacheHelper.loadCacheFlowable(rxCache, key, type, true);
        Flowable<CacheResult<T>> remote;
        if (isSync) {
            remote = RxCacheHelper.loadRemoteSyncFlowable(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        } else {
            remote = RxCacheHelper.loadRemoteFlowable(rxCache, key, source, CacheTarget.MemoryAndDisk, false);
        }
        return Flowable.concat(cache, remote)
                .filter(new Predicate<CacheResult<T>>() {
                    @Override
                    public boolean test(@NonNull CacheResult<T> result) throws Exception {
                        return result != null && result.getData() != null;
                    }
                });
    }
}

