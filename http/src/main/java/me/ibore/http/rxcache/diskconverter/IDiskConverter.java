package me.ibore.http.rxcache.diskconverter;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

public interface IDiskConverter {

    /**
     * 读取
     *
     * @param source
     * @return
     */
    <T> T  load(InputStream source, Type type);

    /**
     * 写入
     *
     * @param sink
     * @param data
     * @return
     */
    boolean writer(OutputStream sink, Object data);

}
