package me.ibore.http.rxcache.data;

public enum ResultFrom {
    Remote, Disk, Memory;

    public static boolean ifFromCache(ResultFrom from) {
        return from == Disk || from == Memory;
    }
}
