package team_ky.androidteambuildinghackathon;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private String mAudioPath;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.input_url)
    EditText mInputUrl;

    @BindView(R.id.root_layout)
    CoordinatorLayout mRootLayout;

    @OnClick(R.id.download_sound_button)
    void onClickDownloadSound(View view) {
        String url = mInputUrl.getText().toString();
        if (url.startsWith("http")) {
            Snackbar.make(mRootLayout, "Start downloading sound", Snackbar.LENGTH_SHORT).show();
            startDownloadBgmFile(url);
        } else {
            Snackbar.make(mRootLayout, "Please set correct url", Snackbar.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.ok_button)
    void onClickOk(View view) {
        // move to xxx
    }

    @OnClick(R.id.share_instagram)
    void onShareInstagram(View view) {
        if (mAudioPath != null) {
            Intent intent = new Intent(this, ShareToInstagramActivity.class);
            startActivity(ShareToInstagramActivity.createIntent(this, mAudioPath, null));
        } else {
            Snackbar.make(mRootLayout, "not finish downloading sound", Snackbar.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.lbl_download_sound);
    }

    private void startDownloadBgmFile(String url) {
        try {
            new FileDownloader().downloadFile(url, getCacheDir().getAbsolutePath(), mFileDownloadListener);
        } catch (Exception e) {
            String message = "Exception when running startDownloadBgmFile() : " + e.getMessage();
            Log.e(TAG, message);
            Snackbar.make(mRootLayout, "Failed download sound", Snackbar.LENGTH_SHORT).show();
        }
    }

    private FileDownloader.FileDownloadListener mFileDownloadListener = new FileDownloader.FileDownloadListener() {

        @Override
        public void downloadStarted() {
        }

        @Override
        public void downloadFinished(String filePath) {
            mAudioPath = filePath;
            Snackbar.make(mRootLayout, "finished downloading", Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void downloadCancled() {

        }

        @Override
        public void downloadFailed() {

        }

    };
}
