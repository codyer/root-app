/*
 * ************************************************************
 * 文件：MainActivity.java  模块：third-app  项目：ThirdApp
 * 当前修改时间：2019年07月30日 18:05:03
 * 上次修改时间：2019年07月30日 18:04:20
 * 作者：Cody.yi   https://github.com/codyer
 *
 * 描述：third-app
 * Copyright (c) 2019
 * 本代码只用作学习用途，如用于非法途径，本人概不负责
 * ************************************************************
 */

package com.example.thirdapp;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private LocalProfile mLocalProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocalProfile = new LocalProfile(getApplicationContext());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final TextView testView = findViewById(R.id.testView);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StringBuilder test = new StringBuilder();
                for (int i = 0; i < 10; i++) {
                    test.append(mLocalProfile.getValue("test" + i, "default value")).append("\n");
                }
                testView.setText(test);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            for (int i = 0; i < 10; i++) {
                mLocalProfile.setValue("test" + i, "third-value" + i);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
