package com.example.covidindia;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

class MyHorizontalScrollView extends HorizontalScrollView {

    private ScrollViewListener scrollViewListener = null;
    public interface ScrollViewListener {

        void onScrollChanged(MyHorizontalScrollView HorizonscrollView, int x, int y, int oldx, int oldy);

    }
    public MyHorizontalScrollView(Context context) {
        super(context);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }



    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if(scrollViewListener != null) {
            scrollViewListener.onScrollChanged( this,l, t, oldl, oldt);
        }
    }

}
