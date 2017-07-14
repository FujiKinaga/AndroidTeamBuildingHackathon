package team_ky.androidteambuildinghackathon;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShareToInstagramActivity extends AppCompatActivity {


    @BindView(R.id.preview_video_view) VideoView mPreviewVideoView;
    @BindView(R.id.button_upload) Button mUploadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_to_instagram);
        ButterKnife.bind(this);

        String SOUND_URL = "android.resource://"+  getPackageName() + "/raw/sample";
        mPreviewVideoView.setVideoPath(SOUND_URL);
        mPreviewVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                mp.setLooping(true);
            }
        });
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
