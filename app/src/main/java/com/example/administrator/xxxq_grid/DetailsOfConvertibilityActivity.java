package com.example.administrator.xxxq_grid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.HeaderBar;
import com.example.administrator.common.HttpRequest;
import com.example.administrator.fragment.CrediteChangeSucesDialogFragment;

import java.util.HashMap;
import java.util.Map;

public class DetailsOfConvertibilityActivity extends BaseActivity {

    private String id;
    private Map<String, Object> dataMap;
    private TextView title, card, orderNum, time1, time2, countNum, score, userName, userAge, grid, phone, memberCard;
    private Button submit;
    private ImageView pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_integral_exchange_order);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");

        initTitle();
        init();
        initData();
    }

    @Override
    protected void init() {
        submit = (Button) findViewById(R.id.activity_integral_exchange_order_submit);
        submit.setVisibility(View.GONE);
        pic = (ImageView) findViewById(R.id.activity_integral_exchange_order_pic);
        title = (TextView) findViewById(R.id.activity_integral_exchange_order_title);
        card = (TextView) findViewById(R.id.activity_integral_exchange_order_card);
        orderNum = (TextView) findViewById(R.id.activity_integral_exchange_order_order_number);
        time1 = (TextView) findViewById(R.id.activity_integral_exchange_order_time1);
        time2 = (TextView) findViewById(R.id.activity_integral_exchange_order_time2);
        countNum = (TextView) findViewById(R.id.activity_integral_exchange_order_count_num);
        score = (TextView) findViewById(R.id.activity_integral_exchange_order_score);
        userName = (TextView) findViewById(R.id.activity_integral_exchange_order_user_name);
        userAge = (TextView) findViewById(R.id.activity_integral_exchange_order_user_age);
        grid = (TextView) findViewById(R.id.activity_integral_exchange_order_grid);
        phone = (TextView) findViewById(R.id.activity_integral_exchange_order_phone);
        memberCard = (TextView) findViewById(R.id.activity_integral_exchange_order_member_card);
    }

    @Override
    protected void initData() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        new HttpRequest().post(this, "member/loadIgOrderUserInfo", map, new CallBackSuccess() {
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
                    title.setText(BaseUtils.toString(dataMap.get("goodName")));
                    card.setText("积分: " + BaseUtils.toString(dataMap.get("integral")));
                    orderNum.setText(BaseUtils.toString(dataMap.get("orderNum")));
                    time1.setText(BaseUtils.toString(dataMap.get("orderTime")));
                    time2.setText(BaseUtils.toString(dataMap.get("effectiveTime")));
                    countNum.setText("1");
                    score.setText(BaseUtils.toString(dataMap.get("integral")));
                    userName.setText(BaseUtils.toString(dataMap.get("name")));
                    userAge.setText(BaseUtils.toString(dataMap.get("age")));
                    grid.setText(BaseUtils.toString(dataMap.get("memberGrid")));
                    phone.setText(BaseUtils.toString(dataMap.get("phone")));
                    memberCard.setText(BaseUtils.toString(dataMap.get("memberCard")));
                    Glide.with(DetailsOfConvertibilityActivity.this).load(new HttpRequest().getFileUrl() + BaseUtils.toString(dataMap.get("photo"))).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(pic);
                    break;
                case 2:
                    //弹出积分兑换成功 2秒自动消失
                    final CrediteChangeSucesDialogFragment ccsDialogFragment = new CrediteChangeSucesDialogFragment();
                    ccsDialogFragment.show(getFragmentManager(), "ccsDialogFragment");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ccsDialogFragment.dismiss();
                        }
                    }, 2000);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2500);
                    break;
            }
        }
    };

    @Override
    protected void initTitle() {
        BaseUtils.initHeaderBar(this);
        HeaderBar headerBar = (HeaderBar) this.findViewById(R.id.headerBar);
        headerBar.initViewsVisible(true, true, false, false);
        headerBar.setAppTitle("积分兑换详情");
        headerBar.setOnLeftButtonClickListener(new HeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }
}
