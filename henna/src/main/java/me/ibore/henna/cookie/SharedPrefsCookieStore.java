package me.ibore.henna.cookie;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;

/**
 * Created by Administrator on 2017/6/9.
 */

public class SharedPrefsCookieStore implements CookieStore {

    private static final String COOKIE_PREFS = "OkHttp_Cookies_Prefs";

    private final SharedPreferences sharedPreferences;

    public SharedPrefsCookieStore(Context context) {
        this(context.getSharedPreferences(COOKIE_PREFS, Context.MODE_PRIVATE));
    }

    public SharedPrefsCookieStore(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }


    @Override
    public List<Cookie> loadAll() {
        List<Cookie> cookies = new ArrayList<>(sharedPreferences.getAll().size());
        for (Map.Entry<String, ?> entry : sharedPreferences.getAll().entrySet()) {
            String serializedCookie = (String) entry.getValue();
            Cookie cookie = new SerializableCookie().decode(serializedCookie);
            cookies.add(cookie);
        }
        return cookies;
    }

    @Override
    public void saveAll(Collection<Cookie> cookies) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Cookie cookie : cookies) {
            editor.putString(createCookieKey(cookie), new SerializableCookie().encode(cookie));
        }
        editor.commit();
    }

    @Override
    public void remove(Cookie cookie) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(createCookieKey(cookie));
        editor.commit();
    }

    @Override
    public void removeAll(Collection<Cookie> cookies) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Cookie cookie : cookies) {
            editor.remove(createCookieKey(cookie));
        }
        editor.commit();
    }

    @Override
    public void clear() {
        sharedPreferences.edit().clear().commit();
    }

    private static String createCookieKey(Cookie cookie) {
        return (cookie.secure() ? "https" : "http") + "://" + cookie.domain() + cookie.path() + "|" + cookie.name();
    }
}
