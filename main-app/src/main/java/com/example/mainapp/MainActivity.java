package com.example.mainapp;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.mainapp.utils.AppUtil;
import com.example.mainapp.utils.PackageUtil;
import com.example.mainapp.utils.ShellUtil;
import com.example.mainapp.utils.ZipUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String commandCopy = "cp -rf /mnt/sdcard/output/* /data/data/com.example.thirdapp/";
    private final static String commandChmod = "chmod -R 777 /data/data/com.example.thirdapp/";
    //设置解压目的路径
    private final static String OUTPUT_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/output";
    private Uri mUri;
    private ProgressDialog mProgressDialog;
    private TextView mStep;
//    String[] commands = new String[]{"mount -o rw,remount /system", commandCopy};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setTitle("操作提示");
        mStep = findViewById(R.id.step);
        mStep.setText(String.format(getString(R.string.step), 1));
        Button copyApp = findViewById(R.id.copyApp);
        Button install = findViewById(R.id.installApp);
        Button unZipData = findViewById(R.id.unZipData);
        Button copyData = findViewById(R.id.copyData);
        Button changeRight = findViewById(R.id.changeRight);
        copyApp.setOnClickListener(this);
        install.setOnClickListener(this);
        unZipData.setOnClickListener(this);
        copyData.setOnClickListener(this);
        changeRight.setOnClickListener(this);
        if (!ShellUtil.checkRootPermission()) {
            showToast("没有ROOT权限无法执行下面操作\n\n请打开应用的Root权限", -1);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Copyright (c) 2019 -- Cody.yi", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.copyApp:
                mProgressDialog.setMessage("正在复制文件，请稍后！");
                mProgressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mUri = AppUtil.copyAssetsFile(MainActivity.this, "third-app-debug.apk");
                        if (mUri != null) {
                            showToast("复制软件成功！", 2);
                        } else {
                            showToast("复制软件失败！", 1);
                        }
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
                        if (mUri != null) {
                            int a = PackageUtil.install(MainActivity.this, mUri.getPath());
                            if (a > 0) {
                                showToast("安装软件成功！", 3);
                            } else {
                                showToast("安装软件失败！", 2);
                            }
                        }
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
                        try {
                            ZipUtil.unZip(MainActivity.this, "third-app-data.zip", OUTPUT_DIRECTORY, true);
                            showToast("解压数据完成！", 4);
                        } catch (IOException e) {
                            e.printStackTrace();
                            showToast("解压数据失败！", 3);
                        }
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
                        ShellUtil.CommandResult result = ShellUtil.execCommand(commandCopy, true);
                        if (result.errorMsg.equals("Permission denied") || result.result != 0) {
                            showToast("恢复数据失败！", 4);
                        } else {
                            showToast("恢复数据完成！", 5);
                        }
                        Log.d("MainActivity", "successMsg = " + result.successMsg + "\nresult =" + result.result + "\nerrorMsg =" + result.errorMsg);
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
                        ShellUtil.CommandResult result = ShellUtil.execCommand(commandChmod, true);
                        if (result.errorMsg.equals("Permission denied") || result.result != 0) {
                            showToast("改变权限失败", 5);
                        } else {
                            showToast("改变权限完成", 0);
                        }
                        Log.d("MainActivity", "successMsg = " + result.successMsg + "\nresult =" + result.result + "\nerrorMsg =" + result.errorMsg);
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
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "Copyright (c) 2019 -- Cody.yi", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(final String msg, final int step) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
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
