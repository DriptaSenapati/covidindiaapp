package com.example.covidindia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TableMainLayout extends RelativeLayout{
    Context context;
    RelativeLayout relativeLayout;
    JSONArray data;
    List<String> tableheaders=new ArrayList<>();
    TableLayout tableA,tableB,tableC,tableD;

    HorizontalScrollView horizontalScrollViewB,horizontalScrollViewD;

    ScrollView scrollViewC,scrollViewD;
    int headerCellsWidth[] ;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public TableMainLayout(Context context, RelativeLayout layout, JSONArray array) throws JSONException {
        super(context);
        this.context = context;
        this.relativeLayout = layout;
        this.data = array;
        JSONObject object = array.getJSONObject(0);
        Iterator<String> iter = object.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            tableheaders.add(key);
        }
        this.headerCellsWidth= new int[tableheaders.size()];
        this.initializeTable();
        this.setComponentsId();
        this.setScrollViewAndHorizontalScrollViewTag();

        this.horizontalScrollViewB.addView(this.tableB);

        this.scrollViewC.addView(this.tableC);

        this.scrollViewD.addView(this.horizontalScrollViewD);
        this.horizontalScrollViewD.addView(this.tableD);
        this.addComponentToMainLayout();
        this.addTableRowToTableA();
        this. addTableRowToTableB();
        this.resizeHeaderHeight();
        this.getTableRowHeaderCellWidth();

        this.generateTableC_AndTable_B();

        this.resizeBodyTableRowHeight();
    }

    private void setScrollViewAndHorizontalScrollViewTag() {
        this.horizontalScrollViewB.setTag("horizontal scroll view b");
        this.horizontalScrollViewD.setTag("horizontal scroll view d");

        this.scrollViewC.setTag("scroll view c");
        this.scrollViewD.setTag("scroll view d");
    }

    private void resizeBodyTableRowHeight() {
        int tableC_ChildCount = this.tableC.getChildCount();

        for(int x=0; x<tableC_ChildCount; x++){

            TableRow productNameHeaderTableRow = (TableRow) this.tableC.getChildAt(x);
            TableRow productInfoTableRow = (TableRow)  this.tableD.getChildAt(x);

            int rowAHeight = this.viewHeight(productNameHeaderTableRow);
            int rowBHeight = this.viewHeight(productInfoTableRow);

            TableRow tableRow = rowAHeight < rowBHeight ? productNameHeaderTableRow : productInfoTableRow;
            int finalHeight = rowAHeight > rowBHeight ? rowAHeight : rowBHeight;

            this.matchLayoutHeight(tableRow, finalHeight);
        }
    }

    private void generateTableC_AndTable_B() throws JSONException {
        for (int i = 0;i<data.length();i++){

            TableRow tableRowForTableC = this.tableRowForTableC(data.getJSONObject(i));
            TableRow taleRowForTableD = this.taleRowForTableD(data.getJSONObject(i));

            tableRowForTableC.setBackgroundColor(Color.LTGRAY);
            taleRowForTableD.setBackgroundColor(Color.LTGRAY);

            this.tableC.addView(tableRowForTableC);
            this.tableD.addView(taleRowForTableD);
        }
    }
    TableRow tableRowForTableC(JSONObject object) throws JSONException {
        Iterator<String> keys = object.keys();
        TableRow.LayoutParams params = new TableRow.LayoutParams( this.headerCellsWidth[0],LayoutParams.MATCH_PARENT);
        params.setMargins(0, 2, 0, 0);

        TableRow tableRowForTableC = new TableRow(this.context);
        TextView textView = this.bodyTextView(object.getString(keys.next()));
        tableRowForTableC.addView(textView,params);

        return tableRowForTableC;
    }
    TableRow taleRowForTableD(JSONObject object) throws JSONException {
        TableRow tableRowForTableD = new TableRow(this.context);
        Iterator<String> keys = object.keys();
        List<String> valuelist = new ArrayList<>();
        while (keys.hasNext()){
            String key = keys.next();
            String value = object.getString(key);
            valuelist.add(value);
        }
        for(int x=0 ; x<valuelist.size()-1; x++){
            TableRow.LayoutParams params = new TableRow.LayoutParams( headerCellsWidth[x+1],LayoutParams.MATCH_PARENT);
            params.setMargins(2, 2, 0, 0);

            TextView textView = this.bodyTextView(valuelist.get(x+1));
            tableRowForTableD.addView(textView,params);
        }
        return tableRowForTableD;
    }
    TextView bodyTextView(String label){

        TextView bodyTextView = new TextView(this.context);
        bodyTextView.setBackgroundColor(Color.WHITE);
        bodyTextView.setText(label);
        bodyTextView.setGravity(Gravity.CENTER);
        bodyTextView.setPadding(5, 5, 5, 5);

        return bodyTextView;
    }
    private void getTableRowHeaderCellWidth() {
        int tableAChildCount = ((TableRow)this.tableA.getChildAt(0)).getChildCount();
        int tableBChildCount = ((TableRow)this.tableB.getChildAt(0)).getChildCount();;

        for(int x=0; x<(tableAChildCount+tableBChildCount); x++){

            if(x==0){
                this.headerCellsWidth[x] = this.viewWidth(((TableRow)this.tableA.getChildAt(0)).getChildAt(x));
            }else{
                this.headerCellsWidth[x] = this.viewWidth(((TableRow)this.tableB.getChildAt(0)).getChildAt(x-1));
            }

        }
    }
    private int viewWidth(View view) {
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        return view.getMeasuredWidth();
    }
    private void resizeHeaderHeight() {
        TableRow productNameHeaderTableRow = (TableRow) this.tableA.getChildAt(0);
        TableRow productInfoTableRow = (TableRow)  this.tableB.getChildAt(0);

        int rowAHeight = this.viewHeight(productNameHeaderTableRow);
        int rowBHeight = this.viewHeight(productInfoTableRow);

        TableRow tableRow = rowAHeight < rowBHeight ? productNameHeaderTableRow : productInfoTableRow;
        int finalHeight = rowAHeight > rowBHeight ? rowAHeight : rowBHeight;

        this.matchLayoutHeight(tableRow, finalHeight);
    }

    private void matchLayoutHeight(TableRow tableRow, int height) {
        int tableRowChildCount = tableRow.getChildCount();
        if(tableRow.getChildCount()==1){

            View view = tableRow.getChildAt(0);
            TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();
            params.height = height - (params.bottomMargin + params.topMargin);

            return ;
        }
        for (int x = 0; x < tableRowChildCount; x++) {

            View view = tableRow.getChildAt(x);

            TableRow.LayoutParams params = (TableRow.LayoutParams) view.getLayoutParams();

            if (!isTheHeighestLayout(tableRow, x)) {
                params.height = height - (params.bottomMargin + params.topMargin);
                return;
            }
        }
    }

    private boolean isTheHeighestLayout(TableRow tableRow, int layoutPosition) {
        int tableRowChildCount = tableRow.getChildCount();
        int heighestViewPosition = -1;
        int viewHeight = 0;

        for (int x = 0; x < tableRowChildCount; x++) {
            View view = tableRow.getChildAt(x);
            int height = this.viewHeight(view);

            if (viewHeight < height) {
                heighestViewPosition = x;
                viewHeight = height;
            }
        }

        return heighestViewPosition == layoutPosition;
    }

    private int viewHeight(View view) {
        view.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    private void addTableRowToTableB() {
        TableRow componentBTableRow = new TableRow(this.context);
        int headerFieldCount = this.tableheaders.size();

        TableRow.LayoutParams params = new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        params.setMargins(2, 0, 0, 0);

        for(int x=0; x<(headerFieldCount-1); x++){
            TextView textView = this.headerTextView(this.tableheaders.get(x+1));
            textView.setLayoutParams(params);
            textView.setMinWidth(200);
            componentBTableRow.addView(textView);

        }
        this.tableB.addView(componentBTableRow);
    }

    private void addTableRowToTableA() {
        TableRow componentATableRow = new TableRow(this.context);
//        TableRow.LayoutParams tableAlayout = new TableRow.LayoutParams(SCREEN_WIDTH/5, SCREEN_HEIGHT/20);
        TextView textView = this.headerTextView(this.tableheaders.get(0));
        textView.setWidth(400);
        componentATableRow.addView(textView);
        this.tableA.addView(componentATableRow);
    }

    private void addComponentToMainLayout() {
        LayoutParams componentB_Params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        componentB_Params.addRule(RelativeLayout.RIGHT_OF, this.tableA.getId());

        LayoutParams componentC_Params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        componentC_Params.addRule(RelativeLayout.BELOW, this.tableA.getId());

        LayoutParams componentD_Params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        componentD_Params.addRule(RelativeLayout.RIGHT_OF, this.scrollViewC.getId());
        componentD_Params.addRule(RelativeLayout.BELOW, this.horizontalScrollViewB.getId());

        this.relativeLayout.addView(this.tableA);
        this.relativeLayout.addView(this.horizontalScrollViewB, componentB_Params);
        this.relativeLayout.addView(this.scrollViewC, componentC_Params);
        this.relativeLayout.addView(this.scrollViewD, componentD_Params);
    }

    @SuppressLint("ResourceType")
    private void setComponentsId() {
        this.tableA.setId(1);
        this.horizontalScrollViewB.setId(2);
        this.scrollViewC.setId(3);
        this.scrollViewD.setId(4);
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initializeTable() {
        this.tableA = new TableLayout(this.context);
        this.tableB = new TableLayout(this.context);
        this.tableC = new TableLayout(this.context);
        this.tableD = new TableLayout(this.context);


        this.horizontalScrollViewB = new MyHorizontalScrollView(this.context);
        this.horizontalScrollViewD = new MyHorizontalScrollView(this.context);
        this.scrollViewC = new MyScrollView(this.context);
        this.scrollViewC.setNestedScrollingEnabled(true);
        this.scrollViewD = new MyScrollView(this.context);
        this.scrollViewD.setNestedScrollingEnabled(true);
        this.horizontalScrollViewD.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                horizontalScrollViewB.scrollTo(i,i1);
                Log.i("msgscroll", String.valueOf(i1));
            }
        });
        this.horizontalScrollViewB.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                horizontalScrollViewD.scrollTo(i,i1);
                Log.i("msgscroll3", String.valueOf(i1));
            }
        });
        this.scrollViewD.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                scrollViewC.scrollTo(i,i1);
            }
        });
        this.scrollViewC.setOnScrollChangeListener(new OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                scrollViewD.scrollTo(i,i1);
                Log.i("msgscroll4", String.valueOf(i1));
            }
        });

        this.tableA.setBackgroundColor(Color.GRAY);
        this.horizontalScrollViewB.setBackgroundColor(Color.LTGRAY);
    }
    TextView headerTextView(String label){

        TextView headerTextView = new TextView(this.context);
        headerTextView.setBackgroundColor(Color.WHITE);
        headerTextView.setText(label);
        headerTextView.setGravity(Gravity.CENTER);
        headerTextView.setPadding(5, 5, 5, 5);

        return headerTextView;
    }

}
