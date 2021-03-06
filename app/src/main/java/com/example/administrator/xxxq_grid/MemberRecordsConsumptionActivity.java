package com.example.administrator.xxxq_grid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.administrator.adapter.MemberRecordsConsumptionListAdapter;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseRecycleAdapter;
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

public class MemberRecordsConsumptionActivity extends BaseActivity {
    private MemberRecordsConsumptionListAdapter memberRecordsConsumptionListAdapter;
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private List<Map<String, Object>> dataList;
    private int currentPage;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_records_consumption);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        id = bundle.getString("id");

        initTitle();
        init();
        initData();
    }

    @Override
    protected void init() {
        recyclerView = (RecyclerView) findViewById(R.id.activity_member_records_consumption_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout = (RefreshLayout) findViewById(R.id.activity_member_records_consumption_refresh_view);
        refreshLayout.setPrimaryColorsId(R.color.color_green_theme, android.R.color.white);

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadOrderList(0, 1);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadOrderList(1, ++currentPage);
            }
        });
    }

    @Override
    protected void initData() {
        loadOrderList(2, 1);
    }

    private void loadOrderList(final int type, final int page) {
        Map<String, Object> map = new HashMap<String, Object>();
        dataList = new ArrayList<Map<String, Object>>();
        if (page == 1) {//如果首次加载或者下拉刷新
            refreshLayout.setEnableLoadMore(true);
            currentPage = page;
        }
        map.put("id", id);
        map.put("state", "3,4");
        map.put("limit", 20);
        map.put("page", page);
        new HttpRequest().post(MemberRecordsConsumptionActivity.this, "member/loadMemberSaleRecord", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                Map<String, Object> map = (Map<String, Object>) data;
                List<Map<String, Object>> ls = (List<Map<String, Object>>) map.get("data");
                dataList.addAll(ls);
                if (Integer.parseInt(BaseUtils.toString(map.get("pageCount"))) == page) {
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
                    memberRecordsConsumptionListAdapter.updateData(dataList);
                    break;
                case 1:
                    dataList = memberRecordsConsumptionListAdapter.addData(dataList);
                    break;
                case 2:
                    setListValues();
                    break;
            }
        }
    };

    private void setListValues() {
        memberRecordsConsumptionListAdapter = new MemberRecordsConsumptionListAdapter(this, R.layout.adapter_member_records_consumption_list, dataList, refreshLayout);
        recyclerView.setAdapter(memberRecordsConsumptionListAdapter);
        memberRecordsConsumptionListAdapter.setOnItemClickListner(new BaseRecycleAdapter.OnItemClickListner() {
            @Override
            public void onItemClickListner(View v, int position) {
                //点击去订单详情页面
                Intent intent = new Intent(MemberRecordsConsumptionActivity.this, SupplierServiceOrderDetailActivity.class);
                Bundle bundle = new Bundle();
                String id = BaseUtils.toString(dataList.get(position).get("id"));
                bundle.putCharSequence("id", id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        memberRecordsConsumptionListAdapter.setOnItemLongClickListner(new BaseRecycleAdapter.OnItemLongClickListner() {
            @Override
            public void onItemLongClickListner(View v, int position) {
                return;
            }
        });
    }

    @Override
    protected void initTitle() {
        BaseUtils.initHeaderBar(this);
        HeaderBar headerBar = (HeaderBar) this.findViewById(R.id.headerBar);
        headerBar.initViewsVisible(true, true, false, false);
        headerBar.setAppTitle("消费记录");
        headerBar.setOnLeftButtonClickListener(new HeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }
}
