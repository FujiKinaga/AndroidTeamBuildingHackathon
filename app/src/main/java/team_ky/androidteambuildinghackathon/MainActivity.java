package team_ky.androidteambuildinghackathon;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @OnClick(R.id.download_sound_button)
    void onClickDownloadSound(View view) {

    }

    @OnClick(R.id.ok_button)
    void onClickOk(View view) {

    }

    @OnClick(R.id.share_instagram)
    void onShareInstagram(View view) {
        Intent intent = new Intent(this, ShareToInstagramActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.lbl_download_sound);
    }
}
