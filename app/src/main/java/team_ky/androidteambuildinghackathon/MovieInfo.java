package team_ky.androidteambuildinghackathon;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.IOException;
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

    public MovieInfo() {
        mAudioUrl = null;
        mImageUrl = null;
        mMovieUrl = null;
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
        return mMovieUrl;
    }

    public void setMovieUrl(String dir) {
        mMovieUrl = String.format("%s/%s.mp4", dir + "/nana", getDateTimeString());
        final File file = new File(mMovieUrl);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        return 3;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(mAudioUrl);
        parcel.writeString(mImageUrl);
        parcel.writeString(mMovieUrl);
    }


    protected MovieInfo(Parcel in) {
        mAudioUrl = in.readString();
        mImageUrl = in.readString();
        mMovieUrl = in.readString();
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
