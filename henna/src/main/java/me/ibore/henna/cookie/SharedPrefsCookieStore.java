package me.ibore.henna.cookie;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;

/**
 * Created by Administrator on 2017/6/9.
 */

public class SharedPrefsCookieStore implements CookieStore {

    private static final String COOKIE_PREFS = "Henna_Cookies_Prefs";

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
        editor.apply();
    }

    @Override
    public void remove(Cookie cookie) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(createCookieKey(cookie));
        editor.apply();
    }

    @Override
    public void removeAll(Collection<Cookie> cookies) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (Cookie cookie : cookies) {
            editor.remove(createCookieKey(cookie));
        }
        editor.apply();
    }

    @Override
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }

    private static String createCookieKey(Cookie cookie) {
        return (cookie.secure() ? "https" : "http") + "://" + cookie.domain() + cookie.path() + "|" + cookie.name();
    }

    public static class SerializableCookie implements Serializable {

        private static final String TAG = SerializableCookie.class.getSimpleName();

        private static final long serialVersionUID = -237423847293248247L;

        private transient Cookie cookie;

        public String encode(Cookie cookie) {
            this.cookie = cookie;

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = null;

            try {
                objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
                objectOutputStream.writeObject(this);
            } catch (IOException e) {
                Log.d(TAG, "IOException in encodeCookie", e);
                return null;
            } finally {
                if (objectOutputStream != null) {
                    try {
                        // Closing a ByteArrayOutputStream has no effect, it can be used later (and is used in the return statement)
                        objectOutputStream.close();
                    } catch (IOException e) {
                        Log.d(TAG, "Stream not closed in encodeCookie", e);
                    }
                }
            }

            return byteArrayToHexString(byteArrayOutputStream.toByteArray());
        }

        /**
         * Using some super basic byte array &lt;-&gt; hex conversions so we don't
         * have to rely on any large Base64 libraries. Can be overridden if you
         * like!
         *
         * @param bytes byte array to be converted
         * @return string containing hex values
         */
        private static String byteArrayToHexString(byte[] bytes) {
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte element : bytes) {
                int v = element & 0xff;
                if (v < 16) {
                    sb.append('0');
                }
                sb.append(Integer.toHexString(v));
            }
            return sb.toString();
        }

        public Cookie decode(String encodedCookie) {

            byte[] bytes = hexStringToByteArray(encodedCookie);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    bytes);

            Cookie cookie = null;
            ObjectInputStream objectInputStream = null;
            try {
                objectInputStream = new ObjectInputStream(byteArrayInputStream);
                cookie = ((SerializableCookie) objectInputStream.readObject()).cookie;
            } catch (IOException e) {
                Log.d(TAG, "IOException in decodeCookie", e);
            } catch (ClassNotFoundException e) {
                Log.d(TAG, "ClassNotFoundException in decodeCookie", e);
            } finally {
                if (objectInputStream != null) {
                    try {
                        objectInputStream.close();
                    } catch (IOException e) {
                        Log.d(TAG, "Stream not closed in decodeCookie", e);
                    }
                }
            }
            return cookie;
        }

        /**
         * Converts hex values from strings to byte array
         *
         * @param hexString string of hex-encoded values
         * @return decoded byte array
         */
        private static byte[] hexStringToByteArray(String hexString) {
            int len = hexString.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character
                        .digit(hexString.charAt(i + 1), 16));
            }
            return data;
        }

        private static long NON_VALID_EXPIRES_AT = -1L;

        private void writeObject(ObjectOutputStream out) throws IOException {
            out.writeObject(cookie.name());
            out.writeObject(cookie.value());
            out.writeLong(cookie.persistent() ? cookie.expiresAt() : NON_VALID_EXPIRES_AT);
            out.writeObject(cookie.domain());
            out.writeObject(cookie.path());
            out.writeBoolean(cookie.secure());
            out.writeBoolean(cookie.httpOnly());
            out.writeBoolean(cookie.hostOnly());
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
            Cookie.Builder builder = new Cookie.Builder();

            builder.name((String) in.readObject());

            builder.value((String) in.readObject());

            long expiresAt = in.readLong();
            if (expiresAt != NON_VALID_EXPIRES_AT) {
                builder.expiresAt(expiresAt);
            }

            final String domain = (String) in.readObject();
            builder.domain(domain);

            builder.path((String) in.readObject());

            if (in.readBoolean())
                builder.secure();

            if (in.readBoolean())
                builder.httpOnly();

            if (in.readBoolean())
                builder.hostOnlyDomain(domain);

            cookie = builder.build();
        }
    }
}
