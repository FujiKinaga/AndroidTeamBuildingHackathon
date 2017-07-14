package team_ky.androidteambuildinghackathon;

import android.content.Context;
import android.util.Log;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.File;
import java.io.FileNotFoundException;

import cz.msebera.android.httpclient.Header;

/**
 * Created by fujikinaga on 2017/07/15.
 */

public class ShareToFacebook {

    private static final String ACCESS_TOKEN = "access_token";

    private static final String SOURCE = "source";

    private static final String DESCRIPTION = "description";

    private static final String OCTET_STREAM = "application/octet-stream";

    private static final String FACEBOOK_SHARE_URL = "https://graph-video.facebook.com/me/videos";

    public static void performShare(final Context context, String token, final File movie, String description) {
        RequestParams param = new RequestParams();
        try {
            param.put(ACCESS_TOKEN, token);
            param.put(SOURCE, movie, OCTET_STREAM, "share.mp4");
            param.put(DESCRIPTION, description);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //190　400
        App.getClient().removeAllHeaders();
        App.getClient().setTimeout(100000);
        App.getClient().post(context, FACEBOOK_SHARE_URL, param, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("ログ", "失敗");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.e("ログ", "成功");
            }
        });
    }

    public static void performStoryPost(String title, String description, String videoUrl, String thumbUrl) {
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("fb:app_id", "977612005613710")
                .putString("og:type", "video.movie")
                .putString("og:title", "One Direction - Drag Me Down")
                .putString("og:description", "One Direction&#39;s new single Drag Me Down is out now! iTunes: http://smarturl.it/1DdmdIT Apple Music: http://smarturl.it/1DdmdAM Spotify: http://smarturl.it/1D...")
                .putString("og:url", "https://www.youtube.com/watch?v=Jwgf3wmiA04")
                .putString("og:image", "https://i.ytimg.com/vi/Jwgf3wmiA04/maxresdefault.jpg")
                .build();
        ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                .setActionType("video.watches")
                .putString("fb:explicitly_shared", "true")
                .putObject("movie", object)
                .build();
        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("movie")
                .setAction(action)
                .build();
        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.e("ログ", result.toString());
            }

            @Override
            public void onCancel() {
                Log.e("ログ", "cancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("ログ", error.toString());
            }
        });
    }
}
