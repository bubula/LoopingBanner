package com.bubu.loopingbanner.viewpager;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class LBViewPagerScroller extends Scroller {
	private int mScrollDuration = 2800;// 滑动速度,值越大滑动越慢，滑动太快会使3d效果不明显
	private boolean zero;

	public LBViewPagerScroller(Context context) {
		super(context);
	}

	public LBViewPagerScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	public LBViewPagerScroller(Context context, Interpolator interpolator,
							   boolean flywheel) {
		super(context, interpolator, flywheel);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, zero ? 0 : mScrollDuration);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		super.startScroll(startX, startY, dx, dy, zero ? 0 : mScrollDuration);
	}

	public int getScrollDuration() {
		return mScrollDuration;
	}

	public void setScrollDuration(int scrollDuration) {
		this.mScrollDuration = scrollDuration;
	}

	public boolean isZero() {
		return zero;
	}

	public void setZero(boolean zero) {
		this.zero = zero;
	}
}