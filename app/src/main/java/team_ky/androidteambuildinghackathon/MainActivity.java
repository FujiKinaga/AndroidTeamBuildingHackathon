package team_ky.androidteambuildinghackathon;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    public static final int REQUEST_STORAGE = 240;

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
        if (url.startsWith(getString(R.string.http_protocol))) {
            startDownloadBgmFile(url);
        } else {
            showSnackBar(getString(R.string.please_set_correct_url));
        }
    }

    @OnClick(R.id.pick_cover_image_button)
    void onClickOk(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(photoPickerIntent, ACTIVITY_REQUEST_CODE_ACTION_PICK);
    }

    @OnClick(R.id.share)
    void onShareInstagram(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_STORAGE);
            return;
        }
        navigateToShareInstagram();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.lbl_movie_resource);

        mMovieInfo = new MovieInfo();
        mMovieInfo.setMovieUrl(getExternalCacheDir().getAbsolutePath());
        createFile();
    }

    private void createFile() {
        File folder = new File(mMovieInfo.getMoviePath());
        boolean success = true;
        if (!folder.exists()) {
            folder.mkdir();
        }
        if (folder.canWrite()) {
            final File file = new File(mMovieInfo.getMovieUrl());
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showSnackBar(getString(R.string.can_not_write_file));
        }
    }

    private void startDownloadBgmFile(String url) {
        try {
            FileDownloader.downloadFile(url, getCacheDir().getAbsolutePath(), mFileDownloadListener);
        } catch (Exception e) {
            String message = "Exception when running startDownloadBgmFile() : " + e.getMessage();
            Log.e(TAG, message);
            showSnackBar(getString(R.string.fail_downloading_sound));

        }
    }

    private FileDownloader.FileDownloadListener mFileDownloadListener = new FileDownloader.FileDownloadListener() {

        @Override
        public void downloadStarted() {
            showSnackBar(getString(R.string.start_downloading_sound));
        }

        @Override
        public void downloadFinished(String filePath) {
            mMovieInfo.setAudioUrl(filePath);
            showSnackBar(getString(R.string.finish_downloading_sound));
        }

        @Override
        public void downloadCancled() {
            showSnackBar(getString(R.string.cancel_downloading_sound));

        }

        @Override
        public void downloadFailed() {
            showSnackBar(getString(R.string.fail_downloading_sound));
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigateToShareInstagram();
                } else {
                    finish();
                }
                break;
        }
    }

    private void navigateToShareInstagram() {
        if (mMovieInfo.isAvailable()) {
            startActivity(ShareActivity.createIntent(this, mMovieInfo));
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

    private void showSnackBar(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_SHORT).show();
    }
}
