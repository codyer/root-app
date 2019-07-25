package com.example.mainapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mainapp.utils.AppUtil;
import com.example.mainapp.utils.PackageUtil;
import com.example.mainapp.utils.ShellUtil;
import com.example.mainapp.utils.ZipUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

/**
 * 本功能只用作学习用途，如用于非法途径，本人概不负责
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String THIRD_DEBUG_APK = "third-app-debug.apk";
    private final static String THIRD_RELEASE_APK = "third-app-release.apk";
    private final static String ZIP_DATA = "third-app-data.zip";
    private final static String COMMAND_COPY = "cp -rf /data/data/com.example.mainapp/cache/data/* /data/data/com.example.thirdapp/";
    private final static String COMMAND_CHMOD = "chmod -R 777 /data/data/com.example.thirdapp/";
    //设置解压目的路径
    private String mDataDirectory;
    private Uri mUri;
    private ProgressDialog mProgressDialog;
    private TextView mStep;
//    String[] commands = new String[]{"mount -o rw,remount /system", COMMAND_COPY};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataDirectory = getCacheDir().getAbsolutePath() + "/data";
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setTitle("操作提示");
        mStep = findViewById(R.id.step);
        mStep.setText(String.format(getString(R.string.step), 1));
        Button copyDebugApp = findViewById(R.id.copyDebugApp);
        Button copyReleaseApp = findViewById(R.id.copyReleaseApp);
        Button install = findViewById(R.id.installApp);
        Button unZipData = findViewById(R.id.unZipData);
        Button copyData = findViewById(R.id.copyData);
        Button changeRight = findViewById(R.id.changeRight);
        copyDebugApp.setOnClickListener(this);
        copyReleaseApp.setOnClickListener(this);
        install.setOnClickListener(this);
        unZipData.setOnClickListener(this);
        copyData.setOnClickListener(this);
        changeRight.setOnClickListener(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.copy_right, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        checkPermission();
    }

    private boolean checkPermission() {
        //判断用户是否已经授权，未授权则向用户申请授权，已授权则直接进行呼叫操作
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //注意第二个参数没有双引号
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            showToast("没有文件读写权限无法使用！", -1);
            return false;
        } else if (!ShellUtil.checkRootPermission()) {
            showToast(getString(R.string.no_root_right), -1);
            return false;
        }
        return true;
    }

    @Override
    public void onClick(final View v) {
        if (!checkPermission()) {
            return;
        }
        switch (v.getId()) {
            case R.id.copyDebugApp:
                mProgressDialog.setMessage("正在复制文件，请稍后！");
                mProgressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        copyApk(THIRD_DEBUG_APK);
                        mProgressDialog.dismiss();
                    }
                }).start();
                break;
            case R.id.copyReleaseApp:
                mProgressDialog.setMessage("正在复制文件，请稍后！");
                mProgressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        copyApk(THIRD_RELEASE_APK);
                        mProgressDialog.dismiss();
                    }
                }).start();
                break;
            case R.id.installApp:
                mProgressDialog.setMessage("正在安装软件，请稍后！");
                mProgressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        installApk();
                        mProgressDialog.dismiss();
                    }
                }).start();
                break;

            case R.id.unZipData:
                mProgressDialog.setMessage("正在解压数据，请稍后！");
                mProgressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        unZipData();
                        mProgressDialog.dismiss();
                    }
                }).start();
                break;
            case R.id.copyData:
                mProgressDialog.setMessage("正在恢复数据，请稍后！");
                mProgressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        execCommand(COMMAND_COPY, "恢复数据失败！", 4, "恢复数据完成！", 5);
                        mProgressDialog.dismiss();
                    }
                }).start();
                break;
            case R.id.changeRight:
                mProgressDialog.setMessage("正在改变权限，请稍后！");
                mProgressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        execCommand(COMMAND_CHMOD, "改变权限失败", 5, "改变权限完成", 0);
                        mProgressDialog.dismiss();
                    }
                }).start();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings && checkPermission()) {
            mProgressDialog.setMessage("正在执行初始化操作，请稍后！");
            mProgressDialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (copyApk(THIRD_RELEASE_APK) &&
                            installApk() &&
                            unZipData() &&
                            execCommand(COMMAND_COPY, "恢复数据失败！", 4, "恢复数据完成！", 5) &&
                            execCommand(COMMAND_CHMOD, "改变权限失败", 5, "改变权限完成", 0)) {
                        showToast("初始化操作完成", 0);
                        mProgressDialog.dismiss();
                    }
                }
            }).start();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity", "获取权限");
            showToast("没有文件读写权限无法使用！", -1);
        }
    }

    private boolean execCommand(final String command, final String failure, final int stepHold, final String success, final int stepNext) {
        boolean result = false;
        ShellUtil.CommandResult commandResult = ShellUtil.execCommand(command, true);
        if (commandResult.errorMsg.equals("Permission denied") || commandResult.result != 0) {
            showToast(failure, stepHold);
        } else {
            showToast(success, stepNext);
            result = true;
        }
        Log.d("MainActivity", "successMsg = " + commandResult.successMsg + "\nresult =" + commandResult.result + "\nerrorMsg =" + commandResult.errorMsg);
        return result;
    }

    private boolean unZipData() {
        boolean result = false;
        try {
            ZipUtil.unZip(MainActivity.this, ZIP_DATA, mDataDirectory, true);
            showToast("解压数据完成！", 4);
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            showToast("解压数据失败！", 3);
        }
        return result;
    }

    private boolean installApk() {
        boolean result = false;
        if (mUri != null) {
            int a = PackageUtil.install(MainActivity.this, mUri.getPath());
            if (a > 0) {
                showToast("安装软件成功！", 3);
                result = true;
            } else {
                showToast("安装软件失败！", 2);
            }
        }
        return result;
    }

    private boolean copyApk(final String thirdDebugApk) {
        boolean result = false;
        mUri = AppUtil.copyAssetsFile(MainActivity.this, thirdDebugApk);
        if (mUri != null) {
            showToast("复制软件成功！", 2);
            result = true;
        } else {
            showToast("复制软件失败！", 1);
        }
        return result;
    }

    private void showToast(final String msg, final int step) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                if (step == 0) {
                    mStep.setText("所以操作已经完成，可以使用第三方应用了！");
                } else if (step == -1) {
                    mStep.setText(msg);
                } else {
                    mStep.setText(String.format(getString(R.string.step), step));
                }
            }
        });
    }
}
