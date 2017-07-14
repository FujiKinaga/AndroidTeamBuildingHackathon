package team_ky.androidteambuildinghackathon;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShareToInstagramActivity extends AppCompatActivity {

    private static final String TAG = ShareToInstagramActivity.class.getSimpleName();
    private static final String ARG_MOVIE_INFO = "ARG_MOVIE_INFO";

    @BindView(R.id.preview_video_view)
    SquareVideoView mPreviewVideoView;

    @OnClick(R.id.button_upload)
    public void onClick() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("video/*");
        File media = new File(mMovieInfo.getMovieUrl());
        Uri uri = Uri.fromFile(media);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setPackage("com.instagram.android");
        startActivity(Intent.createChooser(share, "Share to"));
    }

    private MovieInfo mMovieInfo;

    public static Intent createIntent(Context context, MovieInfo movieInfo) {
        Intent intent = new Intent(context, ShareToInstagramActivity.class);
        intent.putExtra(ARG_MOVIE_INFO, movieInfo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to_instagram);
        ButterKnife.bind(this);

        mMovieInfo = (MovieInfo) getIntent().getParcelableExtra(ARG_MOVIE_INFO);

        try {
            String[] cmd = new String[]{"-y", "-s", "640x640", "-i", mMovieInfo.getImageUrl(), "-i", mMovieInfo.getAudioUrl(), mMovieInfo.getMovieUrl()};
            FFmpeg ffmpeg = FFmpeg.getInstance(ShareToInstagramActivity.this);
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
                }

                @Override
                public void onFinish() {
                    Log.e(TAG, "onFinish : ");
                }

                @Override
                public void onProgress(String message) {
                    Log.e(TAG, "onProgress : " +  message);
                }

                @Override
                public void onFailure(String message) {
                    Log.e(TAG, "onFailure : " + message);
                }

                @Override
                public void onStart() {
                    Log.e(TAG, "onStart : ");
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
}
