package com.example.covidindia;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

public class MarkerviewChart extends MarkerView {
    TextView tvContent,tvContent2;
    ArrayList<String> xaxesVal;
    String type;

    public MarkerviewChart(Context context, int layoutResource,ArrayList<String> listVal,String Charttype) {
        super(context, layoutResource);
        tvContent = findViewById(R.id.tvContent);
        tvContent2 = findViewById(R.id.tvContent2);
        if (listVal != null){
            xaxesVal=listVal;
        }
        type=Charttype;
    }
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            switch (type){
                case "line":
                    tvContent.setText(xaxesVal.get((int) e.getX()));
                    tvContent2.setText(Utils.formatNumber(e.getY(), 0, false));
                    break;
                case "bar":
                    BarEntry b = (BarEntry) e;
                    tvContent.setText(xaxesVal.get((int) e.getX()));
                    tvContent2.setText(Utils.formatNumber(b.getY(), 0, false));
                    break;
            }

        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
