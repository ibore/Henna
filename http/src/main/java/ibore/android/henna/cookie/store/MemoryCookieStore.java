package ibore.android.henna.cookie.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.Cookie;

/**
 * Created by Administrator on 2017/6/9.
 */

public class MemoryCookieStore implements ICookieStore {

    private final List<Cookie> cookies = new ArrayList<>();

    @Override
    public List<Cookie> loadAll() {
        return cookies;
    }

    @Override
    public void saveAll(Collection<Cookie> cookies) {
        cookies.addAll(cookies);
    }

    @Override
    public void remove(Cookie cookie) {
        cookies.remove(cookie);
    }

    @Override
    public void removeAll(Collection<Cookie> cookies) {
        cookies.removeAll(cookies);
    }

    @Override
    public void clear() {
        cookies.clear();
    }
}
