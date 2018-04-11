package com.example.administrator.xxxq_grid;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.example.administrator.adapter.MemberListAdapter;
import com.example.administrator.adapter.SupplierListAdapter;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseRecycleAdapter;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.HeaderBar;
import com.example.administrator.common.HttpRequest;
import com.example.administrator.common.SearchHeaderBar;
import com.insplatform.core.utils.TextUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends BaseActivity {

    private EditText search;
    private String type;


    private SupplierListAdapter supplierListAdapter;
    private MemberListAdapter memberlistAdapter;
    private List<Map<String, Object>> dataList;
    private RefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        type = bundle.getString("type");

        initTitle();
        init();
    }

    @Override
    protected void init() {
        search = (EditText) findViewById(R.id.tvCenterTitle);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (TextUtil.isEmpty(search.getText().toString())) {
                        showToast("请输入要搜索的内容");
                    } else {
                        loadDataList(2, 1);
                    }
                }
                return false;
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.activity_search_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout = (RefreshLayout) findViewById(R.id.activity_search_refresh_view);
        refreshLayout.setPrimaryColorsId(R.color.color_green_theme, android.R.color.white);

        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (TextUtil.isEmpty(search.getText().toString())) {
                    showToast("请输入要搜索的内容");
                    refreshLayout.finishRefresh();
                } else {
                    loadDataList(0, 1);
                }
            }

            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (TextUtil.isEmpty(search.getText().toString())) {
                    showToast("请输入要搜索的内容");
                    refreshLayout.autoLoadMore();
                } else {
                    loadDataList(1, ++currentPage);
                }
            }
        });

    }

    private void loadDataList(final int type, final int page) {
        Map<String, Object> map = new HashMap<String, Object>();
        dataList = new ArrayList<Map<String, Object>>();
        if (page == 1) {//如果首次加载或者下拉刷新
            refreshLayout.setEnableLoadMore(true);
            currentPage = page;
        }
        String url = "";
        if ("member".equals(this.type)) {
            url = "member/loadMemberList";
        } else if ("supplier".equals(this.type)) {
            url = "mainActivity/loadSupplierList";
        }
        map.put("limit", 20);
        map.put("page", page);
        if(TextUtil.isNotEmpty(search.getText().toString())){
            map.put("search", search.getText().toString());
        }
        new HttpRequest().post(SearchActivity.this, url, map, new CallBackSuccess() {
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
                    if ("member".equals(type)) {
                        memberlistAdapter.updateData(dataList);
                    } else if ("supplier".equals(type)) {
                        supplierListAdapter.updateData(dataList);
                    }
                    break;
                case 1:
                    if ("member".equals(type)) {
                        dataList = memberlistAdapter.addData(dataList);
                    } else if ("supplier".equals(type)) {
                        dataList = supplierListAdapter.addData(dataList);
                    }
                    break;
                case 2:
                    if ("member".equals(type)) {
                        setMemberListData();
                    } else if ("supplier".equals(type)) {
                        setListData();
                    }
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
                Intent intent = new Intent(SearchActivity.this, MemberDetailsHasListActivity.class);
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

    private void setListData() {
        supplierListAdapter = new SupplierListAdapter(SearchActivity.this, R.layout.adapter_suppliet_list, dataList, refreshLayout);
        recyclerView.setAdapter(supplierListAdapter);
        supplierListAdapter.setOnItemClickListner(new BaseRecycleAdapter.OnItemClickListner() {
            @Override
            public void onItemClickListner(View v, int position) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(SearchActivity.this, SupplierDetailsHasListActivity.class);
                String id = BaseUtils.toString(dataList.get(position).get("id"));
                bundle.putCharSequence("id", id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        supplierListAdapter.setOnItemLongClickListner(new BaseRecycleAdapter.OnItemLongClickListner() {
            @Override
            public void onItemLongClickListner(View v, int position) {
                return;
            }
        });
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initTitle() {
        BaseUtils.initHeaderBar(this);
        final SearchHeaderBar headerBar = (SearchHeaderBar) this.findViewById(R.id.headerBar);
        headerBar.initViewsVisible(true, true, false, true);
        headerBar.setOnRightButtonClickListener(new SearchHeaderBar.OnRightButtonClickListener() {
            @Override
            public void OnRightButtonClick(View v) {
                EditText editText = headerBar.findViewById(R.id.tvCenterTitle);
                editText.setText("");
            }
        });
        headerBar.setOnLeftButtonClickListener(new SearchHeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }
}
