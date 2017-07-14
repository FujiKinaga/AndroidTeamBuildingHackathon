package team_ky.androidteambuildinghackathon;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by okadaakihito on 2017/07/14.
 */

public class MovieInfo implements Parcelable {

    private String mAudioUrl;
    private String mMovieUrl;
    private String mImageUrl;
    private String mMoviePath;

    public MovieInfo() {
        mAudioUrl = null;
        mImageUrl = null;
        mMovieUrl = null;
        mMoviePath = null;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getImageUrl() {

        return mImageUrl;
    }

    public void setAudioUrl(String audioUrl) {

        mAudioUrl = audioUrl;
    }

    public String getMoviePath() {
        return mMoviePath;
    }

    public String getAudioUrl() {
        return mAudioUrl;
    }

    public String getMovieUrl() {
        return mMovieUrl;
    }

    public void setMovieUrl(String dir) {
        mMoviePath = String.format("%s/nana", dir);
        mMovieUrl = String.format("%s/%s.mp4", mMoviePath, getDateTimeString());
    }

    private static String getDateTimeString() {
        final GregorianCalendar now = new GregorianCalendar(TimeZone.getTimeZone("UTC"), Locale.US);
        final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
        return dateTimeFormat.format(now.getTime());
    }

    public boolean isAvailable() {
        return mAudioUrl != null && mImageUrl != null && mMovieUrl != null;
    }

    public boolean isAvailableAudio() {
        return mAudioUrl != null;
    }

    public boolean isAvailableImage() {
        return mImageUrl != null;
    }

    public boolean isAvailableMovie() {
        return mMovieUrl != null;
    }

    @Override
    public int describeContents() {
        return 4;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(mAudioUrl);
        parcel.writeString(mImageUrl);
        parcel.writeString(mMovieUrl);
        parcel.writeString(mMoviePath);
    }


    protected MovieInfo(Parcel in) {
        mAudioUrl = in.readString();
        mImageUrl = in.readString();
        mMovieUrl = in.readString();
        mMoviePath = in.readString();
    }

    public static final Creator<MovieInfo> CREATOR = new Creator<MovieInfo>() {
        @Override
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        @Override
        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };
}
