package com.bubu.loopingbanner.indicator;

import android.view.ViewGroup;

/**
 * 滚动指示器
 * Created by Administrator on 2016/10/27.
 */

public interface LBIndicatorView {
    /**
     * 或者指示器
     *
     * @return VIEW
     */
    ViewGroup getView();

    /**
     * VIEWPAGER滚动变化
     */
    void PageTurningChange(int position);

    void notifyDataSetChanged(int allCount,int position);

}
