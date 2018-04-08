package com.example.administrator.xxxq_grid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.example.administrator.adapter.FieldExplorationGridViewAdapter;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.DialogUtils;
import com.example.administrator.common.HeaderBar;
import com.example.administrator.common.HttpRequest;
import com.example.administrator.common.UploadUtil;
import com.insplatform.core.utils.JsonUtil;
import com.insplatform.core.utils.TextUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FieldExplorationActivity extends BaseActivity {
    private final int MAX_IMAGE = 9;
    private TextView date, time;
    private EditText content;
    private Spinner supplier_spinner;
    private List<String> supplier_list;
    private List<Map<String, Object>> dataList;
    private ArrayAdapter<String> supplier_adapter;
    private ImageView picture_chooser;
    private Button submit;
    private FieldExplorationGridViewAdapter fieldExplorationGridViewAdapter;
    private List<String> images, photos;
    private GridView picture;
    private String selectShowName = "";
    private RatingBar rank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_exploration);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            if (TextUtil.isNotEmpty(bundle.getString("id"))) {
                selectShowName = bundle.getString("id");
            }
        }

        initTitle();
        init();
        initData();
    }

    @Override
    protected void init() {
        date = (TextView) findViewById(R.id.activity_field_exploration_date);
        date.setOnClickListener(c);
        time = (TextView) findViewById(R.id.activity_field_exploration_time);
        time.setOnClickListener(c);
        picture_chooser = (ImageView) findViewById(R.id.activity_field_exploration_picture_chooser);
        picture_chooser.setOnClickListener(c);
        submit = (Button) findViewById(R.id.activity_field_exploration_submit);
        submit.setOnClickListener(c);
        images = new ArrayList<String>();
        picture = (GridView) findViewById(R.id.activity_field_exploration_grid_view);
        picture.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                DialogUtils.showAlertDialog(FieldExplorationActivity.this, "确定删除该图吗?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        images.remove(position);
                        if (images.size() == MAX_IMAGE) {
                            picture_chooser.setVisibility(View.GONE);
                        } else {
                            picture_chooser.setVisibility(View.VISIBLE);
                        }
                        handler.sendEmptyMessage(1);
                    }
                });
                return false;
            }
        });
        supplier_list = new ArrayList<String>();
        photos = new ArrayList<String>();
        dataList = new ArrayList<Map<String, Object>>();
        rank = findViewById(R.id.activity_field_exploration_rank);
        content = findViewById(R.id.activity_field_exploration_content);
    }

    @Override
    protected void initData() {
        loadSupplierList();
    }

    private void loadSupplierList() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("limit", 10);
        new HttpRequest().post(FieldExplorationActivity.this, "mainActivity/loadSupplierList", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                Map<String, Object> map = (Map<String, Object>) data;
                List<Map<String, Object>> ls = (List<Map<String, Object>>) map.get("data");
                for (int i = 0; i < ls.size(); i++) {
                    dataList.add((Map<String, Object>) ls.get(i));
                    supplier_list.add(BaseUtils.toString(ls.get(i).get("name")));
                }
                handler.sendEmptyMessage(2);
            }
        });
    }

    private void setSupplierList() {
        supplier_spinner = (Spinner) findViewById(R.id.activity_field_exploration_spinner);

        /*新建适配器*/
        supplier_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, supplier_list);

        /*adapter设置一个下拉列表样式，参数为系统子布局*/
        supplier_adapter.setDropDownViewResource(R.layout.view_simple_spinner_item);

        /*spinner加载适配器*/
        supplier_spinner.setAdapter(supplier_adapter);

        /*spinner回显*/
        if (TextUtil.isNotEmpty(selectShowName)) {
            for (int i = 0; i < dataList.size(); i++) {
                if (BaseUtils.toString(dataList.get(i).get("id")).equals(selectShowName)) {
                    supplier_spinner.setSelection(i);
                    selectShowName = dataList.get(i).get("name").toString();
                }
            }
        }

        /*spinner的监听器*/
        supplier_spinner.setOnItemSelectedListener(l);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0x111 && data != null) {
            //获取选择器返回的数据
            List<String> list = new ArrayList<String>();
            list = data.getStringArrayListExtra(
                    ImageSelectorUtils.SELECT_RESULT);
            images.addAll(list);
            if (images.size() == MAX_IMAGE) {
                picture_chooser.setVisibility(View.GONE);
            } else {
                picture_chooser.setVisibility(View.VISIBLE);
            }
            handler.sendEmptyMessage(1);
        }
    }

    /**
     * 消息处理,通知主线程更新ui
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    /*首先获取GrivView组件，然后创建BaseAdapter类的对象，并重写其中的
                     * getView()、getItemId()、getItem()和getConut()方法，其中最主要的是重写
                     * getView()方法来设置要显示的图片，最后将BaseAdapter适配器与GridView关联*/
                    fieldExplorationGridViewAdapter = new FieldExplorationGridViewAdapter(FieldExplorationActivity.this, images);
                    GridView gridview = (GridView) findViewById(R.id.activity_field_exploration_grid_view);//获取GridView组件
                    gridview.setAdapter(fieldExplorationGridViewAdapter);//将适配器与GridView关联
                    break;
                case 2:
                    setSupplierList();
                    break;
                case 3:
                    successfulLoading();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            finish();
                        }
                    }, 2000);
                    break;
                case 4:
                    submit();
                    break;
            }
        }
    };

    View.OnClickListener c = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            /*
            //单选
            //ImageSelectorUtils.openPhoto(FieldExplorationActivity.this, 0x111, true, 0);

            //限数量的多选(比喻最多9张)
            ImageSelectorUtils.openPhoto(FieldExplorationActivity.this, REQUEST_CODE, false, 9);

            //不限数量的多选
            ImageSelectorUtils.openPhoto(FieldExplorationActivity.this, REQUEST_CODE);
            //或者
            ImageSelectorUtils.openPhoto(FieldExplorationActivity.this, REQUEST_CODE, false, 0);

            //单选并剪裁
            ImageSelectorUtils.openPhotoAndClip(FieldExplorationActivity.this, REQUEST_CODE);*/

            switch (v.getId()) {
                case R.id.activity_field_exploration_date:
                    BaseUtils.showDatePickerDialog(FieldExplorationActivity.this, 0, date, Calendar.getInstance());
                    break;
                case R.id.activity_field_exploration_time:
                    BaseUtils.showTimePickerDialog(FieldExplorationActivity.this, 0, time, Calendar.getInstance());
                    break;
                case R.id.activity_field_exploration_picture_chooser:
                    ImageSelectorUtils.openPhoto(FieldExplorationActivity.this, 0x111, false, (MAX_IMAGE - images.size()));
                    break;
                case R.id.activity_field_exploration_submit:
                    verificationData();
                    break;
            }
        }
    };

    public void verificationData() {

        if (TextUtil.isEmpty(BaseUtils.toString(date.getText()))) {
            showToast("请选择侦查日期");
            return;
        }
        if (TextUtil.isEmpty(BaseUtils.toString(time.getText()))) {
            showToast("请选择侦查时间");
            return;
        }
        if (TextUtil.isEmpty(BaseUtils.toString(rank.getRating()))) {
            showToast("请选择评价等级");
            return;
        }
        if (TextUtil.isEmpty(BaseUtils.toString(content.getText()))) {
            showToast("请输入侦查内容");
            return;
        }
        if (images.size() == 0) {
            showToast("请选择勘察图片");
            return;
        }
        //显示loading
        showLoading();
        //开始上传图片
        uploadPhotos();
    }

    private void uploadPhotos() {
        photos.clear();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < images.size(); i++) {
                        String file = UploadUtil.uploadFile(new File(images.get(i)), new HttpRequest().getUrl() + "fieldExploration/uploadFile?deviceId=" + BaseUtils.getDeviceIMEI(FieldExplorationActivity.this) + "&appVersion=" + BaseUtils.getVersionName(FieldExplorationActivity.this));
                        Map<String, Object> map = JsonUtil.toObject(file, Map.class);
                        Map<String, Object> map1 = (Map<String, Object>) map.get("data");
                        photos.add(map1.get("urlPath").toString());
                        if (photos.size() == images.size()) {
                            handler.sendEmptyMessage(4);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void submit() {
        Map<String, Object> map = new HashMap<String, Object>();
        //获取select的id
        for (int i = 0; i < dataList.size(); i++) {
            if (selectShowName.equals(dataList.get(i).get("name"))) {
                //供应商id
                map.put("bsSupplier", dataList.get(i).get("id"));
            }
        }
        //勘察时间
        map.put("surveyTime", date.getText() + " " + time.getText() + ":00");
        //评价
        String rating = rank.getRating() + "";
        map.put("rank", Integer.parseInt(rating.substring(0, 1)));
        //侦查内容
        map.put("content", content.getText().toString());
        //勘察照片
        String tt = "";
        for (int i = 0; i < photos.size(); i++) {
            tt += photos.get(i) + ",";
        }
        map.put("photos", tt);
        new HttpRequest().post(FieldExplorationActivity.this, "fieldExploration/addFieldExploration", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                if (Boolean.valueOf(BaseUtils.toString(data))) {
                    handler.sendEmptyMessage(3);
                }
            }
        });
    }

    //适配器点击事件
    AdapterView.OnItemSelectedListener l = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            selectShowName = supplier_adapter.getItem(position);   //获取选中的那一项
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    protected void initTitle() {
        BaseUtils.initHeaderBar(this);
        HeaderBar headerBar = (HeaderBar) this.findViewById(R.id.headerBar);
        headerBar.initViewsVisible(true, true, false, false);
        headerBar.setAppTitle("现场勘察");
        headerBar.setOnLeftButtonClickListener(new HeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }
}
