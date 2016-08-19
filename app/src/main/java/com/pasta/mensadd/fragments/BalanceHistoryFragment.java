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
import java.util.Locale;

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

public class BalanceHistoryFragment extends Fragment {

    private float mMaxBalance;
    private float mMaxTransaction;
    private SimpleDateFormat mDateFormat;
    private ArrayList<Float> mBalance = new ArrayList<>();
    private ArrayList<Float> mTransactions = new ArrayList<>();
    private ArrayList<Long> mTimestamps = new ArrayList<>();
    private LineChartView mBalanceChart;
    private ColumnChartView mTransactionChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_balance_history, container, false);
        MainActivity.hideToolbarShadow(false);
        Locale locale;
        String dateFormat;
        if (Locale.getDefault().getLanguage().equals("de")) {
            locale = Locale.GERMANY;
            dateFormat = "dd.MM.";
        } else {
            locale = Locale.ENGLISH;
            dateFormat = "MM-dd";
        }
        mDateFormat = new SimpleDateFormat(dateFormat, locale);
        mBalance.clear();
        mTransactions.clear();
        mTimestamps.clear();
        mBalanceChart = (LineChartView) v.findViewById(R.id.lineChart);
        mTransactionChart = (ColumnChartView) v.findViewById(R.id.columnChart);
        TextView mCurrentBalance = (TextView) v.findViewById(R.id.currentBalance);
        TextView mCurrentLastTransaction = (TextView) v.findViewById(R.id.currentLastTransaction);
        DatabaseController dbController = new DatabaseController(getActivity().getApplicationContext());
        SQLiteDatabase db = dbController.getReadableDatabase();
        String[] projection = {
                DatabaseController.BALANCE_ID,
                DatabaseController.CARD_BALANCE,
                DatabaseController.LAST_TRANSACTION};

        String sortOrder =
                DatabaseController.BALANCE_ID + " ASC";

        Cursor c = db.query(
                DatabaseController.BALANCES_TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );


        while (c.moveToNext()) {
            mBalance.add(c.getFloat(c.getColumnIndex(DatabaseController.CARD_BALANCE)));
            mTransactions.add(c.getFloat(c.getColumnIndex(DatabaseController.LAST_TRANSACTION)));
            mTimestamps.add(c.getLong(c.getColumnIndex(DatabaseController.BALANCE_ID)));
            if (c.isLast()){
                String b = getString(R.string.balance_check_balance)+": "+formatMoneyString(c.getFloat(c.getColumnIndex(DatabaseController.CARD_BALANCE)));
                String t = getString(R.string.balance_check_last_transaction)+": "+formatMoneyString(c.getFloat(c.getColumnIndex(DatabaseController.LAST_TRANSACTION)));
                mCurrentBalance.setText(b);
                mCurrentLastTransaction.setText(t);
            }
        }
        c.close();
        TextView noBalanceText = (TextView) v.findViewById(R.id.notEnoughDataForLine);
        TextView noTransactionText = (TextView) v.findViewById(R.id.notEnoughDataForColumn);
        if (mBalance.size() > 1) {
            setUpBalanceChart();
            setUpTransactionsChart();
            noBalanceText.setVisibility(View.GONE);
            noTransactionText.setVisibility(View.GONE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBalanceChart.startDataAnimation();
                    mTransactionChart.startDataAnimation();
                }
            }, 500);
        } else {
            if (mBalance.isEmpty()){
                mCurrentBalance.setText(getString(R.string.no_data_available));
            }
        }

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
        for (int i = 0; i< mBalance.size(); i++){
            values.add(new PointValue(i,i));
            if (mBalance.get(i) > mMaxBalance)
                mMaxBalance = mBalance.get(i);
            Date date = new Date(mTimestamps.get(i));
            axisValues.add(new AxisValue(i).setLabel(mDateFormat.format(date)));
        }


        Line line = new Line(values).setColor(getResources().getColor(R.color.pink_dark)).setCubic(false).setFilled(true).setHasPoints(false);
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
        viewport.top = (int)(mMaxBalance *1.3);
        mBalanceChart.setMaximumViewport(viewport);
        mBalanceChart.setCurrentViewport(viewport);

        for (int j = 0; j<line.getValues().size();j++){
            line.getValues().get(j).setTarget(j, mBalance.get(j));
        }
    }

    public void setUpTransactionsChart(){
        int numColumns = mTransactions.size();
        List<AxisValue> axisValues = new ArrayList<>();
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values;
        for (int i = 0; i < numColumns; ++i) {
            values = new ArrayList<>();
            values.add(new SubcolumnValue(i, getResources().getColor(R.color.cyan_dark)));
            Date date = new Date(mTimestamps.get(i));
            axisValues.add(new AxisValue(i).setLabel(mDateFormat.format(date)));
            if (mTransactions.get(i) > mMaxTransaction)
                mMaxTransaction = mTransactions.get(i);
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
        viewport.top = (int)(mMaxTransaction *1.3);
        mTransactionChart.setMaximumViewport(viewport);
        mTransactionChart.setCurrentViewport(viewport);

        for (int j = 0; j<data.getColumns().size(); j++){
            for (SubcolumnValue value : data.getColumns().get(j).getValues()) {
                value.setTarget(mTransactions.get(j));
            }
        }
    }

}
