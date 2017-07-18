package team_ky.androidteambuildinghackathon;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;
import io.fabric.sdk.android.Fabric;

/**
 * Created by fujikinaga on 2017/07/14.
 */

public class App extends Application {
    private static final String TAG = Application.class.getSimpleName();
    private final static String TWITTER_CONSUMER_KEY = "xxYfyEUnFF5MFNluVS682A";
    private final static String TWITTER_CONSUMER_SECRET = "O01j5maUGzieOtGmss7gZrflT1VuQ9P4FbNwbPXWw";

    private static final SyncHttpClient sSyncHttpClient = new SyncHttpClient();
    private static final AsyncHttpClient sAsyncHttpClient = new AsyncHttpClient();

    public static AsyncHttpClient getClient() {
        return sAsyncHttpClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET);
        Fabric.with(this, new Twitter(authConfig), new Crashlytics());

        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
            }
        });
    }
}
