package com.example.administrator.xxxq_grid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.adapter.SupplierListAdapter;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseRecycleAdapter;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.DialogUtils;
import com.example.administrator.common.HttpRequest;
import com.example.administrator.fragment.CreditsChangeDialogFragment;
import com.example.administrator.view.LimitScrollerView;
import com.insplatform.core.utils.TextUtil;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends BaseActivity {

    private long exitTime = 0;
    private LimitScrollerView limitScroll;//自动滚动的今日头条
    private MainScrollNewsAdapter adapter;//滚动头条的adapter
    private RecyclerView recyclerView;
    private SupplierListAdapter supplierListAdapter;
    private LinearLayout homeIndex1, homeIndex2, homeIndex3, homeIndex4, homeIndex5, homeIndex6;
    private ImageView person, news;
    private CreditsChangeDialogFragment creditsChangeDialogFragment;
    private List<String> newsList;
    private List<Map<String, Object>> supplierList;
    private TextView more, messageCount;
    private ImageView todayimage;

    private RefreshLayout refreshLayout;

    public static boolean isForeground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BaseUtils.initHeaderBar(this);

        initTitle();
        init();
        initData();

        /****** 接收极光推送的receiver  ******/
        registerMessageReceiver();  // used for receive msg

        /********************************首页滚动****************************************/
        limitScroll = (LimitScrollerView) findViewById(R.id.limitScroll);
        //API：4、设置条目点击事件
        limitScroll.setOnItemClickListener(new LimitScrollerView.OnItemClickListener() {
            @Override
            public void onItemClick(Object obj) {
                Intent intent = new Intent(MainActivity.this, HomeNewsListActivity.class);
                startActivity(intent);
            }
        });
        //API:1、设置数据适配器
        adapter = new MainScrollNewsAdapter();
        limitScroll.setDataAdapter(adapter);
        initScrollAdapterData();
        /********************************首页滚动****************************************/
    }

    @Override
    protected void onStart() {
        super.onStart();
        limitScroll.startScroll();    //API:2、开始滚动
    }

    @Override
    protected void onResume() {
        isForeground = true;
        //获取数据--推送消息数量
        loadNewsCount();
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //API:3、停止滚动
        limitScroll.cancel();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    //页面所有项目的点击事件
    View.OnClickListener l = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.activity_main_person:
                    Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                    startActivity(intent);
                    break;
                case R.id.homeIndex1:
                    Intent intent1 = new Intent(MainActivity.this, MemberListActivity.class);
                    startActivity(intent1);
                    break;
                case R.id.homeIndex2:
                    Intent intent2 = new Intent(MainActivity.this, SupplierListActivity.class);
                    startActivity(intent2);
                    break;
                case R.id.homeIndex3:
                    Intent intent3 = new Intent(MainActivity.this, InformationResourceActivity.class);
                    startActivity(intent3);
                    break;
                case R.id.homeIndex4:
                    //弹出积分兑换
                    creditsChangeDialogFragment = new CreditsChangeDialogFragment();
                    creditsChangeDialogFragment.show(getFragmentManager(), "creditsChangeDialogFragment");
                    break;
                case R.id.homeIndex5:
                    Intent intent5 = new Intent(MainActivity.this, FieldExplorationActivity.class);
                    startActivity(intent5);
                    break;
                case R.id.homeIndex6:
                    Intent intent6 = new Intent(MainActivity.this, GridManagerActivity.class);
                    startActivity(intent6);
                    break;
                case R.id.activity_main_more:
                    Intent intent7 = new Intent(MainActivity.this, SupplierListActivity.class);
                    startActivity(intent7);
                    break;
                case R.id.todayimage:
                    Intent intentToday = new Intent(MainActivity.this, HomeNewsListActivity.class);
                    startActivity(intentToday);
                    break;
                case R.id.activity_main_message:
                    Intent intentMessage = new Intent(MainActivity.this, MessageListActivity.class);
                    startActivity(intentMessage);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void init() {
        //会员信息
        homeIndex1 = (LinearLayout) findViewById(R.id.homeIndex1);
        homeIndex1.setOnClickListener(l);
        //供应商信息
        homeIndex2 = (LinearLayout) findViewById(R.id.homeIndex2);
        homeIndex2.setOnClickListener(l);
        //资源库点击事件
        homeIndex3 = (LinearLayout) findViewById(R.id.homeIndex3);
        homeIndex3.setOnClickListener(l);
        //积分兑换
        homeIndex4 = (LinearLayout) findViewById(R.id.homeIndex4);
        homeIndex4.setOnClickListener(l);
        //现场勘察
        homeIndex5 = (LinearLayout) findViewById(R.id.homeIndex5);
        homeIndex5.setOnClickListener(l);
        //网格管理
        homeIndex6 = (LinearLayout) findViewById(R.id.homeIndex6);
        homeIndex6.setOnClickListener(l);

        person = (ImageView) findViewById(R.id.activity_main_person);
        person.setOnClickListener(l);

        news = (ImageView) findViewById(R.id.activity_main_message);
        news.setOnClickListener(l);

        //底部列表
        recyclerView = (RecyclerView) findViewById(R.id.activity_main_list);
        //scroll和recycleView同时出现滚动干扰问题
        BaseUtils.scrollAndRecycleViewScroll(this, recyclerView);

        //点击查看更多
        more = (TextView) findViewById(R.id.activity_main_more);
        more.setOnClickListener(l);

        todayimage = (ImageView) findViewById(R.id.todayimage);
        todayimage.setOnClickListener(l);

        refreshLayout = (RefreshLayout) findViewById(R.id.activity_main_refresh_view);
        refreshLayout.setPrimaryColorsId(R.color.color_green_theme, android.R.color.white);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                loadHomeSupplierList();
                refreshLayout.finishRefresh();
            }
        });

        messageCount = (TextView) findViewById(R.id.activity_main_message_count);

        String udid = BaseUtils.getImei(getApplicationContext(), "");
        if (null != udid) log(1, "IMEI: " + udid);
        String appKey = BaseUtils.getAppKey(getApplicationContext());
        if (null == appKey) appKey = "AppKey异常";
        log(1, "AppKey: " + appKey);
        log(1, "RegId:" + JPushInterface.getRegistrationID(getApplicationContext()));
        if (TextUtil.isEmpty(JPushInterface.getRegistrationID(getApplicationContext()))) {
            DialogUtils.showAlertDialog(MainActivity.this, "暂未获取到推送ID,请联系管理员");
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("registerId", JPushInterface.getRegistrationID(getApplicationContext()));
            new HttpRequest().post(MainActivity.this, "mainActivity/updateUserRegisterId", map, new CallBackSuccess() {
                @Override
                public void onCallBackSuccess(Object data) {
                }
            });
        }
        String packageName = getPackageName();
        log(1, "PackageName: " + packageName);
        String deviceId = BaseUtils.getDeviceId(getApplicationContext());
        log(1, "deviceId:" + deviceId);
    }

    @Override
    protected void initData() {
        //获取数据--滚动新闻
        loadHomeNews();
        //获取数据--供应商列表
        loadHomeSupplierList();
    }

    private void loadNewsCount() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("isNotRead", true);
        new HttpRequest().post(MainActivity.this, "mainActivity/loadNewsCount", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                final Map<String, Object> map = (Map<String, Object>) data;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (Integer.parseInt(BaseUtils.toString(map.get("total"))) > 0) {
                            messageCount.setText(BaseUtils.toString(map.get("total")));
                            messageCount.setVisibility(View.VISIBLE);
                        } else {
                            messageCount.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void initTitle() {
    }

    /**
     * 获取数据--滚动新闻
     */
    private void loadHomeNews() {
        newsList = new ArrayList<String>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("limit", 999);
        new HttpRequest().post(MainActivity.this, "mainActivity/homeNews", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                Map<String, Object> map = (Map<String, Object>) data;
                List<Map<String, Object>> ls = (List<Map<String, Object>>) map.get("data");
                for (int i = 0; i < ls.size(); i++) {
                    Map<String, Object> temp = (Map<String, Object>) ls.get(i);
                    newsList.add(temp.get("name") + "");
                }
            }
        });
    }

    private void loadHomeSupplierList() {
        supplierList = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("limit", 10);
        new HttpRequest().post(MainActivity.this, "mainActivity/loadSupplierList", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                Map<String, Object> map = (Map<String, Object>) data;
                List<Map<String, Object>> ls = (List<Map<String, Object>>) map.get("data");
                for (int i = 0; i < ls.size(); i++) {
                    supplierList.add(ls.get(i));
                }
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
                    supplierListAdapter = new SupplierListAdapter(MainActivity.this, R.layout.adapter_suppliet_list, supplierList, refreshLayout);
                    recyclerView.setAdapter(supplierListAdapter);
                    supplierListAdapter.setOnItemClickListner(new BaseRecycleAdapter.OnItemClickListner() {
                        @Override
                        public void onItemClickListner(View v, int position) {
                            Bundle bundle = new Bundle();
                            Intent intent = new Intent(MainActivity.this, SupplierDetailsHasListActivity.class);
                            String id = BaseUtils.toString(supplierList.get(position).get("id"));
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
                    break;
            }
        }
    };


    private void initScrollAdapterData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                adapter.setDatas(newsList);
            }
        }).start();
    }

    /**
     * 首页滚动新闻适配器
     */
    class MainScrollNewsAdapter implements LimitScrollerView.LimitScrollAdapter {
        private List<String> datas;

        public void setDatas(List<String> datas) {
            this.datas = datas;
            //API:2、开始滚动
            limitScroll.startScroll();
        }

        @Override
        public int getCount() {
            return datas == null ? 0 : datas.size();
        }

        @Override
        public View getView(int index) {
            View itemView = LayoutInflater.from(MainActivity.this).inflate(R.layout.limit_scroller_item, null, false);
            TextView iv_icon = (TextView) itemView.findViewById(R.id.iv_icon);
            TextView tv_text = (TextView) itemView.findViewById(R.id.tv_text);

            //绑定数据
            itemView.setTag(datas.get(index));
            //iv_icon.setImageResource(data.getIcon());
            tv_text.setText(datas.get(index));
            return itemView;
        }
    }

    //监听物理返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            BaseUtils.exitApp(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!BaseUtils.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
//                    接收并显示信息
                    showToast(showMsg.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
