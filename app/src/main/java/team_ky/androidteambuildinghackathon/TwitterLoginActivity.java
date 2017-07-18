package team_ky.androidteambuildinghackathon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

/**
 * Created by fujikinaga on 2017/07/18.
 */

public class TwitterLoginActivity extends AppCompatActivity {

    public static String RET_TOKEN = "RET_TOKEN";
    public static String RET_SECRET = "RET_SECRET";
    public static String RET_RESULT_TYPE = "RET_RESULT_TYPE";

    private TwitterAuthClient mTwitterAuthClient;

    public enum ResultType {
        Success, LoginError
    }

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, TwitterLoginActivity.class);
    }

    public static void logOut() {
        Twitter.getSessionManager().clearActiveSession();
        Twitter.logOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTwitterAuthClient = new TwitterAuthClient();
        mTwitterAuthClient.authorize(this, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                TwitterSession session = Twitter.getSessionManager().getActiveSession();
                Twitter.getSessionManager().setActiveSession(result.data);
                TwitterAuthToken authToken = session.getAuthToken();
                final String token = authToken.token;
                final String secret = authToken.secret;
                returnResultSuccess(token, secret);
            }

            @Override
            public void failure(TwitterException e) {
                //feedback
                if (e.getMessage().equals("Authorization failed, request was canceled.")) {
                    Intent intent = new Intent();
                    setResult(Activity.RESULT_CANCELED, intent);
                    finish();
                } else {
                    returnResultError();
                }
            }
        });
    }

    private void returnResultError() {
        Intent intent = new Intent();
        intent.putExtra(RET_RESULT_TYPE, ResultType.LoginError);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void returnResultSuccess(String token, String secret) {
        Intent intent = new Intent();
        intent.putExtra(RET_TOKEN, token);
        intent.putExtra(RET_SECRET, secret);
        intent.putExtra(RET_RESULT_TYPE, ResultType.Success);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mTwitterAuthClient != null) {
            mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onDestroy() {
        mTwitterAuthClient = null;
        super.onDestroy();
    }
}
