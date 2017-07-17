package team_ky.androidteambuildinghackathon;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareActivity extends AppCompatActivity {

    private static final String TAG = ShareActivity.class.getSimpleName();
    private static final String ARG_MOVIE_INFO = "ARG_MOVIE_INFO";

    @BindView(R.id.preview_video_view) SquareVideoView mPreviewVideoView;

    @BindView(R.id.toolbar) Toolbar mToolbar;

    @BindView(R.id.root_layout) CoordinatorLayout mRootLayout;

    @BindView(R.id.custom_progress_bar) CustomProgressBar mCustomProgressBar;

    @OnClick(R.id.share_button_instagram)
    public void onInstagram() {
        ShareToInstagram.performShare(this, new File(mMovieInfo.getMovieUrl()));
    }

    @OnClick(R.id.share_button_facebook)
    public void onFacebook() {
        ShareToFacebook.performShare(this, "", new File(mMovieInfo.getMovieUrl()), "from nana");
    }

    @OnClick(R.id.share_button_twitter)
    public void onTwitter() {
        ShareToTwitter.performShare(this, "806367043893207040-OHVlnlEfFc03OYxBhy0ZdCug92UJ6ZG", "T8gkcbvdZMYeNZcxHjc5bpMctWhPa864QtleTIbM2gABz", new File(mMovieInfo.getMovieUrl()), "from nana");
    }

    private MovieInfo mMovieInfo;

    public static Intent createIntent(Context context, MovieInfo movieInfo) {
        Intent intent = new Intent(context, ShareActivity.class);
        intent.putExtra(ARG_MOVIE_INFO, movieInfo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.share_to_instagram);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        createMovie();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showProcessView() {
        mCustomProgressBar.setVisibility(View.VISIBLE);
    }

    public void hideProcessView() {
        mCustomProgressBar.setVisibility(View.GONE);
    }

    private int getSoundDuration() {
        Uri uri = Uri.parse(mMovieInfo.getAudioUrl());
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(this, uri);
        String durationStr = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        return Integer.parseInt(durationStr);
    }

    private void createMovie() {
        mMovieInfo = (MovieInfo) getIntent().getParcelableExtra(ARG_MOVIE_INFO);

        try {
            String[] cmd = new String[]{"-y", "-s", "640x640", "-i", mMovieInfo.getImageUrl(), "-i", mMovieInfo.getAudioUrl(), mMovieInfo.getMovieUrl()};
            FFmpeg ffmpeg = FFmpeg.getInstance(ShareActivity.this);
            ffmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
                @Override
                public void onSuccess(String message) {
                    Log.e("onSuccess", message);
                    mPreviewVideoView.setVideoPath(mMovieInfo.getMovieUrl());
                    mPreviewVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    showSnackBar(getString(R.string.finish_creating_movie));
                }

                @Override
                public void onFinish() {
                    Log.e(TAG, "onFinish : ");
                    hideProcessView();
                }

                @Override
                public void onProgress(String message) {
                    Pattern timePattern = Pattern.compile("(?<=time=)[\\d:.]*");
                    Scanner sc = new Scanner(message);

                    String match = sc.findWithinHorizon(timePattern, 0);
                    if (match != null) {
                        String[] matchSplit = match.split(":");
                        if (getSoundDuration() != 0) {
                            float progress = (Integer.parseInt(matchSplit[0]) * 3600 +
                                Integer.parseInt(matchSplit[1]) * 60 +
                                Float.parseFloat(matchSplit[2])) / getSoundDuration();
                            float showProgress = (progress * 100 * 1000);
                            mCustomProgressBar.setProgress((int) showProgress);
                            Log.d(TAG, "=======PROGRESS======== " + showProgress);
                        }
                    }

                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "onFailure : " + message);
                }

                @Override
                public void onStart() {
                    Log.e(TAG, "onStart : ");
                    showProcessView();
                    showSnackBar(getString(R.string.started_creating_movie));
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPreviewVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPreviewVideoView.resume();
    }

    private void showSnackBar(String message) {
        Snackbar.make(mRootLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
