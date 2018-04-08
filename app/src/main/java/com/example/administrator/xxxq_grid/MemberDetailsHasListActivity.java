package com.example.administrator.xxxq_grid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.HeaderBar;
import com.example.administrator.common.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class MemberDetailsHasListActivity extends BaseActivity {

    private Button money_50, money_100, money_200, money_500, money_1000, money_other, submitBtn;
    private int rechargePrice = 50;
    private ImageView avatar;
    private RelativeLayout index1, index2;
    private TextView name, phone, type, balance;
    private Map<String, Object> dataMap;
    private String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");

        initTitle();
        init();
        initData();

    }

    @Override
    protected void init() {
        money_50 = (Button) findViewById(R.id.activity_member_details_money_50);
        money_100 = (Button) findViewById(R.id.activity_member_details_money_100);
        money_200 = (Button) findViewById(R.id.activity_member_details_money_200);
        money_500 = (Button) findViewById(R.id.activity_member_details_money_500);
        money_1000 = (Button) findViewById(R.id.activity_member_details_money_1000);
        money_other = (Button) findViewById(R.id.activity_member_details_money_other);
        submitBtn = (Button) findViewById(R.id.activity_member_details_money_submit);
        avatar = (ImageView) findViewById(R.id.activity_member_details_avatar);
        index1 = (RelativeLayout) findViewById(R.id.activity_member_details_index1);
        index2 = (RelativeLayout) findViewById(R.id.activity_member_details_index2);

        name = (TextView) findViewById(R.id.activity_member_details_name);
        phone = (TextView) findViewById(R.id.activity_member_details_phone);
        type = (TextView) findViewById(R.id.activity_member_details_type);
        balance = (TextView) findViewById(R.id.activity_member_details_balance);

        money_50.setOnClickListener(l);
        money_100.setOnClickListener(l);
        money_200.setOnClickListener(l);
        money_500.setOnClickListener(l);
        money_1000.setOnClickListener(l);
        money_other.setOnClickListener(l);
        submitBtn.setOnClickListener(l);
        avatar.setOnClickListener(l);
        index1.setOnClickListener(l);
        index2.setOnClickListener(l);
    }

    @Override
    protected void initData() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        new HttpRequest().post(MemberDetailsHasListActivity.this, "member/loadMemberInfo", map, new CallBackSuccess() {
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
        balance.setText(BaseUtils.toString(dataMap.get("enableWallet")));
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

    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.activity_member_details_money_50:
                    moneyBtnClick(money_50, 50);
                    break;
                case R.id.activity_member_details_money_100:
                    moneyBtnClick(money_100, 100);
                    break;
                case R.id.activity_member_details_money_200:
                    moneyBtnClick(money_200, 200);
                    break;
                case R.id.activity_member_details_money_500:
                    moneyBtnClick(money_500, 500);
                    break;
                case R.id.activity_member_details_money_1000:
                    moneyBtnClick(money_1000, 1000);
                    break;
                case R.id.activity_member_details_money_other:
                    moneyOtherClick();
                    break;
                case R.id.activity_member_details_money_submit:
                    submitRecharge();
                    break;
                case R.id.activity_member_details_avatar:
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("id", id);
                    Intent intent = new Intent(MemberDetailsHasListActivity.this, MemberInfoActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                case R.id.activity_member_details_index1:
                    Bundle bundle1 = new Bundle();
                    bundle1.putCharSequence("id", id);
                    Intent intent1 = new Intent(MemberDetailsHasListActivity.this, MemberRecordsConsumptionActivity.class);
                    intent1.putExtras(bundle1);
                    startActivity(intent1);
                    break;
                case R.id.activity_member_details_index2:
                    Bundle bundle2 = new Bundle();
                    bundle2.putCharSequence("id", id);
                    Intent intent2 = new Intent(MemberDetailsHasListActivity.this, MemberConvertibilityRecordActivity.class);
                    intent2.putExtras(bundle2);
                    startActivity(intent2);
                    break;
            }
        }
    };

    /**
     * 按钮按下时,改背景,记录金额
     *
     * @param btn
     * @param price
     */
    private void moneyBtnClick(Button btn, int price) {
        money_50.setBackgroundResource(R.drawable.recharge_up);
        money_100.setBackgroundResource(R.drawable.recharge_up);
        money_200.setBackgroundResource(R.drawable.recharge_up);
        money_500.setBackgroundResource(R.drawable.recharge_up);
        money_1000.setBackgroundResource(R.drawable.recharge_up);
        money_other.setBackgroundResource(R.drawable.recharge_up);
        btn.setBackgroundResource(R.drawable.recharge_down);
        rechargePrice = price;
    }

    /**
     * 点击其他金额
     */
    private void moneyOtherClick() {
        final View editText = LayoutInflater.from(MemberDetailsHasListActivity.this).inflate(R.layout.view_dialog_editview, null);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("请输入充值金额");
        builder.setView(editText);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 获取EditView中的输入内容
                EditText edit_text =
                        (EditText) editText.findViewById(R.id.dialog_edit_text);
                Log.i("调起微信支付", "调起微信支付");
            }
        });
        builder.show();
    }

    /**
     * 确认提交按钮
     */
    private void submitRecharge() {
        /*
          这里使用了 android.support.v7.app.AlertDialog.Builder
          可以直接在头部写 import android.support.v7.app.AlertDialog
          那么下面就可以写成 AlertDialog.Builder
          */
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("确定给手机号189****1234充值" + rechargePrice + "元吗?");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.i("调起微信支付", "调起微信支付");
            }
        });
        builder.show();
    }
}
