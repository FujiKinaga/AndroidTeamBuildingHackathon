package team_ky.androidteambuildinghackathon;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by fujikinaga on 2017/07/15.
 */

public class ShareToInstagram {

    public static void performShare(final Context context, final File file) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("video/*");
        Uri uri = Uri.fromFile(file);
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setPackage("com.instagram.android");
        context.startActivity(Intent.createChooser(share, "Share to"));
    }
}
