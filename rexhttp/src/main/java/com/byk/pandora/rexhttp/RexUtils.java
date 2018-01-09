package com.byk.pandora.rexhttp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.internal.Util;
import okio.Buffer;

/**
 * Created by Byk on 2017/12/11.
 *
 * @author Byk
 */
public class RexUtils {

    public static final String UTF8 = "UTF-8";
    public static final Charset CHARSET_UTF8 = Charset.forName(UTF8);

    public static final String METHOD_GET = "get";
    public static final String METHOD_POST = "post";

    public static final String CHAR_EQUAL = "=";
    public static final String CHAR_AND = "&";
    public static final String CHAR_QUERY = "?";

    public static final String CONTENT_TYPE_APK = "application/vnd.android.package-archive";
    public static final String CONTENT_TYPE_PNG = "image/png";
    public static final String CONTENT_TYPE_JPG = "image/jpg";

    public static final String MEDIA_TYPE_TEXT = "text";
    public static final String MEDIA_TYPE_SUBTYPE_FORM = "x-www-form-urlencoded";
    public static final String MEDIA_TYPE_SUBTYPE_JSON = "json";
    public static final String MEDIA_TYPE_SUBTYPE_XML = "xml";
    public static final String MEDIA_TYPE_SUBTYPE_HTML = "html";

    public static String createUrlFromParams(String url, Map<String, String> params) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (url.indexOf(CHAR_AND) > 0 || url.indexOf(CHAR_QUERY) > 0) {
                sb.append(CHAR_AND);
            } else {
                sb.append(CHAR_QUERY);
            }

            for (Map.Entry<String, String> urlParams : params.entrySet()) {
                String urlValues = urlParams.getValue();
                sb.append(urlParams.getKey())
                  .append(CHAR_EQUAL)
                  .append(urlValues)
                  .append(CHAR_AND);
            }

            sb.deleteCharAt(sb.length() - 1);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String transBytes(byte[] content, MediaType contentType) {
        Buffer buffer = new Buffer().write(content);
        try {
            Charset newCharset = (contentType != null) ? contentType.charset(CHARSET_UTF8) : CHARSET_UTF8;
            Charset charset = Util.bomAwareCharset(buffer, newCharset);
            return buffer.readString(charset);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Util.closeQuietly(buffer);
        }
        return "";
    }

    public static boolean isPlainText(MediaType mediaType) {
        if (mediaType == null) {
            return false;
        }

        String type = mediaType.type();
        if (type != null && type.equals(MEDIA_TYPE_TEXT)) {
            return true;
        }

        String subtype = mediaType.subtype();
        if (subtype != null) {
            subtype = subtype.toLowerCase();
            if (subtype.contains(MEDIA_TYPE_SUBTYPE_FORM) || subtype.contains(MEDIA_TYPE_SUBTYPE_JSON) ||
                subtype.contains(MEDIA_TYPE_SUBTYPE_XML) || subtype.contains(MEDIA_TYPE_SUBTYPE_HTML)) {
                return true;
            }
        }
        return false;
    }

    public static <T> T checkNotNull(T t, String message) {
        if (t == null) {
            throw new NullPointerException(message);
        }
        return t;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext()
                                                                   .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == manager) {
            return false;
        }
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (null == info || !info.isAvailable()) {
            return false;
        }
        return true;
    }

}
