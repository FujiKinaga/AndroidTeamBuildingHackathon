package team_ky.androidteambuildinghackathon;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import cafe.adriel.androidaudioconverter.callback.IConvertCallback;
import cafe.adriel.androidaudioconverter.model.AudioFormat;

public class ShareToInstagramActivity extends AppCompatActivity {

    private static final String ARG_AUDIO_FILE_PATH = "ARG_AUDIO_FILE_PATH";
    private static final String ARG_IMAGE_FILE_PATH = "ARG_IMAGE_FILE_PATH";

    @BindView(R.id.preview_video_view)
    SquareVideoView mPreviewVideoView;

    @OnClick(R.id.button_upload)
    public void onClick() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("video/*");
        File media = new File(mOutputPath);
        Uri uri = Uri.fromFile(media);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setPackage("com.instagram.android");
        startActivity(Intent.createChooser(share, "Share to"));
    }

    private String mPostName;
    private String mAudioFilePath;
    private String mImageFilePath;
    private String mOutputPath;

    public static Intent createIntent(Context context, MovieInfo movieInfo) {
        Intent intent = new Intent(context, ShareToInstagramActivity.class);
        intent.putExtra(ARG_AUDIO_FILE_PATH, movieInfo.getAudioUrl());
        intent.putExtra(ARG_IMAGE_FILE_PATH, movieInfo.getImageUrl());
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to_instagram);
        ButterKnife.bind(this);

        mAudioFilePath = getIntent().getStringExtra(ARG_AUDIO_FILE_PATH);
        mImageFilePath = getIntent().getStringExtra(ARG_IMAGE_FILE_PATH);
        mPostName = getDateTimeString();
        mOutputPath = getCaptureFile(this, mPostName, ".mp4").toString();

        File file = new File(mAudioFilePath);
        IConvertCallback callback = new IConvertCallback() {
            @Override
            public void onSuccess(File convertedFile) {
                try {
                    String[] cmd = new String[]{"-y", "-i", mImageFilePath, "-i", convertedFile.getPath(), mOutputPath};
                    FFmpeg ffmpeg = FFmpeg.getInstance(ShareToInstagramActivity.this);
                    ffmpeg.execute(cmd, new FFmpegExecuteResponseHandler() {
                        @Override
                        public void onSuccess(String message) {
                            mPreviewVideoView.setVideoPath(mOutputPath);
                            mPreviewVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    mp.start();
                                }
                            });
                        }

                        @Override
                        public void onProgress(String message) {

                        }

                        @Override
                        public void onFailure(String message) {

                        }

                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onFinish() {

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

    private static String getDateTimeString() {
        final GregorianCalendar now = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.US);
        final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
        return dateTimeFormat.format(now.getTime());
    }

    private static File getCaptureFile(final Context context, String postName, final String ext) {
        final File dir = new File(context.getCacheDir().getAbsolutePath(), "nana");
        dir.mkdirs();
        if (dir.canWrite()) {
            return new File(dir, postName + ext);
        }
        return null;
    }
}
