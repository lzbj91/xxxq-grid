package com.example.administrator.xxxq_grid;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.administrator.adapter.MessageListAdapter;
import com.example.administrator.adapter.SupplierListAdapter;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseRecycleAdapter;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.DialogUtils;
import com.example.administrator.common.HeaderBar;
import com.example.administrator.common.HttpRequest;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageListActivity extends BaseActivity {

    private MessageListAdapter messageListAdapter;
    private RecyclerView recyclerView;
    private List<Map<String, Object>> dataList;
    private RefreshLayout refreshLayout;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);

        initTitle();
        init();
        initData();

    }

    @Override
    protected void initData() {
        //获取数据--供应商列表
        loadMessageList(2, 1);
    }

    @Override
    protected void init() {
        recyclerView = (RecyclerView) findViewById(R.id.activity_message_list_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout = (RefreshLayout) findViewById(R.id.activity_message_list_refresh_view);
        refreshLayout.setPrimaryColorsId(R.color.color_green_theme, android.R.color.white);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadMessageList(0, 1);
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadMessageList(1, ++currentPage);
            }
        });
    }

    private void loadMessageList(final int type, final int page) {
        Map<String, Object> map = new HashMap<String, Object>();
        dataList = new ArrayList<Map<String, Object>>();
        if (page == 1) {//如果首次加载或者下拉刷新
            refreshLayout.setEnableLoadMore(true);
            currentPage = page;
        }
        map.put("limit", 20);
        map.put("page", page);
        new HttpRequest().post(MessageListActivity.this, "mainActivity/loadNewsCount", map, new CallBackSuccess() {
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
                    messageListAdapter.updateData(dataList);
                    break;
                case 1:
                    dataList = messageListAdapter.addData(dataList);
                    break;
                case 2:
                    setListData();
                    break;
            }
        }
    };

    private void setListData() {
        messageListAdapter = new MessageListAdapter(MessageListActivity.this, R.layout.adapter_message_list, dataList, refreshLayout);
        recyclerView.setAdapter(messageListAdapter);
        messageListAdapter.setOnItemClickListner(new BaseRecycleAdapter.OnItemClickListner() {
            @Override
            public void onItemClickListner(final View v, int position) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", dataList.get(position).get("id"));
                new HttpRequest().post(MessageListActivity.this, "mainActivity/updateMessageState", map, new CallBackSuccess() {
                    @Override
                    public void onCallBackSuccess(Object data) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                v.findViewById(R.id.adapter_message_list_read).setVisibility(View.GONE);
                            }
                        });
                    }
                });

                if (Integer.parseInt(BaseUtils.toString(dataList.get(position).get("type"))) == 1) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(MessageListActivity.this, SupplierServiceOrderDetailActivity.class);
                    String id = BaseUtils.toString(dataList.get(position).get("orderInfo"));
                    bundle.putCharSequence("id", id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    DialogUtils.showAlertDialog(MessageListActivity.this, BaseUtils.toString(dataList.get(position).get("content")));
                }
            }
        });
        messageListAdapter.setOnItemLongClickListner(new BaseRecycleAdapter.OnItemLongClickListner() {
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
        headerBar.setAppTitle("消息列表");
        headerBar.setOnLeftButtonClickListener(new HeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }
}
