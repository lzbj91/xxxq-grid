package com.example.administrator.xxxq_grid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.administrator.adapter.HomeNewsAdapter;
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

public class HomeNewsListActivity extends BaseActivity {

    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private HomeNewsAdapter homeNewsAdapter;
    private List<Map<String, Object>> newsList;
    private int currentPage = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_news_list);
        initTitle();
        init();
        initData();
    }

    @Override
    protected void init() {
        recyclerView = (RecyclerView) findViewById(R.id.activity_home_news_list_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        refreshLayout = (RefreshLayout) findViewById(R.id.activity_home_news_list_refresh_view);
        refreshLayout.setPrimaryColorsId(R.color.color_green_theme, android.R.color.white);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadNewsList(0, 1);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadNewsList(1, ++currentPage);
            }
        });
    }

    @Override
    protected void initData() {
        //  开始加载数据
        newsList = new ArrayList<Map<String, Object>>();
        loadNewsList(2, 1);
    }

    public void loadNewsList(final int type, final int page) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (page == 1) {//如果首次加载或者下拉刷新
            refreshLayout.setEnableLoadMore(true);
            currentPage = page;
        }
        map.put("limit", 20);
        map.put("page", page);
        new HttpRequest().post(HomeNewsListActivity.this, "mainActivity/homeNews", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                Map<String, Object> map = (Map<String, Object>) data;
                newsList = (List<Map<String, Object>>) map.get("data");
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
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    homeNewsAdapter.updateData(newsList);
                    break;
                case 1:
                    newsList = homeNewsAdapter.addData(newsList);
                    break;
                case 2:
                    setListValues();
                    break;
                default:
                    break;
            }
        }
    };

    private void setListValues() {
        homeNewsAdapter = new HomeNewsAdapter(this, R.layout.adapter_home_news_list, newsList, refreshLayout);
        recyclerView.setAdapter(homeNewsAdapter);
        homeNewsAdapter.setOnItemClickListner(new BaseRecycleAdapter.OnItemClickListner() {
            @Override
            public void onItemClickListner(View v, int position) {
                Intent intent = new Intent(HomeNewsListActivity.this, HomeNewsDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", BaseUtils.toString(newsList.get(position).get("name")));
                bundle.putString("id", BaseUtils.toString(newsList.get(position).get("id")));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        homeNewsAdapter.setOnItemLongClickListner(new BaseRecycleAdapter.OnItemLongClickListner() {
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
        headerBar.setAppTitle("新闻列表");
        headerBar.setOnLeftButtonClickListener(new HeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }
}
