package com.example.administrator.xxxq_grid;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.HttpRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

public class ContentServiceActivity extends BaseActivity {

    //二级标题
    private LinearLayout monthClick;
    private Map<String, Object> serviceMap;//服务内容数据对象
    private TextView monthShow, orderNum, show1, completeNum, show2;//服务内容文本
    private int year, month, day;
    //横坐标显示值
    public List<String> xValuesService;

    private ColumnChartData columnChartDataContentService1;    //柱状图数据
    private ColumnChartView columnChartViewContentService1;     //柱状图对象

    private ColumnChartData columnChartDataContentService2;    //柱状图数据
    private ColumnChartView columnChartViewContentService2;     //柱状图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_service);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        init();
        initData();
    }

    @Override
    protected void init() {
        monthShow = (TextView) findViewById(R.id.view_information_resource_content1_month);
        orderNum = (TextView) findViewById(R.id.view_information_resource_content1_order_num);
        show1 = (TextView) findViewById(R.id.view_information_resource_content1_month_show1);
        completeNum = (TextView) findViewById(R.id.view_information_resource_content1_complete_num);
        show2 = (TextView) findViewById(R.id.view_information_resource_content1_month_show2);
        monthShow.setText(BaseUtils.getCurrentYearAndMonth());
        columnChartViewContentService1 = (ColumnChartView) findViewById(R.id.view_information_resource_content1_column_chart1);
        columnChartViewContentService2 = (ColumnChartView) findViewById(R.id.view_information_resource_content1_column_chart2);
        show1.setText(BaseUtils.getCurrentMonth() + "月");
        show2.setText(BaseUtils.getCurrentMonth() + "月");
        monthClick = (LinearLayout) findViewById(R.id.view_information_resource_content1_month_click);
        monthClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(monthShow);
            }
        });
    }

    @Override
    protected void initData() {
        loadServiceData();
    }

    @Override
    protected void initTitle() {

    }

    /*******************************隐藏日期的日期选择器(已封装好)START*****************************************/
    private void showDatePicker(final View view) {
        //获取当前日期
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        //创建并显示DatePickerDialog
        DatePickerDialog dialog = new DatePickerDialog(ContentServiceActivity.this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                ((TextView) view).setText(year + "-" + (month + 1));
                if (view.getId() == R.id.view_information_resource_content1_month) {
                    show1.setText((month + 1) + "月");
                    show2.setText((month + 1) + "月");
                    loadServiceData();
                }
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

    private void loadServiceData() {
        Map<String, Object> map = new HashMap<String, Object>();
        String mm = monthShow.getText() + "-01";
        map.put("date", mm);
        new HttpRequest().post(ContentServiceActivity.this, "information/loadServiceColumnView", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                log(1, data);
                serviceMap = (Map<String, Object>) data;

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        orderNum.setText(BaseUtils.toString(serviceMap.get("orderNum")) + "笔");
                        completeNum.setText(BaseUtils.toString(serviceMap.get("orderComplete")) + "笔");
                    }
                });
                serviceColumnChartData1();
                serviceColumnChartData2();
            }
        });
    }

    private void serviceColumnChartData1() {
        /*========== 柱状图数据填充 ==========*/
        List<Column> columnList = new ArrayList<>(); //柱子列表
        List<AxisValue> axisValues = new ArrayList<>();//自定义横轴坐标值
        List<SubcolumnValue> subcolumnValueList;     //子柱列表（即一个柱子，因为一个柱子可分为多个子柱）

        List<Map<String, Object>> orderNumList = (List<Map<String, Object>>) serviceMap.get("orderNumList");
        xValuesService = new ArrayList<String>();
        for (int j = 0; j < orderNumList.size(); j++) {
            xValuesService.add(BaseUtils.toString(orderNumList.get(j).get("serveTypeName")));
        }

        for (int i = 0; i < xValuesService.size(); ++i) {
            subcolumnValueList = new ArrayList<>();
            subcolumnValueList.add(new SubcolumnValue(Float.parseFloat(BaseUtils.toString(orderNumList.get(i).get("counts"))), ChartUtils.pickColor()));
            Column column = new Column(subcolumnValueList);
            columnList.add(column);
            column.setHasLabels(true);//☆☆☆☆☆设置列标签
            //设置坐标值
            axisValues.add(new AxisValue(i).setLabel(xValuesService.get(i)));
        }
        columnChartDataContentService1 = new ColumnChartData(columnList);
         /*===== 坐标轴相关设置 =====*/
        Axis axisX = new Axis(axisValues);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("会员大类");    //设置横轴名称
        axisY.setName("订单笔数");    //设置竖轴名称
        columnChartDataContentService1.setAxisXBottom(axisX); //设置横轴
        columnChartDataContentService1.setAxisYLeft(axisY);   //设置竖轴
        //以上所有设置的数据、坐标配置都已存放到mColumnChartData中，接下来给mColumnChartView设置这些配置
        columnChartViewContentService1.setColumnChartData(columnChartDataContentService1);
    }

    private void serviceColumnChartData2() {
        /*========== 柱状图数据填充 ==========*/
        List<Column> columnList = new ArrayList<>(); //柱子列表
        List<AxisValue> axisValues = new ArrayList<>();//自定义横轴坐标值
        List<SubcolumnValue> subcolumnValueList;     //子柱列表（即一个柱子，因为一个柱子可分为多个子柱）
        List<Map<String, Object>> orderNumList = (List<Map<String, Object>>) serviceMap.get("orderCompleteList");
        for (int i = 0; i < xValuesService.size(); ++i) {
            subcolumnValueList = new ArrayList<>();
            subcolumnValueList.add(new SubcolumnValue(Float.parseFloat(BaseUtils.toString(orderNumList.get(i).get("counts"))), ChartUtils.pickColor()));
            Column column = new Column(subcolumnValueList);
            columnList.add(column);
            column.setHasLabels(true);//☆☆☆☆☆设置列标签
            //设置坐标值
            axisValues.add(new AxisValue(i).setLabel(xValuesService.get(i)));
        }
        columnChartDataContentService2 = new ColumnChartData(columnList);
         /*===== 坐标轴相关设置 =====*/
        Axis axisX = new Axis(axisValues);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("会员大类");    //设置横轴名称
        axisY.setName("完成量");    //设置竖轴名称
        columnChartDataContentService2.setAxisXBottom(axisX); //设置横轴
        columnChartDataContentService2.setAxisYLeft(axisY);   //设置竖轴
        //以上所有设置的数据、坐标配置都已存放到mColumnChartData中，接下来给mColumnChartView设置这些配置
        columnChartViewContentService2.setColumnChartData(columnChartDataContentService2);
    }

}
