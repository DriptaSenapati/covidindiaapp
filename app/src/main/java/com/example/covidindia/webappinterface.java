package com.example.covidindia;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import org.json.JSONArray;

public class webappinterface {
    Context mContext;
    JSONArray Statedata,latlngData;

    /** Instantiate the interface and set the context */
    webappinterface(Context c, JSONArray data,JSONArray data1) {
        mContext = c;
        Statedata = data;
        latlngData = data1;

    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showlog(String toast) {
        Log.i("js",toast);
    }
    @JavascriptInterface
    public String getSateData() {
        Log.i("js",Statedata.toString());
        return Statedata.toString();
    }
    @JavascriptInterface
    public String getLatlngData() {
        return latlngData.toString();
    }
}
