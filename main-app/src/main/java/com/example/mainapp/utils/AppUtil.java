package com.example.mainapp.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
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
    public static Uri copyAssetsFile(Context context,String fileName) {
        String path = Environment.getExternalStorageDirectory() + "/apk/";
        return copyAssetsFile(context, fileName, path);
    }

    /**
     * 复制文件到SD卡
     *
     * @param fileName 复制的文件名
     * @param path     保存的目录路径
     */
    public static Uri copyAssetsFile(Context context, String fileName, String path) {
        try {
            InputStream mInputStream = context.getAssets().open(fileName);
            File file = new File(path);
            if (!file.exists()) {
                boolean mkdir = file.mkdir();
                Log.e(TAG, "mkdir=" + mkdir);
            }
            File mFile = new File(path + File.separator + "temp.apk");
            if (!mFile.exists()) {
                boolean createNewFile = mFile.createNewFile();
                Log.e(TAG, "createNewFile=" + createNewFile);
            }
            Log.e(TAG, "开始拷贝");
            FileOutputStream mFileOutputStream = new FileOutputStream(mFile);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = mInputStream.read(temp)) > 0) {
                mFileOutputStream.write(temp, 0, i);
            }
            mInputStream.close();
            mFileOutputStream.close();
            Uri uri = null;
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    //包名.fileprovider
                    uri = FileProvider.getUriForFile(context, context.getPackageName() + ".file_provider", mFile);
                } else {
                    uri = Uri.fromFile(mFile);
                }
            } catch (ActivityNotFoundException anfe) {
                Log.e(TAG, anfe.getMessage());
            }
            MediaScannerConnection.scanFile(context, new String[]{mFile.getAbsolutePath()}, null, null);
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
}
