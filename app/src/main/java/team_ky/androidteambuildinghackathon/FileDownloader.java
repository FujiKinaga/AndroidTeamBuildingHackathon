package team_ky.androidteambuildinghackathon;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileDownloader {

    public static void downloadFile(final String url, final String cacheDirPath, final FileDownloadListener listener) {
        DownloadFile downloadProcess = new DownloadFile(listener);
        downloadProcess.execute(url, cacheDirPath);
    }

    public interface FileDownloadListener {

        void downloadStarted();

        void downloadFinished(String filePath);

        void downloadCancled();

        void downloadFailed();
    }

    private static class DownloadFile extends AsyncTask<String, Integer, String> {

        private File mTempFile = null;
        private FileDownloadListener mListener;

        DownloadFile(final FileDownloadListener listener) {
            this.mListener = listener;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.mListener.downloadStarted();
        }

        @Override
        protected String doInBackground(String... params) {
            int count;
            try {
                String paramUrl = params[0];
                String cacheDirPath = params[1];
                File soundFile = new File(cacheDirPath, "download.m4a");
                mTempFile = new File(cacheDirPath, "download.m4a.temp");
                if (mTempFile.exists()) {
                    mTempFile.delete();
                }
                URL url = new URL(paramUrl);
                URLConnection conection = url.openConnection();
                conection.connect();
                InputStream inputStream = new BufferedInputStream(url.openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(mTempFile);
                byte data[] = new byte[4096];
                while ((count = inputStream.read(data)) != -1) {
                    fileOutputStream.write(data, 0, count);
                }
                fileOutputStream.flush();
                fileOutputStream.close();
                inputStream.close();
                mTempFile.renameTo(soundFile);
                mTempFile.delete();
                return soundFile.getAbsolutePath();
            } catch (Exception ex) {
                Log.e(FileDownloader.class.getName(), ex.getMessage(), ex);
            } finally {
                if (mTempFile != null) {
                    mTempFile.delete();
                }
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (mTempFile != null) {
                mTempFile.delete();
            }
            this.mListener.downloadCancled();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (this.mListener != null) {
                if (result == null) {
                    this.mListener.downloadFailed();
                } else {
                    this.mListener.downloadFinished(result);
                }
            }
        }
    }
}
