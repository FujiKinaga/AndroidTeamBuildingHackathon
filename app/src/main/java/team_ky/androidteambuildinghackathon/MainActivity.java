package team_ky.androidteambuildinghackathon;

import static android.R.id.message;

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

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

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
        if (mMovieInfo.getAudioUrl() != null && mMovieInfo.getImageUrl() != null) {
            convertToMovie();
        }
/*
        if (mMovieInfo.getAudioUrl() != null && mMovieInfo.getImageUrl() != null) {
            startActivity(ShareToInstagramActivity.createIntent(this, mMovieInfo));
        } else {
            if (mMovieInfo.getAudioUrl() == null) {
                showSnackBar(getString(R.string.not_finish_downloading_sound));
            }
            if (mMovieInfo.getImageUrl() == null) {
                showSnackBar(getString(R.string.not_finish_setting_image));
            }
        }
*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.lbl_download_sound);

        mMovieInfo = new MovieInfo();
        mMovieInfo.setMovieUrl(getCacheDir().getAbsolutePath());
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

    private void convertToMovie() {
        FFmpeg ffmpeg = FFmpeg.getInstance(this);
        String[] cmd = {String.format("ffmpeg -y -i %s -i %s %s", mMovieInfo.getImageUrl(), mMovieInfo.getAudioUrl(), mMovieInfo.getMovieUrl())};
        try {
            // to execute "ffmpeg -version" command you just need to pass "-version"
            ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                @Override
                public void onStart() {
                    Log.e(TAG, "execute onStart");
                }

                @Override
                public void onProgress(String message) {
                    Log.e(TAG, "execute onProgress" + message);
                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "execute onFailure" + message);
                }

                @Override
                public void onSuccess(String message) {
                    Log.e(TAG, "execute onSuccess" + message);
                }

                @Override
                public void onFinish() {
                    Log.e(TAG, "execute onFinish" + message);
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // Handle if FFmpeg is already running
            Log.e(TAG, "FFmpegCommandAlreadyRunningException" + e.getMessage());
        }
    }
}
