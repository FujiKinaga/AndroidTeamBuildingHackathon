package team_ky.androidteambuildinghackathon;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final int ACTIVITY_REQUEST_CODE_ACTION_PICK = 220;
    public static final int ACTIVITY_REQUEST_CODE_CROP_ARTHUR = 230;

    private MovieInfo mMovieInfo;

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
            startDownloadBgmFile(url);
        } else {
            showSnackBar(getString(R.string.please_set_correct_url));
        }
    }

    @OnClick(R.id.ok_button)
    void onClickOk(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(photoPickerIntent, ACTIVITY_REQUEST_CODE_ACTION_PICK);
    }

    @OnClick(R.id.share_instagram)
    void onShareInstagram(View view) {
        if (mMovieInfo.isAvailable()) {
            startActivity(ShareToInstagramActivity.createIntent(this, mMovieInfo));
        } else {
            if (!mMovieInfo.isAvailableAudio()) {
                showSnackBar(getString(R.string.not_finish_downloading_sound));
            }
            if (!mMovieInfo.isAvailableImage()) {
                showSnackBar(getString(R.string.not_finish_setting_image));
            }
            if (!mMovieInfo.isAvailableMovie()) {
                showSnackBar(getString(R.string.not_finish_setting_movie_path));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.lbl_download_sound);

        mMovieInfo = new MovieInfo();
        mMovieInfo.setMovieUrl(getExternalCacheDir().getAbsolutePath());
        createFile();
    }

    private void createFile() {
        File folder = new File(mMovieInfo.getMoviePath());
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            showSnackBar("Do something on success");
        } else {
            showSnackBar("Do something else on failure");
        }
        if (folder.canWrite()) {
            final File file = new File(mMovieInfo.getMovieUrl());
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showSnackBar("can not write file ");
        }
    }

    private void startDownloadBgmFile(String url) {
        try {
            FileDownloader.downloadFile(url, getCacheDir().getAbsolutePath(), mFileDownloadListener);
        } catch (Exception e) {
            String message = "Exception when running startDownloadBgmFile() : " + e.getMessage();
            Log.e(TAG, message);
            showSnackBar(getString(R.string.failed_download_sound));

        }
    }

    private FileDownloader.FileDownloadListener mFileDownloadListener = new FileDownloader.FileDownloadListener() {

        @Override
        public void downloadStarted() {
            showSnackBar(getString(R.string.success_cropped_image));

        }

        @Override
        public void downloadFinished(String filePath) {
            mMovieInfo.setAudioUrl(filePath);
            showSnackBar(getString(R.string.success_cropped_image));

        }

        @Override
        public void downloadCancled() {
            showSnackBar(getString(R.string.success_cropped_image));

        }

        @Override
        public void downloadFailed() {
            showSnackBar(getString(R.string.success_cropped_image));
        }

    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case ACTIVITY_REQUEST_CODE_ACTION_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                Intent intent = CropArthurActivity.createIntent(this, ((Uri) data.getData()).toString(), CropArthurActivity.CropCase.MOVIE_IMAGE);
                startActivityForResult(intent, ACTIVITY_REQUEST_CODE_CROP_ARTHUR);
                break;
            case ACTIVITY_REQUEST_CODE_CROP_ARTHUR:
                if (data == null || !data.hasExtra(CropArthurActivity.RET_CROPPED_IMAGE_URI)) {
                    return;
                }
                showSnackBar(getString(R.string.success_cropped_image));
                mMovieInfo.setImageUrl(data.getStringExtra(CropArthurActivity.RET_CROPPED_IMAGE_URI));
                break;
            default:
                break;
        }
    }

    private void showSnackBar(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
