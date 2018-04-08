package com.example.administrator.xxxq_grid;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.HeaderBar;
import com.example.administrator.common.HttpRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class InformationResourceActivity extends BaseActivity implements View.OnClickListener {

    //二级标题
    private LinearLayout llServiceLayoutTitle, llPersonLayoutTitle, llSupplierLayoutTitle, serviceMonthClick, memberMonthClick, supplierMonthClick;
    //标题文字
    private TextView tvServiceTitle, tvPersonTitle, tvSupplierTitle;
    //标题状态
    private TextView tvServiceBg, tvPersonBg, tvSupplierBg;
    //选中显示内容
    private LinearLayout llServiceLayoutContext, llPersonLayoutContext, llSupplierLayoutContext;
    //横坐标显示值
    public List<String> xValuesService, xValuesMember, xValuesSupplier;

    private Map<String, Object> serviceMap, memberMap, supplierMap;//数据对象
    private TextView serviceMonthShow, orderNum, show1, completeNum, show2;//服务内容文本
    private TextView memberMonthShow, show3, salesNum;//会员信息文本
    private TextView supplierMonthShow, show4, show5, salesNum1, orderNum1;//供应商信息文本
    private int year, month, day;


    /*========== 数据相关 ==========*/
    private ColumnChartData columnChartDataContentService1;    //柱状图数据
    private ColumnChartView columnChartViewContentService1;     //柱状图对象

    private ColumnChartData columnChartDataContentService2;    //柱状图数据
    private ColumnChartView columnChartViewContentService2;     //柱状图对象

    private ColumnChartData columnChartDataContentMember;    //柱状图数据
    private ColumnChartView columnChartViewContentMember;     //柱状图对象

    private LineChartData lineChartDataSupplier1;           //折线图数据
    private LineChartView lineChartViewSupplier1;           //折线图对象

    private LineChartData lineChartDataSupplier2;           //折线图数据
    private LineChartView lineChartViewSupplier2;           //折线图对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_resource);

        initTitle();
        init();
        initData();

    }

    @Override
    protected void init() {
        /******************************公共信息START***********************************/
        llServiceLayoutTitle = (LinearLayout) findViewById(R.id.llServiceLayoutTitle);
        llPersonLayoutTitle = (LinearLayout) findViewById(R.id.llPersonLayoutTitle);
        llSupplierLayoutTitle = (LinearLayout) findViewById(R.id.llSupplierLayoutTitle);
        tvServiceTitle = (TextView) findViewById(R.id.tvServiceText);
        tvPersonTitle = (TextView) findViewById(R.id.tvPersonText);
        tvSupplierTitle = (TextView) findViewById(R.id.tvSupplierText);
        tvServiceBg = (TextView) findViewById(R.id.tvServiceBackground);
        tvPersonBg = (TextView) findViewById(R.id.tvPersonBackground);
        tvSupplierBg = (TextView) findViewById(R.id.tvSupplierBackground);
        llServiceLayoutContext = (LinearLayout) findViewById(R.id.llServiceLayoutContext);
        llPersonLayoutContext = (LinearLayout) findViewById(R.id.llPersonLayoutContext);
        llSupplierLayoutContext = (LinearLayout) findViewById(R.id.llSupplierLayoutContext);

        llServiceLayoutTitle.setOnClickListener(this);
        llPersonLayoutTitle.setOnClickListener(this);
        llSupplierLayoutTitle.setOnClickListener(this);
        /******************************公共信息END*************************************/

        /*****************************服务内容变量定义START*****************************/
        //布局嵌入信息
        columnChartViewContentService1 = (ColumnChartView) findViewById(R.id.view_information_resource_content1_column_chart1);
        columnChartViewContentService2 = (ColumnChartView) findViewById(R.id.view_information_resource_content1_column_chart2);
        serviceMonthShow = (TextView) findViewById(R.id.view_information_resource_content1_month);
        orderNum = (TextView) findViewById(R.id.view_information_resource_content1_order_num);
        completeNum = (TextView) findViewById(R.id.view_information_resource_content1_complete_num);
        show1 = (TextView) findViewById(R.id.view_information_resource_content1_month_show1);
        show2 = (TextView) findViewById(R.id.view_information_resource_content1_month_show2);
        show1.setText(BaseUtils.getCurrentMonth() + "月");
        show2.setText(BaseUtils.getCurrentMonth() + "月");
        serviceMonthShow.setText(BaseUtils.getCurrentYearAndMonth());
        serviceMonthClick = (LinearLayout) findViewById(R.id.view_information_resource_content1_month_click);
        serviceMonthClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(serviceMonthShow);
            }
        });
        /*****************************服务内容变量定义END*****************************/

        /*****************************会员信息变量定义START**************************/
        columnChartViewContentMember = (ColumnChartView) findViewById(R.id.view_information_resource_content2_column_chart);
        memberMonthShow = (TextView) findViewById(R.id.view_information_resource_content2_month);
        show3 = (TextView) findViewById(R.id.view_information_resource_content2_month_show);
        show3.setText(BaseUtils.getCurrentMonth() + "月");
        salesNum = (TextView) findViewById(R.id.view_information_resource_content2_sales_num);
        memberMonthShow.setText(BaseUtils.getCurrentYearAndMonth());
        memberMonthClick = (LinearLayout) findViewById(R.id.view_information_resource_content2_month_click);
        memberMonthClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(memberMonthShow);
            }
        });
        /*****************************会员信息变量定义END**************************/

        /*****************************供应商信息变量定义START**********************/
        //布局嵌入信息
        lineChartViewSupplier1 = (LineChartView) findViewById(R.id.view_information_resource_content3_line_chart1);
        lineChartViewSupplier2 = (LineChartView) findViewById(R.id.view_information_resource_content3_line_chart2);
        supplierMonthShow = (TextView) findViewById(R.id.view_information_resource_content3_month);
        salesNum1 = (TextView) findViewById(R.id.view_information_resource_content3_sale_num);
        orderNum1 = (TextView) findViewById(R.id.view_information_resource_content3_order_num);
        show4 = (TextView) findViewById(R.id.view_information_resource_content3_month_show1);
        show5 = (TextView) findViewById(R.id.view_information_resource_content3_month_show2);
        show4.setText(BaseUtils.getCurrentMonth() + "月");
        show5.setText(BaseUtils.getCurrentMonth() + "月");
        supplierMonthShow.setText(BaseUtils.getCurrentYearAndMonth());
        supplierMonthClick = (LinearLayout) findViewById(R.id.view_information_resource_content3_month_click);
        supplierMonthClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker(supplierMonthShow);
            }
        });
        /*****************************会员信息变量定义END**************************/
    }

    @Override
    protected void initData() {
        //获取服务内容
        loadServiceData();
        //获取会员信息
        loadMemberData();
        //获取供应商信息
        loadSupplierData();
    }

    /*******************************隐藏日期的日期选择器(已封装好)START*****************************************/
    private void showDatePicker(final View view) {
        //获取当前日期
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        //创建并显示DatePickerDialog
        DatePickerDialog dialog = new DatePickerDialog(InformationResourceActivity.this, AlertDialog.THEME_HOLO_LIGHT, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                ((TextView) view).setText(year + "-" + (month + 1));
                if (view.getId() == R.id.view_information_resource_content1_month) {
                    show1.setText((month + 1) + "月");
                    show2.setText((month + 1) + "月");
                    loadServiceData();
                }
                if (view.getId() == R.id.view_information_resource_content2_month) {
                    show3.setText((month + 1) + "月");
                    loadMemberData();
                }
                if (view.getId() == R.id.view_information_resource_content3_month) {
                    show4.setText((month + 1) + "月");
                    show5.setText((month + 1) + "月");
                    loadSupplierData();
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
        String mm = serviceMonthShow.getText() + "-01";
        map.put("date", mm);
        new HttpRequest().post(InformationResourceActivity.this, "information/loadServiceColumnView", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
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

    private void loadMemberData() {
        Map<String, Object> map = new HashMap<String, Object>();
        String mm = memberMonthShow.getText() + "-01";
        map.put("date", mm);
        new HttpRequest().post(InformationResourceActivity.this, "information/loadMemberColumnView", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                memberMap = (Map<String, Object>) data;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        salesNum.setText(BaseUtils.toString(memberMap.get("totalMoney")) + "元");
                    }
                });
                memberColumnChartData();
            }
        });
    }


    private void loadSupplierData() {
        Map<String, Object> map = new HashMap<String, Object>();
        String mm = supplierMonthShow.getText() + "-01";
        map.put("date", mm);
        new HttpRequest().post(InformationResourceActivity.this, "information/loadSupplierColumnView", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                supplierMap = (Map<String, Object>) data;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        salesNum1.setText(BaseUtils.toString(supplierMap.get("orderMoney")) + "元");
                        orderNum1.setText(BaseUtils.toString(supplierMap.get("orderNum")) + "笔");
                    }
                });
                supplierLineChartData1();
                supplierLineChartData2();
            }
        });
    }

    private void supplierLineChartData1() {
        //折线图的点值
        List<PointValue> mPointValues = new ArrayList<PointValue>();
        //折线图X轴坐标
        List<AxisValue> mAxisValues = new ArrayList<AxisValue>();

        List<Map<String, Object>> orderMoneyList = (List<Map<String, Object>>) supplierMap.get("orderMoneyList");
        for (int i = 0; i < orderMoneyList.size(); i++) {
            mPointValues.add(new PointValue(i, Float.valueOf(BaseUtils.toString(orderMoneyList.get(i).get("money")))));
            mAxisValues.add(new AxisValue(i).setLabel(BaseUtils.toString(orderMoneyList.get(i).get("dateNum"))));
        }

        Line line = new Line(mPointValues).setColor(Color.parseColor("#FF2EC39A")).setCubic(false);  //折线的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setCubic(true);//曲线是否平滑
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(false);//是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);
        axisX.setName("日期");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setValues(mAxisValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部

        Axis axisY = new Axis();  //Y轴
        axisY.setName("销售金额");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //设置行为属性，支持缩放、滑动以及平移
        lineChartViewSupplier1.setInteractive(true);
        lineChartViewSupplier1.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        lineChartViewSupplier1.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartViewSupplier1.setLineChartData(data);
        lineChartViewSupplier1.setVisibility(View.VISIBLE);
    }

    private void supplierLineChartData2() {
        //折线图的点值
        List<PointValue> mPointValues = new ArrayList<PointValue>();
        //折线图X轴坐标
        List<AxisValue> mAxisValues = new ArrayList<AxisValue>();

        List<Map<String, Object>> orderMoneyList = (List<Map<String, Object>>) supplierMap.get("orderNumList");
        for (int i = 0; i < orderMoneyList.size(); i++) {
            mPointValues.add(new PointValue(i, Float.valueOf(BaseUtils.toString(orderMoneyList.get(i).get("counts")))));
            mAxisValues.add(new AxisValue(i).setLabel(BaseUtils.toString(orderMoneyList.get(i).get("dateNum"))));
        }

        Line line = new Line(mPointValues).setColor(Color.parseColor("#FF2EC39A")).setCubic(false);  //折线的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setCubic(true);//曲线是否平滑
        line.setFilled(false);//是否填充曲线的面积
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(false);//是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);
        axisX.setName("日期");  //表格名称
        axisX.setTextSize(10);//设置字体大小
        axisX.setValues(mAxisValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部

        Axis axisY = new Axis();  //Y轴
        axisY.setName("订单笔数");//y轴标注
        axisY.setTextSize(10);//设置字体大小
        data.setAxisYLeft(axisY);  //Y轴设置在左边
        //设置行为属性，支持缩放、滑动以及平移
        lineChartViewSupplier2.setInteractive(true);
        lineChartViewSupplier2.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        lineChartViewSupplier2.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChartViewSupplier2.setLineChartData(data);
        lineChartViewSupplier2.setVisibility(View.VISIBLE);
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

    private void memberColumnChartData() {
        /*========== 柱状图数据填充 ==========*/
        List<Column> columnList = new ArrayList<>(); //柱子列表
        List<AxisValue> axisValues = new ArrayList<>();//自定义横轴坐标值
        List<SubcolumnValue> subcolumnValueList;     //子柱列表（即一个柱子，因为一个柱子可分为多个子柱）

        List<Map<String, Object>> totalMoneyList = (List<Map<String, Object>>) memberMap.get("totalMoneyList");
        xValuesMember = new ArrayList<String>();
        for (int j = 0; j < totalMoneyList.size(); j++) {
            xValuesMember.add(BaseUtils.toString(totalMoneyList.get(j).get("rank")));
        }

        for (int i = 0; i < xValuesMember.size(); ++i) {
            subcolumnValueList = new ArrayList<>();
            subcolumnValueList.add(new SubcolumnValue(Float.parseFloat(BaseUtils.toString(totalMoneyList.get(i).get("money"))), ChartUtils.pickColor()));
            Column column = new Column(subcolumnValueList);
            columnList.add(column);
            column.setHasLabels(true);//☆☆☆☆☆设置列标签
            //设置坐标值
            axisValues.add(new AxisValue(i).setLabel(xValuesMember.get(i)));
        }
        columnChartDataContentMember = new ColumnChartData(columnList);
         /*===== 坐标轴相关设置 =====*/
        Axis axisX = new Axis(axisValues);
        Axis axisY = new Axis().setHasLines(true);
        axisX.setName("会员类型");    //设置横轴名称
        axisY.setName("消费金额");    //设置竖轴名称
        columnChartDataContentMember.setAxisXBottom(axisX); //设置横轴
        columnChartDataContentMember.setAxisYLeft(axisY);   //设置竖轴
        //以上所有设置的数据、坐标配置都已存放到mColumnChartData中，接下来给mColumnChartView设置这些配置
        columnChartViewContentMember.setColumnChartData(columnChartDataContentMember);
    }

    @Override
    protected void initTitle() {
        BaseUtils.initHeaderBar(this);
        HeaderBar headerBar = (HeaderBar) this.findViewById(R.id.headerBar);
        headerBar.initViewsVisible(true, true, false, false);
        headerBar.setAppTitle("信息资源库");
        headerBar.setOnLeftButtonClickListener(new HeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llServiceLayoutTitle:
                tvServiceBg.setBackgroundColor(getResources().getColor(R.color.white));
                tvPersonBg.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                tvSupplierBg.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                llServiceLayoutContext.setVisibility(View.VISIBLE);
                llPersonLayoutContext.setVisibility(View.GONE);
                llSupplierLayoutContext.setVisibility(View.GONE);
                break;
            case R.id.llPersonLayoutTitle:
                tvServiceBg.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                tvPersonBg.setBackgroundColor(getResources().getColor(R.color.white));
                tvSupplierBg.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                llServiceLayoutContext.setVisibility(View.GONE);
                llPersonLayoutContext.setVisibility(View.VISIBLE);
                llSupplierLayoutContext.setVisibility(View.GONE);
                break;
            case R.id.llSupplierLayoutTitle:
                tvServiceBg.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                tvPersonBg.setBackgroundColor(getResources().getColor(R.color.colorTransparent));
                tvSupplierBg.setBackgroundColor(getResources().getColor(R.color.white));
                llServiceLayoutContext.setVisibility(View.GONE);
                llPersonLayoutContext.setVisibility(View.GONE);
                llSupplierLayoutContext.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void informationOrderList(View view) {
        Intent intent = new Intent(this, InformationOrderListActivity.class);
        Bundle bundle = new Bundle();
        String mm = serviceMonthShow.getText() + "-01";
        bundle.putString("date", mm);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void informationMemberList(View view) {
        Intent intent = new Intent(this, InformationMemberListActivity.class);
        Bundle bundle = new Bundle();
        String mm = memberMonthShow.getText() + "-01";
        bundle.putString("date", mm);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public void informationSupplierList(View view) {
        Intent intent = new Intent(this, InformationSupplierListActivity.class);
        Bundle bundle = new Bundle();
        String mm = supplierMonthShow.getText() + "-01";
        bundle.putString("date", mm);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
