package com.pasta.mensadd.fragments;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pasta.mensadd.MainActivity;
import com.pasta.mensadd.R;
import com.pasta.mensadd.controller.DatabaseController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lecho.lib.hellocharts.formatter.SimpleAxisValueFormatter;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class CardHistoryFragment extends Fragment {

    private float maxBalanceValue;
    private float maxTransactionValue;
    private SimpleDateFormat sf = new SimpleDateFormat("dd.MM");
    private ArrayList<Float> balanceList = new ArrayList<>();
    private ArrayList<Float> transactionList = new ArrayList<>();
    private ArrayList<Long> timestamps = new ArrayList<>();
    private LineChartView mBalanceChart;
    private ColumnChartView mTransactionChart;
    private TextView mCurrentBalance;
    private TextView mCurrentLastTransaction;


    public static CardHistoryFragment newInstance() {
        return new CardHistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_card_history, container, false);
        balanceList.clear();
        transactionList.clear();
        timestamps.clear();
        Log.i("CARD",balanceList.size()+"");
        mBalanceChart = (LineChartView) v.findViewById(R.id.lineChart);
        mTransactionChart = (ColumnChartView) v.findViewById(R.id.columnChart);
        mCurrentBalance = (TextView) v.findViewById(R.id.currentBalance);
        mCurrentLastTransaction = (TextView) v.findViewById(R.id.currentLastTransaction);
        DatabaseController dbController = new DatabaseController(getActivity().getApplicationContext());
        SQLiteDatabase db = dbController.getReadableDatabase();
        String[] projection = {
                DatabaseController.ID,
                DatabaseController.CARD_BALANCE,
                DatabaseController.LAST_TRANSACTION};

        String sortOrder =
                DatabaseController.ID + " ASC";

        Cursor c = db.query(
                DatabaseController.BALANCE_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
        while (c.moveToNext()) {
            balanceList.add(c.getFloat(c.getColumnIndex(DatabaseController.CARD_BALANCE)));
            transactionList.add(c.getFloat(c.getColumnIndex(DatabaseController.LAST_TRANSACTION)));
            timestamps.add(c.getLong(c.getColumnIndex(DatabaseController.ID)));
            if (c.isLast()){
                mCurrentBalance.setText("Guthaben: " + formatMoneyString(c.getFloat(c.getColumnIndex(DatabaseController.CARD_BALANCE))));
                mCurrentLastTransaction.setText("Letzte Abbuchung: " + formatMoneyString(c.getFloat(c.getColumnIndex(DatabaseController.LAST_TRANSACTION))));
            }

        }
        setUpBalanceChart();
        setUpTransactionsChart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mBalanceChart.startDataAnimation();
                mTransactionChart.startDataAnimation();
            }
        }, 500);
        MainActivity.updateNavDrawer(R.id.nav_card_history);
        return v;
    }

    public String formatMoneyString (float value) {
        String temp = ""+value;
        if (temp.length() == 4 && temp.substring(2,3).equals("."))
            temp += "0";
        return temp+"€";
    }


    public void setUpBalanceChart(){
        List<PointValue> values = new ArrayList<>();
        List<AxisValue> axisValues = new ArrayList<>();
        for (int i = 0; i<balanceList.size(); i++){
            values.add(new PointValue(i,i));
            if (balanceList.get(i) > maxBalanceValue)
                maxBalanceValue = balanceList.get(i);
            Date date = new Date(timestamps.get(i));
            axisValues.add(new AxisValue(i).setLabel(sf.format(date)));
        }


        Line line = new Line(values).setColor(Color.parseColor("#bbcc3c51")).setCubic(false).setFilled(true).setHasPoints(false);
        List<Line> lines = new ArrayList<>();
        lines.add(line);

        LineChartData data = new LineChartData();
        Axis axisY = new Axis().setHasLines(true).setMaxLabelChars(4).setFormatter(new SimpleAxisValueFormatter().setAppendedText("€".toCharArray()));
        Axis axisX = new Axis(axisValues).setMaxLabelChars(5);

        data.setAxisYLeft(axisY);
        data.setAxisXBottom(axisX);
        data.setLines(lines);

        mBalanceChart.setLineChartData(data);
        mBalanceChart.setViewportCalculationEnabled(false);
        Viewport viewport = new Viewport(mBalanceChart.getMaximumViewport());
        viewport.bottom = 0;
        viewport.top = (int)(maxBalanceValue*1.3);
        mBalanceChart.setMaximumViewport(viewport);
        mBalanceChart.setCurrentViewport(viewport);

        for (int j = 0; j<line.getValues().size();j++){
            line.getValues().get(j).setTarget(j, balanceList.get(j));
            Log.i("CARDHISTORY",balanceList.get(j)+"");
        }
    }

    public void setUpTransactionsChart(){
        int numColumns = transactionList.size();
        List<AxisValue> axisValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            values.add(new SubcolumnValue(i, Color.parseColor("#bb00888a")));
            Date date = new Date(timestamps.get(i));
            axisValues.add(new AxisValue(i).setLabel(sf.format(date)));
            if (transactionList.get(i) > maxTransactionValue)
                maxTransactionValue = transactionList.get(i);
            Column column = new Column(values);
            columns.add(column);
        }

        ColumnChartData data = new ColumnChartData(columns);

        Axis axisY = new Axis().setHasLines(true).setMaxLabelChars(4).setFormatter(new SimpleAxisValueFormatter().setAppendedText("€".toCharArray()));

        data.setAxisXBottom(new Axis(axisValues).setMaxLabelChars(5));
        data.setAxisYLeft(axisY);

        mTransactionChart.setColumnChartData(data);
        mTransactionChart.setViewportCalculationEnabled(false);
        Viewport viewport = new Viewport(mTransactionChart.getMaximumViewport());
        viewport.bottom = 0;
        viewport.top = (int)(maxTransactionValue*1.3);
        mTransactionChart.setMaximumViewport(viewport);
        mTransactionChart.setCurrentViewport(viewport);

        for (int j = 0; j<data.getColumns().size(); j++){
            for (SubcolumnValue value : data.getColumns().get(j).getValues()) {
                value.setTarget(transactionList.get(j));
            }
        }
    }

}
