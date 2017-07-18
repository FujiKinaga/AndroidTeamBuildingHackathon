package team_ky.androidteambuildinghackathon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

/**
 * Created by fujikinaga on 2017/07/18.
 */

public class FacebookLoginActivity extends AppCompatActivity {

    public static String RET_AUTH_TOKEN = "RET_AUTH_TOKEN";
    public static String RET_RESULT_TYPE = "RET_RESULT_TYPE";

    private CallbackManager mCallbackManager;

    public enum ResultType {
        Success, LoginError
    }

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, FacebookLoginActivity.class);
    }

    public static void logOut() {
        LoginManager.getInstance().logOut();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                final String authToken = loginResult.getAccessToken().getToken();
                returnResultSuccess(authToken);
            }

            @Override
            public void onCancel() {
                Intent intent = new Intent();
                setResult(Activity.RESULT_CANCELED, intent);
                finish();
            }

            @Override
            public void onError(FacebookException exception) {
                if (!isFinishing()) {
                    returnResultError();
                }
            }
        });

        LoginManager.getInstance().logInWithPublishPermissions(this, Arrays.asList("publish_actions"));
    }

    private void returnResultError() {
        if (isFinishing()) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(RET_RESULT_TYPE, ResultType.LoginError);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void returnResultSuccess(String authToken) {
        if (isFinishing()) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(RET_AUTH_TOKEN, authToken);
        intent.putExtra(RET_RESULT_TYPE, ResultType.Success);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        mCallbackManager = null;
        super.onDestroy();
    }
}
