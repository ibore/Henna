package ibore.android.henna.cookie;


import java.util.ArrayList;
import java.util.List;

import ibore.android.henna.cookie.store.ICookieStore;
import ibore.android.henna.cookie.store.MemoryCookieStore;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by Administrator on 2017/6/8.
 */

public final class CookieStore implements okhttp3.CookieJar {

    private ICookieStore memory;

    private ICookieStore persistent;

    public CookieStore() {
        memory = new MemoryCookieStore();
    }

    public CookieStore(ICookieStore cookieStore) {
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

    synchronized public void clearSession() {
        memory.clear();
        if (null != persistent) memory.saveAll(persistent.loadAll());
    }

    synchronized public void clear() {
        memory.clear();
        if (null != persistent) persistent.clear();
    }
}
