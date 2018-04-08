package com.example.administrator.xxxq_grid;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.HttpRequest;
import com.example.administrator.common.PermissionUtils;
import com.insplatform.core.utils.TextUtil;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextView login_title_desction;// 标题字体
    private Button login_please_btn;//登陆按钮
    private EditText login_please_username;//请输入用户名
    private EditText login_please_password;//请输入密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        BaseUtils.setTransparentBar(this);
        PermissionUtils.checkPermission(this, PermissionUtils.PHONE_STATE, PermissionUtils.GPS, PermissionUtils.CAMERA, PermissionUtils.SD, PermissionUtils.SD1);
        loadingDialog = new LoadingDialog(LoginActivity.this);
        init();

        // 设置字体样式
        Typeface mtypeface = Typeface.createFromAsset(getAssets(), "fangzhengjianti.TTF");
        login_title_desction.setTypeface(mtypeface);
        login_please_btn.setOnClickListener(this);
    }

    /**
     * 初始化控件
     */
    public void init() {
        login_title_desction = findViewById(R.id.login_title_desction);
        login_please_btn = findViewById(R.id.login_please_btn);
        login_please_username = findViewById(R.id.login_please_username_edit);
        login_please_password = findViewById(R.id.login_please_password_edit);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitle() {

    }

    /**
     * 登陆按钮实现
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_please_btn:
                if (TextUtil.isEmpty(BaseUtils.toString(login_please_username.getText()))) {
                    showToast("用户名不能为空");
                    return;
                }
                if (TextUtil.isEmpty(BaseUtils.toString(login_please_password.getText()))) {
                    showToast("密码不能为空");
                    return;
                }
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("phone", login_please_username.getText());
                map.put("password", login_please_password.getText());

                //登录之前删除所有的本地存储
                BaseUtils.deleteSharedPreferences(this);
                new HttpRequest().post(LoginActivity.this, "login", map, new CallBackSuccess() {
                    @Override
                    public void onCallBackSuccess(Object data) {
                        //存储返回的用户信息
                        BaseUtils.putSharedPreferences(LoginActivity.this, "fu", BaseUtils.toString(data));
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                showToast("登录成功");
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                });
                break;
            default:
                break;
        }
    }

    //监听物理返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BaseUtils.exitApp(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
