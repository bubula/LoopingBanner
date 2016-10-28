package com.bubu.loopingbanner.indicator;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * 默认的指针
 * Created by Administrator on 2016/10/27.
 */

public class DefaultLBIndicator implements LBIndicatorView {
    private LinearLayout loPageTurningPoint;
    Context context;
    private ArrayList<ImageView> mPointViews = new ArrayList<>();
    private int indicatorDefault;
    private int indicatorFocused;

    public enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }

    public static DefaultLBIndicator getInstance(Context context, int indicatorDefault, int indicatorFocused) {
        return new DefaultLBIndicator(context).setIndicatorFocused(indicatorFocused).setIndicatorDefault(indicatorDefault);
    }


    public DefaultLBIndicator(Context context) {
        this.context = context;
        loPageTurningPoint = new LinearLayout(context);
        loPageTurningPoint.setGravity(Gravity.BOTTOM | Gravity.CENTER);
    }

    public DefaultLBIndicator setIndicatorDefault(int indicatorDefault) {
        this.indicatorDefault = indicatorDefault;
        return this;
    }

    public DefaultLBIndicator setIndicatorFocused(int indicatorFocused) {
        this.indicatorFocused = indicatorFocused;
        return this;
    }

    /**
     * 指示器的方向
     *
     * @param align 三个方向：居左 （RelativeLayout.ALIGN_PARENT_LEFT），居中 （RelativeLayout.CENTER_HORIZONTAL），居右 （RelativeLayout.ALIGN_PARENT_RIGHT）
     * @return
     */
    public DefaultLBIndicator setPageIndicatorAlign(PageIndicatorAlign align) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) loPageTurningPoint.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        loPageTurningPoint.setLayoutParams(layoutParams);
        return this;
    }

    @Override
    public ViewGroup getView() {
        return loPageTurningPoint;
    }

    @Override
    public void PageTurningChange(int position) {
        for (int i = 0; i < mPointViews.size(); i++) {
            mPointViews.get(position).setImageResource(indicatorDefault);
            if (position != i) {
                mPointViews.get(i).setImageResource(indicatorFocused);
            }
        }
    }

    @Override
    public void notifyDataSetChanged(int allCount, int position) {
        loPageTurningPoint.removeAllViews();
        mPointViews.clear();
        for (int count = 0; count < allCount; count++) {
            // 翻页指示的点
            ImageView pointView = new ImageView(context);
            pointView.setPadding(5, 0, 5, 0);
            if (mPointViews.isEmpty()) {
                pointView.setImageResource(indicatorFocused);
            } else {
                pointView.setImageResource(indicatorDefault);
            }

            mPointViews.add(pointView);
            loPageTurningPoint.addView(pointView);
        }
    }

}
