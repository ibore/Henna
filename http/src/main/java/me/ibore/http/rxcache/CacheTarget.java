package me.ibore.http.rxcache;

public enum CacheTarget {
    Memory,
    Disk,
    MemoryAndDisk;

    public boolean supportMemory() {
        return this==Memory || this== MemoryAndDisk;
    }

    public boolean supportDisk() {
        return this==Disk || this== MemoryAndDisk;
    }

}
