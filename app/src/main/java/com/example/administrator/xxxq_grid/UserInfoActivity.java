package com.example.administrator.xxxq_grid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.administrator.common.AppManager;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.HeaderBar;
import com.example.administrator.common.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends BaseActivity {

    private Button logout;
    private TextView name, appellation, position, name1, sex, phone;
    private RelativeLayout relativeLayout;
    private ImageView avatar;
    private Map<String, Object> dataMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        initTitle();
        init();
        initData();
    }

    @Override
    protected void initTitle() {
        BaseUtils.initHeaderBar(this);
        HeaderBar headerBar = (HeaderBar) this.findViewById(R.id.headerBar);
        headerBar.initViewsVisible(true, true, false, false);
        headerBar.setAppTitle("管理员中心");
        headerBar.setOnLeftButtonClickListener(new HeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void init() {
        logout = (Button) findViewById(R.id.activity_user_info_logoutbtn);
        logout.setOnClickListener(l);
        relativeLayout = (RelativeLayout) findViewById(R.id.activity_user_info_relativeLayout);
        relativeLayout.setOnClickListener(l);
        name = (TextView) findViewById(R.id.activity_user_info_name);
        appellation = (TextView) findViewById(R.id.activity_user_info_appellation);
        position = (TextView) findViewById(R.id.activity_user_info_position);
        name1 = (TextView) findViewById(R.id.activity_user_info_textView1);
        sex = (TextView) findViewById(R.id.activity_user_info_textView2);
        phone = (TextView) findViewById(R.id.activity_user_info_textView3);
        avatar = (ImageView) findViewById(R.id.activity_user_info_avatar);
    }

    @Override
    protected void initData() {
        Map<String, Object> map = new HashMap<String, Object>();
        new HttpRequest().post(UserInfoActivity.this, "mainActivity/loadMemberInfo", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                dataMap = (Map<String, Object>) data;
                handler.sendEmptyMessage(1);
            }
        });
    }

    /**
     * 消息处理,通知主线程更新ui
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    setData();
                    break;
            }
        }
    };

    private void setData() {
        name.setText(BaseUtils.toString(dataMap.get("name")));
        position.setText(BaseUtils.toString(dataMap.get("gridName")));
        name1.setText(BaseUtils.toString(dataMap.get("name")));
        sex.setText(Integer.valueOf(BaseUtils.toString(dataMap.get("sex"))) == 1 ? "男" : "女");
        phone.setText(BaseUtils.toString(dataMap.get("phone")));
//        Glide.with(this).load(new HttpRequest().getFileUrl() + BaseUtils.toString(dataMap.get("phone"))).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(avatar);
    }

    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.activity_user_info_logoutbtn:
                    BaseUtils.deleteSharedPreferences(UserInfoActivity.this);
                    Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
                    startActivity(intent);
                    AppManager appManager = AppManager.getAppManager();
                    appManager.finishAllActivity();
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    break;
                case R.id.activity_user_info_relativeLayout:
                    Intent intent1 = new Intent(UserInfoActivity.this, GridManagerActivity.class);
                    startActivity(intent1);
                    break;
            }
        }
    };

}
