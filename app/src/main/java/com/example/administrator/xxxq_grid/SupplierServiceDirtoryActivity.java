package com.example.administrator.xxxq_grid;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.HeaderBar;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class SupplierServiceDirtoryActivity extends BaseActivity {
    private LineChartView lineChart;
    String[] weeks = {"周一","周二","周三","周四","周五","周六","周日"};//X轴的标注
    int[] weather = {9,7,6,7,8,6,8};//图表的数据

    private List<PointValue> mPointValues = new ArrayList<PointValue>();
    private List<AxisValue> mAxisValues = new ArrayList<AxisValue>();

    //柱状图
     /*========== 数据相关 ==========*/
    private ColumnChartView mColumnChartView;
    private ColumnChartData mColumnChartData;               //柱状图数据
    public final String[] xValues = new String[]{"语文", "数学", "英语", "音乐", "科学", "体育"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_service_charts_layout);
        lineChart = (LineChartView)findViewById(R.id.line_chart);

        getAxisLables();//获取x轴的标注
        getAxisPoints();//获取坐标点
        initLineChart();//初始化

        initTitle();
        init();
        initData();


        //柱状图的添加数据
        initView();

    }

    /**
     * 初始化LineChart的一些设置
     */
    private void initLineChart(){
        Line line = new Line(mPointValues).setColor(Color.GREEN).setCubic(false);  //折线的颜色
        List<Line> lines = new ArrayList<Line>();
        line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.SQUARE）
        line.setCubic(true);//曲线是否平滑
        line.setFilled(true);//是否填充曲线的面积
//      line.setHasLabels(true);//曲线的数据坐标是否加上备注
        line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
        line.setHasLines(true);//是否用直线显示。如果为false 则没有曲线只有点显示
        line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示
        lines.add(line);
        LineChartData data = new LineChartData();
        data.setLines(lines);

        //坐标轴
        Axis axisX = new Axis(); //X轴
        axisX.setHasTiltedLabels(true);
        axisX.setTextColor(Color.WHITE);  //设置字体颜色
        axisX.setName("未来几天的天气");  //表格名称
        axisX.setTextSize(7);//设置字体大小
        axisX.setMaxLabelChars(7);  //最多几个X轴坐标
        axisX.setValues(mAxisValues);  //填充X轴的坐标名称
        data.setAxisXBottom(axisX); //x 轴在底部
//      data.setAxisXTop(axisX);  //x 轴在顶部

        Axis axisY = new Axis();  //Y轴
        axisY.setMaxLabelChars(7); //默认是3，只能看最后三个数字
        axisY.setName("温度");//y轴标注
        axisY.setTextSize(7);//设置字体大小

        data.setAxisYLeft(axisY);  //Y轴设置在左边
//      data.setAxisYRight(axisY);  //y轴设置在右边

        //设置行为属性，支持缩放、滑动以及平移
        lineChart.setInteractive(true);
        lineChart.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);
        lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        lineChart.setLineChartData(data);
        lineChart.setVisibility(View.VISIBLE);
    }


    /**
     * X 轴的显示
     */
    private void getAxisLables(){
        for (int i = 0; i < weeks.length; i++) {
            mAxisValues.add(new AxisValue(i).setLabel(weeks[i]));
        }
    }

    /**
     * 图表的每个点的显示
     */
    private void getAxisPoints(){
        for (int i = 0; i < weather.length; i++) {
            mPointValues.add(new PointValue(i, weather[i]));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initTitle() {
        BaseUtils.initHeaderBar(this);
        HeaderBar headerBar = (HeaderBar) this.findViewById(R.id.headerBar);
        headerBar.initViewsVisible(true, true, false, false);
        headerBar.setAppTitle("服务资源库");
        headerBar.setOnLeftButtonClickListener(new HeaderBar.OnLeftButtonClickListener() {
            @Override
            public void onLeftButtonClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {
        mColumnChartView = (ColumnChartView) findViewById(R.id.mColumnChartView);
        mColumnChartView.setOnValueTouchListener(new ValueTouchListener());

        /*========== 柱状图数据填充 ==========*/
        List<Column> columnList = new ArrayList<>(); //柱子列表
        List<SubcolumnValue> subcolumnValueList;     //子柱列表（即一个柱子，因为一个柱子可分为多个子柱）
        List<AxisValue> axisValues = new ArrayList<>();
        for (int i = 0; i < xValues.length; ++i) {
            subcolumnValueList = new ArrayList<>();
            subcolumnValueList.add(new SubcolumnValue((float) Math.random() * 100f, ChartUtils.pickColor()));

            Column column = new Column(subcolumnValueList);
            column.setHasLabels(true);                    //设置列标签
//            column.setHasLabelsOnlyForSelected(true);       //只有当点击时才显示列标签

            columnList.add(column);

            //设置坐标值
            axisValues.add(new AxisValue(i).setLabel(xValues[i]));
        }

        mColumnChartData = new ColumnChartData(columnList);               //设置数据


        /*===== 坐标轴相关设置 =====*/
        Axis axisX = new Axis(axisValues); //将自定义x轴显示值传入构造函数
        Axis axisY = new Axis().setHasLines(true); //setHasLines是设置线条
        axisX.setName("考试科目");    //设置横轴名称
        axisY.setName("成绩");    //设置竖轴名称
        mColumnChartData.setAxisXBottom(axisX); //设置横轴
        mColumnChartData.setAxisYLeft(axisY);   //设置竖轴

        //以上所有设置的数据、坐标配置都已存放到mColumnChartData中，接下来给mColumnChartView设置这些配置
        mColumnChartView.setColumnChartData(mColumnChartData);


        /*===== 设置竖轴最大值 =====*/
        //法一：
        Viewport v = mColumnChartView.getMaximumViewport();
        v.top = 103;
        mColumnChartView.setCurrentViewport(v);
        /*法二：
        Viewport v = mColumnChartView.getCurrentViewport();
        v.top = 100;
        mColumnChartView.setMaximumViewport(v);
        mColumnChartView.setCurrentViewport(v);*/
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            Toast.makeText(SupplierServiceDirtoryActivity.this, xValues[columnIndex]+"成绩 : " +
                    (int)value.getValue(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub

        }

    }

}
