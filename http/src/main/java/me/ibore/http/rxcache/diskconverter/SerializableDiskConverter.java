package me.ibore.http.rxcache.diskconverter;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

import me.ibore.http.utils.Utils;

public class SerializableDiskConverter implements IDiskConverter {

    @Override
    public <T> T load(InputStream source, Type type) {
        T value = null;
        ObjectInputStream oin = null;
        try {
            oin = new ObjectInputStream(source);
            value = (T) oin.readObject();
        } catch (IOException | ClassNotFoundException e) {
            HttpLog.e(e);
        } finally {
            Utils.close(source);
        }
        return value;
    }

    @Override
    public boolean writer(OutputStream sink, Object data) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(sink);
            oos.writeObject(data);
            oos.flush();
            return true;
        } catch (IOException e) {
            HttpLog.e(e);
        } finally {
            Utils.close(sink);
        }
        return false;
    }

}

