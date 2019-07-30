/*
 * ************************************************************
 * 文件：AppUtil.java  模块：main-app  项目：ThirdApp
 * 当前修改时间：2019年07月30日 18:05:03
 * 上次修改时间：2019年07月30日 18:04:20
 * 作者：Cody.yi   https://github.com/codyer
 *
 * 描述：main-app
 * Copyright (c) 2019
 * 本代码只用作学习用途，如用于非法途径，本人概不负责
 * ************************************************************
 */

package com.example.mainapp.utils;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by xu.yi. on 2019-07-23.
 * ThirdApp  本功能只用作学习用途，如用于非法途径，本人概不负责
 */
public class AppUtil {
    private static final String TAG = "AppUtil";

    public static String copyAssetsFile(Context context, String fileName) {
        String path = Environment.getExternalStorageDirectory() + "/apk/";
        return getFilePathByUri(context, copyAssetsFile(context, fileName, path));
    }

    /**
     * 复制文件到SD卡
     *
     * @param fileName 复制的文件名
     * @param path     保存的目录路径
     */
    private static Uri copyAssetsFile(Context context, String fileName, String path) {
        try {
            InputStream mInputStream = context.getAssets().open(fileName);
            File dir = new File(path);
            if (!dir.exists()) {
                boolean mkdir = dir.mkdir();
                Log.e(TAG, "mkdir=" + mkdir);
            }
            File file = new File(path + File.separator + "temp.apk");
            if (!file.exists()) {
                boolean createNewFile = file.createNewFile();
                Log.e(TAG, "createNewFile=" + createNewFile);
            }
            Log.e(TAG, "开始拷贝");
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i;
            while ((i = mInputStream.read(temp)) > 0) {
                outputStream.write(temp, 0, i);
            }
            mInputStream.close();
            outputStream.close();
            Uri uri = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //包名.file_provider
                    uri = FileProvider.getUriForFile(context, context.getPackageName() + ".file_provider", file);
                } else {
                    uri = Uri.fromFile(file);
                }
            } catch (ActivityNotFoundException anfe) {
                Log.e(TAG, anfe.getMessage());
            }
            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
            Log.e(TAG, "拷贝完毕：" + uri);
            return uri;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, fileName + "not exists" + "or write err");
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String getFilePathByUri(Context context, Uri uri) {
        if (uri == null) return null;
        String path = null;
        String scheme = uri.getScheme();

        // 以 file:// 开头的
        if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
            return path;
        }
        // 以 content:// 开头的，比如 content://media/extenral/images/media/17766
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (columnIndex > -1) {
                        path = cursor.getString(columnIndex);
                    }
                }
                cursor.close();
            }
            return path;
        }
        // 4.4及之后的 是以 content:// 开头的，比如 content://com.android.providers.media.documents/document/image%3A235700
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme()) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    // ExternalStorageProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                        return path;
                    }
                } else if (isDownloadsDocument(uri)) {
                    // DownloadsProvider
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                            Long.valueOf(id));
                    path = getDataColumn(context, contentUri, null, null);
                    return path;
                } else if (isMediaDocument(uri)) {
                    // MediaProvider
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};
                    path = getDataColumn(context, contentUri, selection, selectionArgs);
                    return path;
                }
            } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                //微信文件打开的uri
                path = uri.getPath();
                if (path == null)return null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && path.startsWith("/external_storage_root")) {
                    return new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + path.replace("/external_storage_root", "")).getPath();
                } else {
                    String[] paths = path.split("/0/");
                    if (paths.length == 2) {
                        return Environment.getExternalStorageDirectory() + "/" + paths[1];
                    }
                }
            } else {
                path = uri.getPath();
                if (path == null)return null;
                String[] paths = path.split("/0/");
                if (paths.length == 2) {
                    return Environment.getExternalStorageDirectory() + "/" + paths[1];
                }

            }
        }
        return null;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
