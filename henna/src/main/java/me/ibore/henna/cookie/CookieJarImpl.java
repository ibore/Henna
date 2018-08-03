package me.ibore.henna.cookie;


import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by Administrator on 2017/6/8.
 */

public final class CookieJarImpl implements okhttp3.CookieJar {

    private CookieStore memory;

    private CookieStore persistent;

    public CookieJarImpl() {
        memory = new MemoryCookieStore();
    }

    public CookieJarImpl(CookieStore cookieStore) {
        memory = new MemoryCookieStore();
        this.persistent = cookieStore;
        memory.saveAll(persistent.loadAll());
    }

    @Override
    synchronized public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        memory.saveAll(cookies);
        if (null != persistent) persistent.saveAll(filterPersistentCookies(cookies));
    }

    private List<Cookie> filterPersistentCookies(List<Cookie> cookies) {
        List<Cookie> persistentCookies = new ArrayList<>();
        for (Cookie cookie : cookies) {
            if (cookie.persistent()) {
                persistentCookies.add(cookie);
            }
        }
        return persistentCookies;
    }

    @Override
    synchronized public List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> cookiesToRemove = new ArrayList<>();
        List<Cookie> validCookies = new ArrayList<>();
        for (Cookie cookie : memory.loadAll()) {
            if (cookie.expiresAt() < System.currentTimeMillis()) {
                cookiesToRemove.add(cookie);
                memory.remove(cookie);
            } else if (cookie.matches(url)) {
                validCookies.add(cookie);
            }
        }
        if (null != persistent) persistent.removeAll(cookiesToRemove);
        return validCookies;
    }

    synchronized public void clear() {
        memory.clear();
        if (null != persistent) persistent.clear();
    }
}
