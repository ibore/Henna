package me.ibore.http.cookie;


import java.util.ArrayList;
import java.util.List;

import me.ibore.http.cookie.store.CookieStore;
import me.ibore.http.cookie.store.MemoryCookieStore;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by Administrator on 2017/6/8.
 */

public final class XCookieStore implements okhttp3.CookieJar {

    private CookieStore memory;

    private CookieStore presistent;

    public XCookieStore() {
        memory = new MemoryCookieStore();
    }

    public XCookieStore(CookieStore cookieStore) {
        memory = new MemoryCookieStore();
        this.presistent = cookieStore;
        memory.saveAll(presistent.loadAll());
    }

    @Override
    synchronized public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        memory.saveAll(cookies);
        if (null != presistent) presistent.saveAll(filterPersistentCookies(cookies));
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
        if (null != presistent) presistent.removeAll(cookiesToRemove);
        return validCookies;
    }

    synchronized public void clearSession() {
        memory.clear();
        if (null != presistent) memory.saveAll(presistent.loadAll());
    }

    synchronized public void clear() {
        memory.clear();
        if (null != presistent) presistent.clear();
    }
}
