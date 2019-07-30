/*
 * ************************************************************
 * 文件：LocalProfile.java  模块：third-app  项目：ThirdApp
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cody.yi on 2016/9/13.
 * 存放全局应用级需要本地化的变量LocalProfile
 */
public class LocalProfile {

    private final static String FILENAME = "XFProfile";
    private final SharedPreferences mSharedPreferences;

    /**
     * 构造函数，进行初始化
     */
    public LocalProfile(Context context) {
        mSharedPreferences = context.getSharedPreferences(FILENAME, Context.MODE_PRIVATE);
    }

    /**
     * 保存本地化键值对
     *
     * @param key   键
     * @param value 值
     */
    public final void setValue(String key, String value) {
        mSharedPreferences.edit().putString(key, value).apply();
    }

    /**
     * 获取本地化键值对
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public final String getValue(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    /**
     * 保存本地化键值对
     *
     * @param key   键
     * @param value 值
     */
    public final void setValue(String key, boolean value) {
        mSharedPreferences.edit().putBoolean(key, value).apply();
    }

    /**
     * 获取本地化键值对
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public final boolean getValue(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * 保存本地化键值对
     *
     * @param key   键
     * @param value 值
     */
    public final void setValue(String key, int value) {
        mSharedPreferences.edit().putInt(key, value).apply();
    }

    /**
     * 获取本地化键值对
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public final int getValue(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    /**
     * 保存本地化键值对
     *
     * @param key   键
     * @param value 值
     */
    public final void setValue(String key, float value) {
        mSharedPreferences.edit().putFloat(key, value).apply();
    }

    /**
     * 获取本地化键值对
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 值
     */
    public final float getValue(String key, float defaultValue) {
        return mSharedPreferences.getFloat(key, defaultValue);
    }

    /**
     * 移除值
     *
     * @param key 键
     */
    public final void remove(String key) {
        mSharedPreferences.edit().remove(key).apply();
    }

    public final void registerCallback(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        if (listener == null) return;
        mSharedPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @SuppressLint("ApplySharedPref")
    public void clear() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
