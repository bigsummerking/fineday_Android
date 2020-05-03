package com.example.finedayapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.finedayapp.DailyCount.CostBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class ChartsActivity extends Activity {

    private LineChartView mchart;
    private Map<String, Integer> table = new TreeMap<>();
    private LineChartData mData;
    private List<CostBean> allDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.count_chart);
        mchart = (LineChartView) findViewById(R.id.chart);
        allDate = (List<CostBean>) getIntent().getSerializableExtra("cost_list");
        generateValues(allDate);
        generateData();

    }

    private void generateData() {
        List<Line> lines = new ArrayList<>();
        mData=new LineChartData(lines);
        List<PointValue> mPointValues  = new ArrayList<>();
        List<AxisValue> mAxisXValues  = new ArrayList<>();
        int indexX=0;
        int indexY=0;
        for (Integer value : table.values()) {
            mPointValues.add(new PointValue(indexX,value));
            indexX++;
        }
        Iterator<String> iter = table.keySet().iterator();
        while (iter.hasNext()) {
            mAxisXValues.add(new AxisValue(indexY,iter.next().toCharArray()));
            indexY++;
        }
        Axis axisX = new Axis(); //X轴
        axisX.setValues(mAxisXValues);
        axisX.setName("消费日期");  //表格名称
        axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
        axisX.setMaxLabelChars(7); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数6<=x<=mAxisXValues.length
        axisX.setHasLines(true); //x 轴分割线  每个x轴上 面有个虚线 与x轴垂直
        mData.setAxisXBottom(axisX);
        Axis axisY = new Axis();
        axisY.setName("消费金额");//y轴标注
        mData.setAxisYLeft(axisY);
        Line line = new Line(mPointValues);
        line.setColor(ChartUtils.COLORS[0]);
        //折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
        line.setShape(ValueShape.CIRCLE);
        line.setCubic(true);//曲线是否平滑，即是曲线还是折线
        line.setPointColor(ChartUtils.COLORS[1]);
        line.setHasPoints(false);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
        line.setHasLabels(true);//曲线的数据坐标是否加上备注
        lines.add(line);
        mchart.setZoomEnabled(true);
        mchart.setInteractive(true);
        mchart.setZoomType(ZoomType.HORIZONTAL);
        mchart.setMaxZoom((float) 2);//最大方法比例
        mchart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
        mchart.setLineChartData(mData);

    }

    private void generateValues(List<CostBean> allDate) {
        if (allDate != null) {
            for (int i = 0; i < allDate.size(); i++) {
                CostBean costBean = allDate.get(i);
                String costDate = costBean.getCostDate();
                int costMoney = (int) Double.parseDouble(costBean.getCostMoney());
                if (!table.containsKey(costDate)) {
                    table.put(costDate, costMoney);
                } else {
                    int originMoney = table.get(costDate);
                    table.put(costDate, originMoney + costMoney);
                }
            }
        }
    }
}
