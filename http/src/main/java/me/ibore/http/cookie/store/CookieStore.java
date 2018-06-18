package me.ibore.http.cookie.store;

import java.util.Collection;
import java.util.List;

import okhttp3.Cookie;

/**
 * Created by Administrator on 2017/6/9.
 */

public interface CookieStore {

    List<Cookie> loadAll();

    void saveAll(Collection<Cookie> cookies);

    void remove(Cookie cookie);

    void removeAll(Collection<Cookie> cookies);

    void clear();

}
