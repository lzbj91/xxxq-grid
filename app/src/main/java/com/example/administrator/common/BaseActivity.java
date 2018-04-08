package com.example.administrator.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

public abstract class BaseActivity extends Activity {
    protected LoadingDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //添加Activity到堆栈
        AppManager.getAppManager().addActivity(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        //结束Activity&从堆栈中移除
        AppManager.getAppManager().finishActivity(this);
        super.onDestroy();
    }

    protected void showLoading() {
        loadingDialog = new LoadingDialog(BaseActivity.this);
        loadingDialog.show();
    }

    protected void closeLoading() {
        loadingDialog.close();
    }

    protected void successfulLoading() {
        loadingDialog.loadSuccess();
    }

    protected void failedLoading() {
        loadingDialog.loadFailed();
    }

    protected void showToast(Object msg) {
        Toast.makeText(BaseActivity.this, BaseUtils.toString(msg), Toast.LENGTH_SHORT).show();
    }

    protected void log(int type, Object msg) {
        String splice = "";
        switch (type) {
            case 1:
                splice = "------------";
                break;
            case 2:
                splice = "************";
                break;
            case 3:
                splice = "+++++++++++++";
                break;
            case 4:
                splice = "=============";
                break;
        }
        Log.w(splice, BaseUtils.toString(msg));
    }

    protected abstract void init();

    protected abstract void initData();

    protected abstract void initTitle();


}
