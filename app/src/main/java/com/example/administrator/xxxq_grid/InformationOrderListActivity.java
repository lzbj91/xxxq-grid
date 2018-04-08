package com.example.administrator.xxxq_grid;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.adapter.InformationOrderListAdapter;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseRecycleAdapter;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.HeaderBar;
import com.example.administrator.common.HttpRequest;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InformationOrderListActivity extends BaseActivity {

    private List<Map<String, Object>> dataList;
    private RecyclerView recyclerView;
    private InformationOrderListAdapter informationOrderListAdapter;
    private RefreshLayout refreshLayout;
    private TextView click1, click2, click3, click4;
    private ImageView img1, img2, img3;
    private int currentClick = 1;//当前点击tabs第几个作为标记
    private boolean temp = true;//当重复点击该标签时,升降序排序
    private String date;
    private int currentPage;
    private Map<String, Object> uploadDataMap = new HashMap<String, Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_order_list);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        date = bundle.getString("date");

        initTitle();
        init();
        initData();
    }

    @Override
    protected void init() {
        recyclerView = (RecyclerView) findViewById(R.id.activity_information_order_list_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout = (RefreshLayout) findViewById(R.id.activity_information_order_list_refresh_view);
        refreshLayout.setPrimaryColorsId(R.color.color_green_theme, android.R.color.white);
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                loadOrderList(1, ++currentPage);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadOrderList(0, 1);
            }
        });

        click1 = (TextView) findViewById(R.id.activity_information_order_list_click1);
        click2 = (TextView) findViewById(R.id.activity_information_order_list_click2);
        click3 = (TextView) findViewById(R.id.activity_information_order_list_click3);
        click4 = (TextView) findViewById(R.id.activity_information_order_list_click4);

        click1.setOnClickListener(l);
        click2.setOnClickListener(l);
        click3.setOnClickListener(l);
        click4.setOnClickListener(l);

        img1 = (ImageView) findViewById(R.id.activity_information_order_list_img1);
        img2 = (ImageView) findViewById(R.id.activity_information_order_list_img2);
        img3 = (ImageView) findViewById(R.id.activity_information_order_list_img3);
    }

    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.activity_information_order_list_click1:
                    //刷新数据
                    log(1, "我默认综合排序");
                    uploadDataMap.remove("numOrder");
                    uploadDataMap.remove("completeOrder");
                    uploadDataMap.put("smart", true);
                    loadOrderList(0, 1);
                    //初始化样式状态
                    initClickState();
                    //更改第一个样式状态
                    setClickState(1);
                    break;
                case R.id.activity_information_order_list_click2:
                    //刷新数据
                    if (currentClick != 2) {
                        //升序排列
                        uploadDataMap.put("numOrder", "up");
                        log(1, "我默认升序排列");
                        currentClick = 2;
                        temp = true;
                    } else {
                        if (temp) {
                            //降序排列
                            temp = false;
                            uploadDataMap.put("numOrder", "down");
                            log(2, "我重复点击一次,降序排列");
                        } else {
                            //升序排列
                            temp = true;
                            uploadDataMap.put("numOrder", "up");
                            log(3, "我又被点击了一次,升序排列");
                        }
                    }
                    uploadDataMap.remove("smart");
                    uploadDataMap.remove("completeOrder");
                    loadOrderList(0, 1);
                    //初始化样式状态
                    initClickState();
                    //更改第一个样式状态
                    setClickState(2);
                    break;
                case R.id.activity_information_order_list_click3:
                    //刷新数据
                    if (currentClick != 3) {
                        //升序排列
                        log(1, "我默认升序排列");
                        uploadDataMap.put("completeOrder", "up");
                        currentClick = 3;
                        temp = true;
                    } else {
                        if (temp) {
                            //降序排列
                            uploadDataMap.put("completeOrder", "down");
                            temp = false;
                            log(2, "我重复点击一次,降序排列");
                        } else {
                            //升序排列
                            uploadDataMap.put("completeOrder", "up");
                            temp = true;
                            log(3, "我又被点击了一次,升序排列");
                        }
                    }
                    uploadDataMap.remove("smart");
                    uploadDataMap.remove("numOrder");
                    loadOrderList(0, 1);
                    //初始化样式状态
                    initClickState();
                    //更改第一个样式状态
                    setClickState(3);
                    break;
                case R.id.activity_information_order_list_click4:
                    showDatePicker();
                    break;
            }
        }
    };

    private void initClickState() {
        click1.setTextColor(Color.parseColor("#999999"));
        click2.setTextColor(Color.parseColor("#999999"));
        click3.setTextColor(Color.parseColor("#999999"));

        img1.setImageResource(R.drawable.down2);
        img2.setImageResource(R.drawable.down3);
        img3.setImageResource(R.drawable.down3);
    }

    private void setClickState(int i) {
        if (i == 1) {
            click1.setTextColor(Color.parseColor("#FF2EC39A"));
            img1.setImageResource(R.drawable.down1);
        }
        if (i == 2) {
            click2.setTextColor(Color.parseColor("#FF2EC39A"));
            img2.setImageResource(R.drawable.down4);
        }
        if (i == 3) {
            click3.setTextColor(Color.parseColor("#FF2EC39A"));
            img3.setImageResource(R.drawable.down4);
        }
    }

    @Override
    protected void initData() {
        uploadDataMap.put("smart", true);
        loadOrderList(2, 1);
    }

    private void loadOrderList(final int type, final int page) {
        dataList = new ArrayList<Map<String, Object>>();
        if (page == 1) {//如果首次加载或者下拉刷新
            refreshLayout.setEnableLoadMore(true);
            currentPage = page;
        }
        uploadDataMap.put("limit", 20);
        uploadDataMap.put("page", page);
        uploadDataMap.put("date", date);
        new HttpRequest().post(InformationOrderListActivity.this, "information/loadServiceList", uploadDataMap, new CallBackSuccess() {
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

    private void setListData() {
        informationOrderListAdapter = new InformationOrderListAdapter(this, R.layout.adapter_information_order_list, dataList, refreshLayout);
        recyclerView.setAdapter(informationOrderListAdapter);
        informationOrderListAdapter.setOnItemClickListner(new BaseRecycleAdapter.OnItemClickListner() {
            @Override
            public void onItemClickListner(View v, int position) {
            }
        });
        informationOrderListAdapter.setOnItemLongClickListner(new BaseRecycleAdapter.OnItemLongClickListner() {
            @Override
            public void onItemLongClickListner(View v, int position) {
                return;
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
                    informationOrderListAdapter.updateData(dataList);
                    break;
                case 1:
                    dataList = informationOrderListAdapter.addData(dataList);
                    break;
                case 2:
                    setListData();
                    break;
            }
        }
    };

    /*******************************隐藏日期的日期选择器(已封装好)START*****************************************/
    private void showDatePicker() {
        //获取当前日期
        Calendar calendar = Calendar.getInstance();
        int year, month, day;
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        //创建并显示DatePickerDialog
        DatePickerDialog dialog = new DatePickerDialog(InformationOrderListActivity.this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                date = year + "-" + (month + 1) + "-01";
                loadOrderList(0, 1);
            }
        }, year, month, day);
        dialog.show();
        //只显示年月，隐藏掉日
        DatePicker dp = findDatePicker((ViewGroup) dialog.getWindow().getDecorView());
        if (dp != null) {
            ((ViewGroup) ((ViewGroup) dp.getChildAt(0)).getChildAt(0)).getChildAt(2).setVisibility(View.GONE);
            //如果想隐藏掉年，将getChildAt(2)改为getChildAt(0)
        }
    }

    private DatePicker findDatePicker(ViewGroup group) {
        if (group != null) {
            for (int i = 0, j = group.getChildCount(); i < j; i++) {
                View child = group.getChildAt(i);
                if (child instanceof DatePicker) {
                    return (DatePicker) child;
                } else if (child instanceof ViewGroup) {
                    DatePicker result = findDatePicker((ViewGroup) child);
                    if (result != null)
                        return result;
                }
            }
        }
        return null;
    }

    /*******************************隐藏日期的日期选择器END*****************************************/

    @Override
    protected void initTitle() {
        BaseUtils.initHeaderBar(this);
        HeaderBar headerBar = (HeaderBar) this.findViewById(R.id.headerBar);
        headerBar.initViewsVisible(true, true, false, false);
        headerBar.setAppTitle("订单分类列表");
        headerBar.setOnLeftButtonClickListener(new HeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }
}
