package team_ky.androidteambuildinghackathon;

import android.app.Application;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.SyncHttpClient;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.ILoadCallback;

/**
 * Created by fujikinaga on 2017/07/14.
 */

public class App extends Application {
    private static final String TAG = Application.class.getSimpleName();

    private static final SyncHttpClient sSyncHttpClient = new SyncHttpClient();

    public static SyncHttpClient getClient() {
        return sSyncHttpClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
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
