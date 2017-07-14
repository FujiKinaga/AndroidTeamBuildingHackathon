package team_ky.androidteambuildinghackathon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by okadaakihito on 2017/07/14.
 */

public class CropArthurActivity extends AppCompatActivity implements CropImageView.OnSetImageUriCompleteListener, CropImageView.OnCropImageCompleteListener {

    private static final String TAG = CropArthurActivity.class.getSimpleName();
    private static final int COMPRESS_QUALITY = 100;
    private final static String ARG_CROP_CASE = "crop_case";
    private static final String ARG_SELECTED_IMAGE_URI = "selected_image_uri";
    public static final String RET_CROPPED_IMAGE_URI = "cropped_image_uri";

    public enum CropCase {
        MOVIE_IMAGE("movie_cover.png");

        private String mFileName;

        CropCase(String fileName) {
            mFileName = fileName;
        }

        public String getFileName() {
            return mFileName;
        }
    }

    @BindView(R.id.crop_image_view) CropImageView mCropImageView;
    @BindView(R.id.toolbar) Toolbar mToolbar;

    private CropCase mCropCase;

    public static Intent createIntent(@NonNull Context context, @NonNull String selectedImage, CropCase cropCase) {
        Intent intent = new Intent(context, CropArthurActivity.class);
        intent.putExtra(ARG_SELECTED_IMAGE_URI, selectedImage);
        intent.putExtra(ARG_CROP_CASE, cropCase);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_crop_arthur);
        ButterKnife.bind(this);

        initActionBar();

        initVariables();
    }

    private void initVariables() {
        Uri uri = null;
        if (getIntent().hasExtra(ARG_SELECTED_IMAGE_URI)) {
            String strUrl = getIntent().getStringExtra(ARG_SELECTED_IMAGE_URI);
            uri = Uri.parse(strUrl);
        }
        if (getIntent().hasExtra(ARG_CROP_CASE)) {
            mCropCase = (CropCase) getIntent().getSerializableExtra(ARG_CROP_CASE);
        }
        mCropImageView.setOnSetImageUriCompleteListener(this);
        mCropImageView.setOnCropImageCompleteListener(this);
        CropImageViewOptions mCropImageViewOptions = new CropImageViewOptions();
        setCropImageViewOptions(mCropImageViewOptions);
        if (uri == null) {
            return;
        }
        mCropImageView.setImageUriAsync(uri);
    }

    /**
     * Init action bar
     */
    private void initActionBar() {
        mToolbar.setTitle(R.string.lbl_crop_image);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.arthur_menu, menu);
        //this handler is used for preventing displaying toast on long press.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                final View v = findViewById(R.id.main_action_rotate);

                if (v != null) {
                    v.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            return false;
                        }
                    });
                }
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        switch (item.getItemId()) {
            case R.id.main_action_rotate:
                mCropImageView.rotateImage(90);
                break;
            case R.id.main_action_crop:
                mCropImageView.getCroppedImageAsync();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDestroy() {
        mCropImageView.setOnSetImageUriCompleteListener(null);
        mCropImageView.setOnCropImageCompleteListener(null);
        super.onDestroy();
    }

    @Override
    public void onCropImageComplete(CropImageView view, final CropImageView.CropResult result) {
        final Intent intent = new Intent();
        if (result == null || result.getError() != null) {
            Log.e(CropArthurActivity.class.getSimpleName(), getString(R.string.failed_to_crop_image), result.getError());
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
            return;
        }
        if (result.getUri() != null) {
            intent.putExtra(RET_CROPPED_IMAGE_URI, result.getUri().toString());
            setResult(Activity.RESULT_OK, intent);
            finish();
            return;
        }
        if (result.getBitmap() == null) {
            setResult(Activity.RESULT_CANCELED, intent);
            finish();
            return;
        }
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {
                    File file = new File(getCacheDir(), mCropCase.getFileName());
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    result.getBitmap().compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, fileOutputStream);
                    intent.putExtra(RET_CROPPED_IMAGE_URI, Uri.fromFile(file).toString());
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    setResult(Activity.RESULT_OK, intent);
                } catch (Exception e) {
                    Log.e(TAG, "User Image Exception:- " + e != null ? e.getMessage() : "");
                    setResult(Activity.RESULT_CANCELED, intent);
                }
                finish();
            }
        });
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, @Nullable Exception error) {
        if (error == null) {
            Log.i(TAG, "Image load successful by ARG_SELECTED_IMAGE_URI");
            return;
        }
        Log.e(TAG, "Failed to load image by ARG_SELECTED_IMAGE_URI" + error != null ? error.getMessage() : "");
    }

    public void setCropImageViewOptions(CropImageViewOptions options) {
        mCropImageView.setScaleType(options.mScaleType);
        mCropImageView.setCropShape(options.mCropShape);
        mCropImageView.setGuidelines(options.mGuidelines);
        mCropImageView.setAspectRatio(options.mAspectRatio.first, options.mAspectRatio.second);
        mCropImageView.setFixedAspectRatio(options.mIsFixAspectRatio);
        mCropImageView.setMultiTouchEnabled(options.mIsMultiTouch);
        mCropImageView.setShowCropOverlay(options.mIsShowCropOverlay);
        mCropImageView.setShowProgressBar(options.mIsShowProgressBar);
        mCropImageView.setAutoZoomEnabled(options.mIsAutoZoomEnabled);
        mCropImageView.setMaxZoom(options.mIntMaxZoomLevel);
    }
}
