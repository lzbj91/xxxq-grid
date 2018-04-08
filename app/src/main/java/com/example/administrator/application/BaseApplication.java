package com.example.administrator.application;

import android.app.Application;

import com.xiasuhuei321.loadingdialog.manager.StyleManager;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import cn.jpush.android.api.JPushInterface;

/**
 * 极光推送   一般建议在自定义 Application 类里初始化。也可以在主 Activity 里。
 */
public class BaseApplication extends Application {
    private static final String TAG = "GRID_PROJECT";

    @Override
    public void onCreate() {
        //初始化方法写这
        super.onCreate();
        //极光推送初始化
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush

        StyleManager s = new StyleManager();

        //在这里调用方法设置s的属性
        //code here...
        s.Anim(false).repeatTime(0).contentSize(-1).intercept(true);

        LoadingDialog.initStyle(s);

    }
}
