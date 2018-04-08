package com.example.administrator.xxxq_grid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.HeaderBar;
import com.example.administrator.common.HttpRequest;
import com.insplatform.core.utils.TextUtil;

import java.util.HashMap;
import java.util.Map;

public class SupplierServiceOrderDetailActivity extends BaseActivity {

    private TextView orderNum, userName, title, time, orderNum1, orderTime, count, bookingTime,
            totalPrice, name, age, grid, memberType, phone, cardNumber, serviceName, serviceContent, getMemberType, typeTime;
    private ImageView avatar;
    private String id;
    private Map<String, Object> dataMap;
    private LinearLayout show1, show2, bookingLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_service_order_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");

        initTitle();
        init();
        initData();

    }

    @Override
    protected void init() {
        orderNum = (TextView) findViewById(R.id.activity_supplier_service_order_detail_order_num);
        userName = (TextView) findViewById(R.id.activity_supplier_service_order_detail_user_name);
        title = (TextView) findViewById(R.id.activity_supplier_service_order_detail_title);
        time = (TextView) findViewById(R.id.activity_supplier_service_order_detail_order_time);
        orderNum1 = (TextView) findViewById(R.id.activity_supplier_service_order_detail_order_num1);
        orderTime = (TextView) findViewById(R.id.activity_supplier_service_order_detail_order_time);
        count = (TextView) findViewById(R.id.activity_supplier_service_order_detail_count);
        totalPrice = (TextView) findViewById(R.id.activity_supplier_service_order_detail_total_price);
        name = (TextView) findViewById(R.id.activity_supplier_service_order_detail_name);
        age = (TextView) findViewById(R.id.activity_supplier_service_order_detail_age);
        grid = (TextView) findViewById(R.id.activity_supplier_service_order_detail_grid);
        memberType = (TextView) findViewById(R.id.activity_supplier_service_order_detail_member_type);
        phone = (TextView) findViewById(R.id.activity_supplier_service_order_detail_phone);
        cardNumber = (TextView) findViewById(R.id.activity_supplier_service_order_detail_card_number);
        serviceName = (TextView) findViewById(R.id.activity_supplier_service_order_detail_service_name);
        serviceContent = (TextView) findViewById(R.id.activity_supplier_service_order_detail_service_content);
        avatar = (ImageView) findViewById(R.id.activity_supplier_service_order_detail_avatar);
        show1 = (LinearLayout) findViewById(R.id.activity_supplier_service_order_detail_show1);
        show2 = (LinearLayout) findViewById(R.id.activity_supplier_service_order_detail_show2);
        typeTime = (TextView) findViewById(R.id.activity_supplier_service_order_detail_service_type_time);
        bookingTime = (TextView) findViewById(R.id.activity_supplier_service_order_detail_booking_time);
        bookingLine = (LinearLayout) findViewById(R.id.activity_supplier_service_order_detail_booking_line);
    }

    @Override
    protected void initData() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        new HttpRequest().post(SupplierServiceOrderDetailActivity.this, "supplier/loadSupplierOrderInfo", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                dataMap = (Map<String, Object>) data;
                handler.sendEmptyMessage(1);
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    setInfoValues();
                    break;
            }
        }
    };

    private void setInfoValues() {
        orderNum.setText(BaseUtils.toString(dataMap.get("orderNum")));
        userName.setText(BaseUtils.toString(dataMap.get("name")));
        title.setText(BaseUtils.toString(dataMap.get("serveName")));
        time.setText(BaseUtils.toString(dataMap.get("orderTime")));
        orderNum1.setText(BaseUtils.toString(dataMap.get("orderNum")));
        orderTime.setText(BaseUtils.toString(dataMap.get("bookingTime")));
        count.setText(BaseUtils.toString(dataMap.get("orderCount")));
        totalPrice.setText(BaseUtils.toString(dataMap.get("totalPrice")));
        name.setText(BaseUtils.toString(dataMap.get("name")));
        age.setText(BaseUtils.toString(dataMap.get("age")));
        grid.setText(BaseUtils.toString(dataMap.get("memberGrid")));
        memberType.setText(BaseUtils.toString(dataMap.get("memberType")));
        phone.setText(BaseUtils.toString(dataMap.get("phone")));
        cardNumber.setText(BaseUtils.toString(dataMap.get("memberCard")));
        serviceName.setText(BaseUtils.toString(dataMap.get("serveName")));
        serviceContent.setText(BaseUtils.toString(dataMap.get("serveContent")));
        String type = BaseUtils.toString(dataMap.get("type"));
        if ("3".equals(type)) {
            show1.setVisibility(View.VISIBLE);
            show2.setVisibility(View.VISIBLE);
            typeTime.setText(BaseUtils.toString(dataMap.get("specialStartTime") + "-" + BaseUtils.toString(dataMap.get("specialEndTime"))));
        } else {
            show1.setVisibility(View.GONE);
            show2.setVisibility(View.GONE);
        }
        if (TextUtil.isNotEmpty(BaseUtils.toString(dataMap.get("bookingTime")))) {
            bookingLine.setVisibility(View.VISIBLE);
            bookingTime.setText(BaseUtils.toString(dataMap.get("bookingTime")));
        } else {
            bookingLine.setVisibility(View.GONE);
        }
        Glide.with(this).load(new HttpRequest().getFileUrl() + BaseUtils.toString(dataMap.get("photo"))).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(avatar);
    }

    @Override
    protected void initTitle() {
        BaseUtils.initHeaderBar(this);
        HeaderBar headerBar = (HeaderBar) this.findViewById(R.id.headerBar);
        headerBar.initViewsVisible(true, true, false, false);
        headerBar.setAppTitle("服务订单详情");
        headerBar.setOnLeftButtonClickListener(new HeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }
}
