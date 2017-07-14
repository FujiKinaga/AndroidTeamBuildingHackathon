package team_ky.androidteambuildinghackathon;

/**
 * Created by okadaakihito on 2017/07/14.
 */

public class MovieInfo {

    private String mAudioUrl;
    private String mImageUrl;

    public MovieInfo() {
        mAudioUrl = null;
        mImageUrl = null;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getImageUrl() {

        return mImageUrl;
    }

    public void setAudioUrl(String mAudioUrl) {

        this.mAudioUrl = mAudioUrl;
    }

    public String getAudioUrl() {
        return mAudioUrl;
    }
}
