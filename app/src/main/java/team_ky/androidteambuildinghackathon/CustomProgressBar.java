package team_ky.androidteambuildinghackathon;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

/**
 * Created by okadaakihito on 2017/07/14.
 */

public class CustomProgressBar extends FrameLayout {

    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @OnTouch(R.id.progress_mask)
    boolean onTouchProgressMask(View v, MotionEvent event) {
        return true;
    }

    public CustomProgressBar(@NonNull Context context) {
        super(context);
        init();
    }

    public CustomProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomProgressBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        if (isInEditMode()) {
            return;
        }
        LayoutInflater.from(getContext()).inflate(R.layout.progress_bar, this, true);
        ButterKnife.bind(this);

        setProgressBarAnimation();
    }

    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    private void setProgressBarAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.6f, 0.9f);
        alphaAnimation.setDuration(3000);
        alphaAnimation.setInterpolator(new CycleInterpolator(-1));
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        mProgressBar.startAnimation(alphaAnimation);
    }
}
