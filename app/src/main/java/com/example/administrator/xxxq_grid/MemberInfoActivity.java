package com.example.administrator.xxxq_grid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.HeaderBar;
import com.example.administrator.common.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class MemberInfoActivity extends BaseActivity {

    private String id;
    private TextView name, type, phone, memberCard;
    private ImageView avatar;
    private Map<String, Object> dataMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");

        initTitle();
        init();
        initData();
    }

    @Override
    protected void init() {
        name = (TextView) findViewById(R.id.activity_member_info_name);
        type = (TextView) findViewById(R.id.activity_member_info_type);
        phone = (TextView) findViewById(R.id.activity_member_info_phone);
        memberCard = (TextView) findViewById(R.id.activity_member_info_member_card);
        avatar = (ImageView) findViewById(R.id.activity_member_info_avatar);
    }

    @Override
    protected void initData() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        new HttpRequest().post(MemberInfoActivity.this, "member/loadMemberInfo", map, new CallBackSuccess() {
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
                    setMemberInfo();
                    break;
            }
        }
    };

    private void setMemberInfo() {
        name.setText(BaseUtils.toString(dataMap.get("name")));
        phone.setText(BaseUtils.toString(dataMap.get("phone")));
        type.setText(BaseUtils.toString(dataMap.get("typeAs")));
        memberCard.setText(BaseUtils.toString(dataMap.get("memberCard")));
        Glide.with(this).load(new HttpRequest().getFileUrl() + BaseUtils.toString(dataMap.get("photo"))).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(avatar);
    }

    @Override
    protected void initTitle() {
        BaseUtils.initHeaderBar(this);
        HeaderBar headerBar = (HeaderBar) this.findViewById(R.id.headerBar);
        headerBar.initViewsVisible(true, true, false, false);
        headerBar.setAppTitle("会员信息");
        headerBar.setOnLeftButtonClickListener(new HeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }
}
