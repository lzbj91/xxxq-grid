package com.example.administrator.xxxq_grid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.administrator.adapter.SupplierEvaluationListAdapter;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.HeaderBar;
import com.example.administrator.common.HttpRequest;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SupplierServiceEvaluationHasListActivity extends BaseActivity {
    private ListView listView;
    private SupplierEvaluationListAdapter supplierEvaluationListAdapter;
    private String id;
    private Map<String, Object> dataMap;
    private TextView name, address, phone, source, evaluation;
    private RatingBar ratingBar;
    private List<Map<String, Object>> dataList;

    private RefreshLayout refreshLayout;
    private int currentPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_service_evaluation_has_list);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");

        initTitle();
        init();
        initData();
    }

    @Override
    protected void init() {
        name = (TextView) findViewById(R.id.activity_supplier_service_evaluation_has_list_name);
        ratingBar = (RatingBar) findViewById(R.id.activity_supplier_service_evaluation_has_list_rating);
        source = (TextView) findViewById(R.id.activity_supplier_service_evaluation_has_list_source);
        evaluation = (TextView) findViewById(R.id.activity_supplier_service_evaluation_has_list_evaluation);
        phone = (TextView) findViewById(R.id.activity_supplier_service_evaluation_has_list_phone);
        address = (TextView) findViewById(R.id.activity_supplier_service_evaluation_has_list_address);

        listView = (ListView) findViewById(R.id.activity_supplier_service_evaluation_has_list_list);

        refreshLayout = (RefreshLayout) findViewById(R.id.activity_supplier_service_evaluation_has_list_refresh_view);
        refreshLayout.setPrimaryColorsId(R.color.color_green_theme, android.R.color.white);

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadSupplierEvaluationList(0, 1);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadSupplierEvaluationList(1, ++currentPage);
            }
        });
    }

    @Override
    protected void initData() {
        loadSupplierInfo();
        loadSupplierEvaluationList(2, 1);
    }

    private void loadSupplierInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        new HttpRequest().post(SupplierServiceEvaluationHasListActivity.this, "supplier/loadSupplierInfo", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                dataMap = (Map<String, Object>) data;
                handler.sendEmptyMessage(3);
            }
        });
    }

    private void loadSupplierEvaluationList(final int type, final int page) {
        final Map<String, Object> map = new HashMap<String, Object>();
        dataList = new ArrayList<Map<String, Object>>();
        if (page == 1) {//如果首次加载或者下拉刷新
            refreshLayout.setEnableLoadMore(true);
            currentPage = page;
        }
        map.put("id", id);
        map.put("limit", 20);
        map.put("page", page);

        new HttpRequest().post(SupplierServiceEvaluationHasListActivity.this, "supplier/loadSupplierEvaluationList", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                Map<String, Object> evaluationMap = (Map<String, Object>) data;
                dataList.addAll((List<Map<String, Object>>) evaluationMap.get("data"));
                if (Integer.parseInt(BaseUtils.toString(evaluationMap.get("pageCount"))) == page) {
                    refreshLayout.setEnableLoadMore(false);
                }
                handler.sendEmptyMessage(type);
            }
        });
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    supplierEvaluationListAdapter.updateData(dataList);
                    break;
                case 1:
                    dataList = supplierEvaluationListAdapter.addData(dataList);
                    break;
                case 2:
                    setEvaluationListValues();
                    break;
                case 3:
                    setInfoValues();
                    break;
            }
        }
    };

    private void setEvaluationListValues() {
        supplierEvaluationListAdapter = new SupplierEvaluationListAdapter(this, dataList, refreshLayout);
        listView.setAdapter(supplierEvaluationListAdapter);
    }

    private void setInfoValues() {
        name.setText(BaseUtils.toString(dataMap.get("name")));
        source.setText(BaseUtils.toString(dataMap.get("evaluate")) + "分");
        phone.setText("电话: " + BaseUtils.toString(dataMap.get("phone")));
        address.setText("地址: " + BaseUtils.toString(dataMap.get("address")));
        evaluation.setText("评价 (" + BaseUtils.toString(dataMap.get("evaluate")) + ")");
        ratingBar.setRating(Float.valueOf(BaseUtils.toString(dataMap.get("evaluate"))));
    }

    @Override
    protected void initTitle() {
        BaseUtils.initHeaderBar(this);
        HeaderBar headerBar = (HeaderBar) this.findViewById(R.id.headerBar);
        headerBar.initViewsVisible(true, true, false, false);
        headerBar.setAppTitle("供应商服务评价");
        headerBar.setOnLeftButtonClickListener(new HeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }
}
