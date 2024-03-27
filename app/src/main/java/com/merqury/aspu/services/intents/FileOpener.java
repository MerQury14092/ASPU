package com.merqury.aspu.services.intents;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

public class FileOpener {


    private FileOpener() {
    }

    private static Uri uri;
    private static Intent intent;

    public static void open(Context context, String url) {
        setupBase(url);
        checkFileTypeByUrl(url);

        try {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void checkFileTypeByUrl(String url) {

    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
    intent.setDataAndType(uri, mimeType);
    }

    private static void setupBase(String url) {
        uri = Uri.parse(url);
        intent = new Intent(Intent.ACTION_VIEW, uri);
    }
}