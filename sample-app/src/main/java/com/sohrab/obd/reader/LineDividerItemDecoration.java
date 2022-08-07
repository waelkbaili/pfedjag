package com.sohrab.obd.reader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class LineDividerItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public LineDividerItemDecoration(Context context, int dividerDrawable) {
        mDivider = ContextCompat.getDrawable(context, dividerDrawable);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int val1=parent.getWidth();
        int val = parent.getPaddingRight();
        int right = val1-val;
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
