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
import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

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

        File file = new File(mMovieInfo.getAudioUrl());
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                try {
                    String[] cmd = new String[]{"-y", "-i", mMovieInfo.getImageUrl(), "-i", convertedFile.getPath(), mMovieInfo.getMovieUrl()};
                    FFmpeg ffmpeg = FFmpeg.getInstance(ShareToInstagramActivity.this);
                    ffmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
                        @Override
                        public void onSuccess(String message) {
                            mPreviewVideoView.setVideoPath(mMovieInfo.getMovieUrl());
                            mPreviewVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                }
                            });
                            Log.e(TAG, "onSuccess : " + message);
                        }

                        @Override
                        public void onProgress(String message) {
                            Log.e(TAG, "onProgress : " + message);
                        }

                        @Override
                        public void onFailure(String message) {
                            Log.e(TAG, "onFailure : " + message);
                        }

                        @Override
                        public void onStart() {
                            Log.e(TAG, "onStart : ");
                        }

                        @Override
                        public void onFinish() {
                            Log.e(TAG, "onFinish : ");
                        }
                    });
                } catch (FFmpegCommandAlreadyRunningException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception error) {
                // Oops! Something went wrong
            }
        };
        AndroidAudioConverter.with(this)
                // Your current audio file
                .setFile(file)

                // Your desired audio format
                .setFormat(AudioFormat.MP3)

                // An callback to know when conversion is finished
                .setCallback(callback)

                // Start conversion
                .convert();
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
