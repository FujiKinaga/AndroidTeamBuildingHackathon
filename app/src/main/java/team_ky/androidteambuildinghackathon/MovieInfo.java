package team_ky.androidteambuildinghackathon;

/**
 * Created by okadaakihito on 2017/07/14.
 */

public class MovieInfo {

    private String mAudioUrl;
    private String mImageUrl;
    private String mMoviewUrl;

    public MovieInfo() {
        mAudioUrl = null;
        mImageUrl = null;
        mMoviewUrl = null;
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


    public String getMovieUrl() {
        return mMoviewUrl;
    }

    public void setMovieUrl(String path) {
        this.mMoviewUrl = String.format("%s/mov.mp4", path);
    }
}
