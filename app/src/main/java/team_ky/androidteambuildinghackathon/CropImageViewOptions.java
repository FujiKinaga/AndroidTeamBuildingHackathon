package team_ky.androidteambuildinghackathon;

import android.util.Pair;

import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * Created by okadaakihito on 2017/07/14.
 */

public class CropImageViewOptions {
    public CropImageView.ScaleType mScaleType = CropImageView.ScaleType.CENTER_INSIDE;
    public CropImageView.CropShape mCropShape = CropImageView.CropShape.RECTANGLE;
    public CropImageView.Guidelines mGuidelines = CropImageView.Guidelines.ON;
    public Pair<Integer, Integer> mAspectRatio = new Pair<>(1, 1);
    public boolean mIsAutoZoomEnabled = true;
    public int mIntMaxZoomLevel;
    public boolean mIsFixAspectRatio = true;
    public boolean mIsMultiTouch = false;
    public boolean mIsShowCropOverlay = true;
    public boolean mIsShowProgressBar = false;
}
