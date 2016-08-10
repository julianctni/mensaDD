package com.pasta.mensadd.fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pasta.mensadd.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class ImprintFragment extends Fragment {

    float maxBalanceValue;
    float maxTransactionValue;
    public ImprintFragment() {}

    public static ImprintFragment newInstance() {
        return new ImprintFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_imprint, container, false);
        setUpBalanceChart((LineChartView) v.findViewById(R.id.lineChart));
        setUpTransactionsChart((ColumnChartView) v.findViewById(R.id.columnChart));
        return v;
    }

    public void setUpBalanceChart(LineChartView chart){
        List<PointValue> values = new ArrayList<PointValue>();
        values.add(new PointValue(0, 2));
        values.add(new PointValue(1, 4));
        values.add(new PointValue(2, 3));
        values.add(new PointValue(3, 4));

        for (PointValue pv : values) {
            if (pv.getY() > maxBalanceValue)
                maxBalanceValue = pv.getY();
        }

        Line line = new Line(values).setColor(Color.parseColor("#bbcc3c51")).setCubic(false).setFilled(true).setHasPoints(false);
        List<Line> lines = new ArrayList<Line>();
        lines.add(line);

        LineChartData data = new LineChartData();
        Axis axisY = new Axis().setHasLines(true).setMaxLabelChars(4).setFormatter(new SimpleAxisValueFormatter().setAppendedText("€".toCharArray()));

        data.setAxisYLeft(axisY);
        data.setLines(lines);
        data.setBaseValue(Float.NEGATIVE_INFINITY);

        chart.setLineChartData(data);

        Viewport viewport = new Viewport(chart.getMaximumViewport());
        viewport.bottom = 0;
        viewport.top = (int)(maxBalanceValue*1.4);
        chart.setMaximumViewport(viewport);
        chart.setCurrentViewport(viewport);
    }

    public void setUpTransactionsChart(ColumnChartView chart){


        int numSubcolumns = 1;
        int numColumns = 8;
        List<Column> columns = new ArrayList<Column>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<SubcolumnValue>();
            for (int j = 0; j < numSubcolumns; ++j) {
                values.add(new SubcolumnValue((float) Math.random() * 50f + 5, ChartUtils.pickColor()));
            }

            Column column = new Column(values);
            columns.add(column);
        }

        ColumnChartData data = new ColumnChartData(columns);

        Axis axisX = new Axis();
        Axis axisY = new Axis().setHasLines(true).setMaxLabelChars(4).setFormatter(new SimpleAxisValueFormatter().setAppendedText("€".toCharArray()));

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        chart.setColumnChartData(data);

    }

}
