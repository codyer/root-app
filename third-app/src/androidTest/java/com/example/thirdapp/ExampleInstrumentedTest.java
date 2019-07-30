/*
 * ************************************************************
 * 文件：ExampleInstrumentedTest.java  模块：third-app  项目：ThirdApp
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

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.thirdapp", appContext.getPackageName());
    }
}
