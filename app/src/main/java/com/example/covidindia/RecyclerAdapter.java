package com.example.covidindia;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.mViewHolder> {

    JSONArray data;
    Context ct;

    public RecyclerAdapter(Context context,JSONArray jsonArray){
        ct=context;
        data=jsonArray;
    }
    @NonNull
    @Override
    public mViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(ct);
        View view=layoutInflater.inflate(R.layout.recyclerrow,parent,false);
        return new mViewHolder(view);
    }

    public List<TextView> createTextview(int arraylength, LinearLayout layout){
        layout.removeAllViews();
        List<TextView> textlist = new ArrayList<>();
        for (int i =0;i < arraylength-1;i++){
            TextView data = new TextView(ct);
            data.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1f));
            data.setHeight(0);
            data.setGravity(Gravity.CENTER);
            layout.addView(data);
            textlist.add(data);
        }
        return textlist;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull mViewHolder holder, int position) {
        holder.cardView.setAnimation(AnimationUtils.loadAnimation(ct,R.anim.recycler_anim));
        List<String> valuelist = new ArrayList<>();
        List<String> keylist = new ArrayList<>();
        try {
            JSONObject object = data.getJSONObject(position);
            Iterator<String> keys = object.keys();
            while (keys.hasNext()){
                String key = keys.next();
                keylist.add(key);
                String value = object.getString(key);
                valuelist.add(value);
            }
            holder.headinghead.setText(valuelist.get(0));
            List<TextView> textlist = createTextview(data.getJSONObject(0).length(),holder.linearLayout);
            for (int i =0;i<textlist.size();i++){
                TextView textView = textlist.get(i);
                textView.setText(keylist.get(i+1)+" : "+valuelist.get(i+1));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return data.length();
    }

    public class mViewHolder extends RecyclerView.ViewHolder {

        TextView headinghead;
        CardView cardView;
        LinearLayout linearLayout;
        public mViewHolder(@NonNull View itemView) {
            super(itemView);
            headinghead = itemView.findViewById(R.id.headinghead);
            linearLayout = itemView.findViewById(R.id.datalayout);
            cardView = itemView.findViewById(R.id.cardrecyclerdata);
        }
    }
}
