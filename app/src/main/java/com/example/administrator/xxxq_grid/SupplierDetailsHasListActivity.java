package com.example.administrator.xxxq_grid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.administrator.adapter.SupplierDetailListAdapter;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseRecycleAdapter;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.HeaderBar;
import com.example.administrator.common.HttpRequest;
import com.scwang.smartrefresh.layout.api.RefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierDetailsHasListActivity extends BaseActivity {

    private SupplierDetailListAdapter supplierDetailListAdapter;
    private RelativeLayout list1, list2, list3;
    private String id;//前一个页面传来的id值
    private Map<String, Object> dataMap;
    private ImageView avatarImageView;
    private RatingBar ratingBar;
    private TextView source, title, address, phone, more;
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private List<Map<String, Object>> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_detail);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = BaseUtils.toString(bundle.get("id"));

        initTitle();
        init();
        initData();
    }

    @Override
    protected void initData() {
        //获取供应商详情信息
        loadSupplierInfo();
        //获取商家订单
        loadBusinessOrder();
    }


    @Override
    protected void init() {
        avatarImageView = (ImageView) findViewById(R.id.activity_supplier_detail_avatar);
        ratingBar = (RatingBar) findViewById(R.id.activity_supplier_detail_ratingBar);
        source = (TextView) findViewById(R.id.activity_supplier_detail_source);
        title = (TextView) findViewById(R.id.activity_supplier_detail_title);
        address = (TextView) findViewById(R.id.activity_supplier_detail_address);
        phone = (TextView) findViewById(R.id.activity_supplier_detail_phone);
        recyclerView = (RecyclerView) findViewById(R.id.activity_supplier_detail_list);
        //scroll和recycleView同时出现滚动干扰问题
        BaseUtils.scrollAndRecycleViewScroll(this, recyclerView);

        more = (TextView) findViewById(R.id.activity_supplier_detail_more);
        list1 = (RelativeLayout) findViewById(R.id.activity_supplier_detail_list1);
        list2 = (RelativeLayout) findViewById(R.id.activity_supplier_detail_list2);
        list3 = (RelativeLayout) findViewById(R.id.activity_supplier_detail_list3);

        list1.setOnClickListener(l);
        list2.setOnClickListener(l);
        list3.setOnClickListener(l);
        more.setOnClickListener(l);
    }

    private void loadSupplierInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        new HttpRequest().post(SupplierDetailsHasListActivity.this, "supplier/loadSupplierInfo", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                dataMap = (Map<String, Object>) data;
                handler.sendEmptyMessage(1);
            }
        });
    }

    private void loadBusinessOrder() {
        dataList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("bsSupplier", id);
        map.put("limit", 10);
        new HttpRequest().post(SupplierDetailsHasListActivity.this, "supplier/loadSupplierList", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                Map<String, Object> map = (Map<String, Object>) data;
                List<Map<String, Object>> ls = (List<Map<String, Object>>) map.get("data");
                for (int i = 0; i < ls.size(); i++) {
                    dataList.add(ls.get(i));
                }
                handler.sendEmptyMessage(2);
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
                case 2:
                    setListValues();
                    break;
            }
        }
    };

    private void setListValues() {
        supplierDetailListAdapter = new SupplierDetailListAdapter(this, R.layout.adapter_supplier_detail_list, dataList, refreshLayout);
        recyclerView.setAdapter(supplierDetailListAdapter);
        supplierDetailListAdapter.setOnItemClickListner(new BaseRecycleAdapter.OnItemClickListner() {
            @Override
            public void onItemClickListner(View v, int position) {
                //点击去订单详情页面
                Intent intent = new Intent(SupplierDetailsHasListActivity.this, SupplierServiceOrderDetailActivity.class);
                Bundle bundle = new Bundle();
                String id = BaseUtils.toString(dataList.get(position).get("id"));
                bundle.putCharSequence("id", id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        supplierDetailListAdapter.setOnItemLongClickListner(new BaseRecycleAdapter.OnItemLongClickListner() {
            @Override
            public void onItemLongClickListner(View v, int position) {
                return;
            }
        });
    }

    private void setInfoValues() {
        Glide.with(this).load(new HttpRequest().getFileUrl() + BaseUtils.toString(dataMap.get("photo"))).placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(avatarImageView);
        ratingBar.setRating(Float.valueOf(BaseUtils.toString(dataMap.get("evaluate"))));
        source.setText(BaseUtils.toString(dataMap.get("evaluate")) + "分");
        title.setText(BaseUtils.toString(dataMap.get("name")));
        address.setText("地址: " + BaseUtils.toString(dataMap.get("detailAdress")));
        phone.setText("电话: " + BaseUtils.toString(dataMap.get("phone")));
    }

    @Override
    protected void initTitle() {
        BaseUtils.initHeaderBar(this);
        HeaderBar headerBar = (HeaderBar) this.findViewById(R.id.headerBar);
        headerBar.initViewsVisible(true, true, false, false);
        headerBar.setAppTitle("供应商信息");
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
                case R.id.activity_supplier_detail_list1:
                    Intent intent1 = new Intent(SupplierDetailsHasListActivity.this, SupplierServiceOrderListActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putCharSequence("id", id);
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                    break;
                case R.id.activity_supplier_detail_list2:
                    Intent intent2 = new Intent(SupplierDetailsHasListActivity.this, SupplierServiceEvaluationHasListActivity.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putCharSequence("id", id);
                    intent2.putExtras(bundle2);
                    startActivity(intent2);
                    break;
                case R.id.activity_supplier_detail_list3:
                    Intent intent3 = new Intent(SupplierDetailsHasListActivity.this, FieldExplorationActivity.class);
                    Bundle bundle3 = new Bundle();
                    bundle3.putCharSequence("id", id);
                    intent3.putExtras(bundle3);
                    startActivity(intent3);
                    break;
                case R.id.activity_supplier_detail_more:
                    Intent intent4 = new Intent(SupplierDetailsHasListActivity.this, SupplierServiceOrderListActivity.class);
                    Bundle bundle4 = new Bundle();
                    bundle4.putCharSequence("id", id);
                    intent4.putExtras(bundle4);
                    startActivity(intent4);
                    break;
            }
        }
    };
}
