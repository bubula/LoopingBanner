package com.bubu.loopingbanner.viewpager;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class LBViewPageAdapter<T> extends PagerAdapter {
    protected List<T> mData;
    protected LBViewPageAdapterViewHolder holder;
    private boolean canLoop = true;
    private LBViewPager viewPager;
    private final int MULTIPLE_COUNT = 300;

    public int toRealPosition(int position) {
        int realCount = getRealCount();
        if (realCount == 0)
            return 0;
        int realPosition = position % realCount;
        return realPosition;
    }

    public void setData(List<T> mData) {
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return canLoop ? getRealCount() * MULTIPLE_COUNT : getRealCount();
    }

    public int getRealCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int realPosition = toRealPosition(position);
        View view = getView(realPosition, null, container);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        int position = viewPager.getCurrentItem();
        if (position == 0) {
            position = viewPager.getFirstItem();
        } else if (position == getCount() - 1) {
            position = viewPager.getLastItem();
        }
        try {
            viewPager.setCurrentItem(position, false);
        } catch (IllegalStateException ignored) {
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
    }

    public void setViewPager(LBViewPager viewPager) {
        this.viewPager = viewPager;
    }

    public LBViewPageAdapter(LBViewPageAdapterViewHolder holder, List<T> data) {
        this.holder = holder;
        this.mData = data;
    }

    public View getView(int position, View view, ViewGroup container) {
        if (view == null) {
            view = holder.createView(container.getContext());
            view.setTag(holder);
        } else {
            holder = (LBViewPageAdapterViewHolder) view.getTag();
        }
        if (mData != null && !mData.isEmpty())
            holder.UpdateUI(container.getContext(), position, mData.get(position));
        return view;
    }

}
