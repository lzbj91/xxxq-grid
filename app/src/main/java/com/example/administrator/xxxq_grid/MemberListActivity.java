package com.example.administrator.xxxq_grid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.administrator.adapter.MemberListAdapter;
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

public class MemberListActivity extends BaseActivity {

    private MemberListAdapter memberlistAdapter;
    private List<Map<String, Object>> dataList;
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        initTitle();
        init();
        initData();
    }

    @Override
    protected void init() {
        recyclerView = (RecyclerView) findViewById(R.id.activity_member_list_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout = (RefreshLayout) findViewById(R.id.activity_member_list_view_refresh_view);
        refreshLayout.setPrimaryColorsId(R.color.color_green_theme, android.R.color.white);

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadMemberList(0, 1);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMemberList(1, ++currentPage);
            }
        });
    }

    @Override
    protected void initData() {
        loadMemberList(2, 1);
    }

    private void loadMemberList(final int type, final int page) {
        Map<String, Object> map = new HashMap<String, Object>();
        dataList = new ArrayList<Map<String, Object>>();
        if (page == 1) {//如果首次加载或者下拉刷新
            refreshLayout.setEnableLoadMore(true);
            currentPage = page;
        }
        map.put("limit", 20);
        map.put("page", page);
        new HttpRequest().post(MemberListActivity.this, "member/loadMemberList", map, new CallBackSuccess() {
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

    /**
     * 消息处理,通知主线程更新ui
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    memberlistAdapter.updateData(dataList);
                    break;
                case 1:
                    dataList = memberlistAdapter.addData(dataList);
                    break;
                case 2:
                    setMemberListData();
                    break;
            }
        }
    };

    private void setMemberListData() {
        memberlistAdapter = new MemberListAdapter(this, R.layout.adapter_member_list, dataList, refreshLayout);
        recyclerView.setAdapter(memberlistAdapter);
        memberlistAdapter.setOnItemClickListner(new BaseRecycleAdapter.OnItemClickListner() {
            @Override
            public void onItemClickListner(View v, int position) {
                Intent intent = new Intent(MemberListActivity.this, MemberDetailsHasListActivity.class);
                Bundle bundle = new Bundle();
                String id = BaseUtils.toString(dataList.get(position).get("id"));
                bundle.putCharSequence("id", id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        memberlistAdapter.setOnItemLongClickListner(new BaseRecycleAdapter.OnItemLongClickListner() {
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
        headerBar.setAppTitle("会员列表");
        headerBar.setOnLeftButtonClickListener(new HeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }
}
