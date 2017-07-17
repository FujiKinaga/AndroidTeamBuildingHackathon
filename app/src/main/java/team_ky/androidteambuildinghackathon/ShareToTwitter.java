package team_ky.androidteambuildinghackathon;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import cz.msebera.android.httpclient.Header;
import team_ky.androidteambuildinghackathon.encode.HttpParameters;
import team_ky.androidteambuildinghackathon.encode.PercentEscaper;

/**
 * Created by fujikinaga on 2017/07/15.
 */

public class ShareToTwitter {

    public static final String VERSION_1_0 = "1.0";

    public static final String ENCODING = "UTF-8";

    public static final String FORM_ENCODED = "application/x-www-form-urlencoded";

    public static final String HTTP_AUTHORIZATION_HEADER = "Authorization";

    public static final String OAUTH_CONSUMER_KEY = "oauth_consumer_key";

    public static final String OAUTH_TOKEN = "oauth_token";

    public static final String OAUTH_TOKEN_SECRET = "oauth_token_secret";

    public static final String OAUTH_SIGNATURE_METHOD = "oauth_signature_method";

    public static final String OAUTH_SIGNATURE = "oauth_signature";

    public static final String OAUTH_TIMESTAMP = "oauth_timestamp";

    public static final String OAUTH_NONCE = "oauth_nonce";

    public static final String OAUTH_VERSION = "oauth_version";

    public static final String OAUTH_CALLBACK = "oauth_callback";

    public static final String OAUTH_CALLBACK_CONFIRMED = "oauth_callback_confirmed";

    public static final String OAUTH_VERIFIER = "oauth_verifier";

    public static final String OUT_OF_BAND = "oob";

    private static final String MAC_NAME = "HmacSHA1";

    private static final String MAC_VALUE = "HMAC-SHA1";

    private static final String POST_TWITTER = "https://upload.twitter.com/1.1/media/upload.json";

    private static final String GET_TWITTER = "https://api.twitter.com/1.1/account/verify_credentials.json";

    private static final String TWEET_TWITTER = "https://api.twitter.com/1.1/statuses/update.json";

    private static final String TWITTER_KEY = "xxYfyEUnFF5MFNluVS682A";

    private static final String TWITTER_SECRET = "O01j5maUGzieOtGmss7gZrflT1VuQ9P4FbNwbPXWw";

    private static final PercentEscaper percentEncoder = new PercentEscaper(
            "-._~", false);

    public static String percentEncode(String s) {
        if (s == null) {
            return "";
        }
        return percentEncoder.escape(s);
    }

    public static String percentDecode(String s) {
        try {
            if (s == null) {
                return "";
            }
            return URLDecoder.decode(s, ENCODING);
            // This implements http://oauth.pbwiki.com/FlexibleDecoding
        } catch (java.io.UnsupportedEncodingException wow) {
            throw new RuntimeException(wow.getMessage(), wow);
        }
    }

    public static String toHeaderElement(String name, String value) {
        return percentEncode(name) + "=\"" + percentEncode(value) + "\"";
    }

    public static String addQueryParameters(String url, String... kvPairs) {
        String queryDelim = url.contains("?") ? "&" : "?";
        StringBuilder sb = new StringBuilder(url + queryDelim);
        for (int i = 0; i < kvPairs.length; i += 2) {
            if (i > 0) {
                sb.append("&");
            }
            sb.append(percentEncode(kvPairs[i]) + "="
                    + percentEncode(kvPairs[i + 1]));
        }
        return sb.toString();
    }

    public static String addQueryParameters(String url, Map<String, String> params) {
        String[] kvPairs = new String[params.size() * 2];
        int idx = 0;
        for (String key : params.keySet()) {
            kvPairs[idx] = key;
            kvPairs[idx + 1] = params.get(key);
            idx += 2;
        }
        return addQueryParameters(url, kvPairs);
    }

    public static HttpParameters getParam(String token) {
        HttpParameters param = new HttpParameters();
        param.put(OAUTH_CONSUMER_KEY, TWITTER_KEY, true);
        param.put(OAUTH_NONCE, generateNonce(), true);
        param.put(OAUTH_SIGNATURE_METHOD, MAC_VALUE, true);
        param.put(OAUTH_TIMESTAMP, generateTimestamp(), true);
        param.put(OAUTH_TOKEN, token, true);
        param.put(OAUTH_VERSION, VERSION_1_0, true);
        return param;
    }

    public static String createOAuthSignatureHeaderEntry(HttpParameters requestParameters) {
        return "OAuth " +
                requestParameters.getAsHeaderElement(OAUTH_CONSUMER_KEY) + ", " +
                requestParameters.getAsHeaderElement(OAUTH_NONCE) + ", " +
                requestParameters.getAsHeaderElement(OAUTH_SIGNATURE) + ", " +
                requestParameters.getAsHeaderElement(OAUTH_SIGNATURE_METHOD) + ", " +
                requestParameters.getAsHeaderElement(OAUTH_TIMESTAMP) + ", " +
                requestParameters.getAsHeaderElement(OAUTH_TOKEN) + ", " +
                requestParameters.getAsHeaderElement(OAUTH_VERSION);
    }

    public static String createOAuthSignature(String method, String baseUrl, String tokenSecret, HttpParameters params) {
        String signature = null;
        try {
            String keyString = percentEncode(TWITTER_SECRET) + '&'
                    + percentEncode(tokenSecret);
            byte[] keyBytes = keyString.getBytes(ENCODING);

            SecretKey key = new SecretKeySpec(keyBytes, MAC_NAME);
            Mac mac = Mac.getInstance(MAC_NAME);
            mac.init(key);

            String sbs = generateSignatureBaseString(method, baseUrl, params);
            byte[] text = sbs.getBytes(ENCODING);

            //Base64.decode(s.getBytes(), Base64.NO_WRAP)
            signature = Base64.encodeToString(mac.doFinal(text), Base64.NO_WRAP).trim();
        } catch (GeneralSecurityException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return signature;
    }

    private static String generateSignatureBaseString(String method, String baseUrl, HttpParameters params) {
        try {
            String normalizedUrl = normalizeRequestUrl(baseUrl);
            String normalizedParams = normalizeRequestParameters(params);

            return method + '&' + percentEncode(normalizedUrl) + '&'
                    + percentEncode(normalizedParams);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String normalizeRequestUrl(String baseUrl) throws URISyntaxException {
        URI uri = new URI(baseUrl);
        String scheme = uri.getScheme().toLowerCase();
        String authority = uri.getAuthority().toLowerCase();
        boolean dropPort = (scheme.equals("http") && uri.getPort() == 80)
                || (scheme.equals("https") && uri.getPort() == 443);
        if (dropPort) {
            // find the last : in the authority
            int index = authority.lastIndexOf(":");
            if (index >= 0) {
                authority = authority.substring(0, index);
            }
        }
        String path = uri.getRawPath();
        if (path == null || path.length() <= 0) {
            path = "/"; // conforms to RFC 2616 section 3.2.2
        }
        // we know that there is no query and no fragment here.
        return scheme + "://" + authority + path;
    }

    private static String normalizeRequestParameters(HttpParameters requestParameters) throws IOException {
        if (requestParameters == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = requestParameters.keySet().iterator();

        for (int i = 0; iter.hasNext(); i++) {
            String param = iter.next();

            if (OAUTH_SIGNATURE.equals(param) || "realm".equals(param)) {
                continue;
            }

            if (i > 0) {
                sb.append("&");
            }

            sb.append(requestParameters.getAsQueryString(param));
        }
        return sb.toString();
    }

    protected static String generateTimestamp() {
        return Long.toString(System.currentTimeMillis() / 1000L);
    }

    protected static String generateNonce() {
        return Long.toString(new Random().nextLong());
    }

    public static ArrayList<byte[]> readFileToByteArray(File file) throws Exception {
        ArrayList<byte[]> array = new ArrayList<>();
        byte[] b = new byte[(int) file.length()];

        FileInputStream fis = new FileInputStream(file);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (fis.read(b) > 0) {
            baos.write(b);
        }
        baos.flush();
        baos.close();
        fis.close();

        b = baos.toByteArray();

        int GOMEGA = 1024 * 1024 * 5;
        int begin = 0;
        for (int i = 0; i < (b.length / GOMEGA); i++) {
            begin = (i + 1) * GOMEGA;
            array.add(Arrays.copyOfRange(b, i * GOMEGA, begin));
        }
        array.add(Arrays.copyOfRange(b, begin, b.length));
        return array;
    }

    public static void performShare(final Context context, final String token, final String tokenSecret, final File file, final String message) {
        HttpParameters httpParameters = getParam(token);
        httpParameters.put("command", "INIT", true);
        httpParameters.put("media_type", "video/mp4", true);
        httpParameters.put("total_bytes", String.valueOf(file.length()), true);
        httpParameters.put("media_category", "tweet_video", true);

        RequestParams requestParams = new RequestParams();
        requestParams.put("command", "INIT");
        requestParams.put("media_type", "video/mp4");
        requestParams.put("total_bytes", String.valueOf(file.length()));
        requestParams.put("media_category", "tweet_video");

        httpParameters.put(OAUTH_SIGNATURE, createOAuthSignature("POST", POST_TWITTER, tokenSecret, httpParameters), true);

        App.getClient().addHeader(HTTP_AUTHORIZATION_HEADER, createOAuthSignatureHeaderEntry(httpParameters));
        App.getClient().setTimeout(100000);
        App.getClient().post(context, POST_TWITTER, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ログ", "失敗");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String media_id = response.getString("media_id_string");
                    ArrayList<byte[]> array = ShareToTwitter.readFileToByteArray(file);
                    performAppend(context, media_id, token, tokenSecret, 0, array, message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void performAppend(final Context context, final String media_id, final String token, final String tokenSecret, final int segment_index, final ArrayList<byte[]> list, final String message) {
        HttpParameters httpParameters = getParam(token);

        RequestParams requestParams = new RequestParams();
        requestParams.put("media", new ByteArrayInputStream(list.get(segment_index)), "rb", "application/octet-stream");
        requestParams.put("command", "APPEND");
        requestParams.put("media_id", media_id);
        requestParams.put("segment_index", String.valueOf(segment_index));

        httpParameters.put(OAUTH_SIGNATURE, createOAuthSignature("POST", POST_TWITTER, tokenSecret, httpParameters), true);

        App.getClient().removeHeader(HTTP_AUTHORIZATION_HEADER);
        App.getClient().setTimeout(100000);
        App.getClient().addHeader(HTTP_AUTHORIZATION_HEADER, createOAuthSignatureHeaderEntry(httpParameters));
        App.getClient().post(context, POST_TWITTER, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ログ", "失敗");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (list.size() == segment_index + 1) {
                    performFinalize(context, media_id, token, tokenSecret, message);
                } else {
                    performAppend(context, media_id, token, tokenSecret, segment_index + 1, list, message);
                }
            }
        });
    }

    private static void performFinalize(final Context context, final String media_id, final String token, final String tokenSecret, final String message) {
        HttpParameters httpParameters = getParam(token);
        httpParameters.put("command", "FINALIZE", true);
        httpParameters.put("media_id", media_id, true);

        RequestParams requestParams = new RequestParams();
        requestParams.put("command", "FINALIZE");
        requestParams.put("media_id", media_id);

        httpParameters.put(OAUTH_SIGNATURE, createOAuthSignature("POST", POST_TWITTER, tokenSecret, httpParameters), true);

        App.getClient().removeHeader(HTTP_AUTHORIZATION_HEADER);
        App.getClient().setTimeout(100000);
        App.getClient().addHeader(HTTP_AUTHORIZATION_HEADER, createOAuthSignatureHeaderEntry(httpParameters));
        App.getClient().post(context, POST_TWITTER, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ログ", "失敗");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject processing_info = response.getJSONObject("processing_info");
                    String state = processing_info.getString("state");
                    if (state.equals("succeeded")) {
                        performVideoTweet(context, media_id, token, tokenSecret, message);
                    } else if (state.equals("succeeded")) {
                        Log.e("ログ", "失敗");
                    } else {
                        int check_after_secs = processing_info.getInt("check_after_secs");
                        try {
                            Thread.sleep(check_after_secs * 1000);
                            performCheckStatus(context, media_id, token, tokenSecret, message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void performCheckStatus(final Context context, final String media_id, final String token, final String tokenSecret, final String message) {
        HttpParameters httpParameters = getParam(token);
        httpParameters.put("command", "STATUS", true);
        httpParameters.put("media_id", media_id, true);

        RequestParams requestParams = new RequestParams();
        requestParams.put("command", "STATUS");
        requestParams.put("media_id", media_id);

        httpParameters.put(OAUTH_SIGNATURE, createOAuthSignature("GET", POST_TWITTER, tokenSecret, httpParameters), true);

        App.getClient().removeHeader(HTTP_AUTHORIZATION_HEADER);
        App.getClient().setTimeout(100000);
        App.getClient().addHeader(HTTP_AUTHORIZATION_HEADER, createOAuthSignatureHeaderEntry(httpParameters));
        App.getClient().get(context, POST_TWITTER, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ログ", "失敗");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject processing_info = response.getJSONObject("processing_info");
                    String state = processing_info.getString("state");
                    if (state.equals("succeeded")) {
                        performVideoTweet(context, media_id, token, tokenSecret, message);
                    } else if (state.equals("succeeded")) {
                        Log.e("ログ", "失敗");
                    } else {
                        int check_after_secs = processing_info.getInt("check_after_secs");
                        try {
                            Thread.sleep(check_after_secs * 1000);
                            performCheckStatus(context, media_id, token, tokenSecret, message);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void performVideoTweet(final Context context, final String media_id, String token, String tokenSecret, String message) {
        HttpParameters httpParameters = getParam(token);
        httpParameters.put("status", message, true);
        httpParameters.put("media_ids", media_id, true);

        RequestParams requestParams = new RequestParams();
        requestParams.put("status", message);
        requestParams.put("media_ids", media_id);

        httpParameters.put(OAUTH_SIGNATURE, createOAuthSignature("POST", TWEET_TWITTER, tokenSecret, httpParameters), true);

        App.getClient().removeHeader(HTTP_AUTHORIZATION_HEADER);
        App.getClient().setTimeout(100000);
        App.getClient().addHeader(HTTP_AUTHORIZATION_HEADER, createOAuthSignatureHeaderEntry(httpParameters));
        App.getClient().post(context, TWEET_TWITTER, requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ログ", "失敗");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("ログ", "成功");
            }
        });
    }

    private static void performAuthentication(final Context context, final String token, final String tokenSecret) {
        HttpParameters httpParameters = getParam(token);
        httpParameters.put("skip_status", "true", true);
        httpParameters.put("include_entities", "false", true);

        Map<String, String> map = new HashMap<>();
        map.put("skip_status", "true");
        map.put("include_entities", "false");
        String url = addQueryParameters(GET_TWITTER, map);

        httpParameters.put(OAUTH_SIGNATURE, createOAuthSignature("GET", GET_TWITTER, tokenSecret, httpParameters), true);

        App.getClient().removeHeader(HTTP_AUTHORIZATION_HEADER);
        App.getClient().setTimeout(100000);
        App.getClient().addHeader(HTTP_AUTHORIZATION_HEADER, createOAuthSignatureHeaderEntry(httpParameters));
        App.getClient().get(context, url, new JsonHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e("ログ", "失敗");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            }
        });
    }
}
