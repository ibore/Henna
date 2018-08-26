package ibore.android.henna.cookie;

import java.util.Collection;
import java.util.List;

import okhttp3.Cookie;

public interface CookieStore {

    List<Cookie> loadAll();

    void saveAll(Collection<Cookie> cookies);

    void remove(Cookie cookie);

    void removeAll(Collection<Cookie> cookies);

    void clear();

}
