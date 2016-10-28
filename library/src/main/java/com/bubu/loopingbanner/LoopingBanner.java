package com.bubu.loopingbanner;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.PageTransformer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.bubu.loopingbanner.indicator.DefaultLBIndicator;
import com.bubu.loopingbanner.indicator.LBIndicatorView;
import com.bubu.loopingbanner.viewpager.LBViewPageAdapter;
import com.bubu.loopingbanner.viewpager.LBViewPageAdapterViewHolder;
import com.bubu.loopingbanner.viewpager.LBViewPager;
import com.bubu.loopingbanner.viewpager.LBViewPagerScroller;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 页面翻转控件，极方便的广告栏
 * 支持无限循环，自动翻页，翻页特效
 *
 * @author Sai 支持自动翻页
 */
public class LoopingBanner<T> extends FrameLayout {
    private List<T> mData = new ArrayList<>();
    private LBViewPageAdapter lbViewPageAdapter;//viewPager适配器
    private LBViewPager lbViewPager;//显示内容
    private LBViewPagerScroller lbViewPagerScroller;//ViewPager滚动
    private LBIndicatorView mLbIndicatorView;//指示器View
    private LBViewPageAdapterViewHolder lbPageAdapterViewHolder;//内容显示View
    private long autoTurningTime;//翻转间隔
    private boolean turning;//是否自动翻转
    private boolean canTurn = false;
    private boolean canLoop = true;//是否能翻转
    private AdSwitchTask adSwitchTask;//自动翻转线程
    private Context mContext;

    public LoopingBanner(Context context) {
        super(context);
        init(context);
    }

    public LoopingBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoopingBanner);
        canLoop = a.getBoolean(R.styleable.LoopingBanner_canLoop, true);
        a.recycle();
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public LoopingBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoopingBanner);
        canLoop = a.getBoolean(R.styleable.LoopingBanner_canLoop, true);
        a.recycle();
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoopingBanner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoopingBanner);
        canLoop = a.getBoolean(R.styleable.LoopingBanner_canLoop, true);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        lbViewPager = new LBViewPager(context);
        lbViewPager.setOnPageChangeListener(myPageChangeListener);
        addView(lbViewPager);
        initViewPagerScroll();
        adSwitchTask = new AdSwitchTask(this);
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            lbViewPagerScroller = new LBViewPagerScroller(
                    lbViewPager.getContext());
            mScroller.set(lbViewPager, lbViewPagerScroller);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置指示器View
     *
     * @param lbIndicatorView
     * @return
     */
    public LoopingBanner setLBIndicatorView(LBIndicatorView lbIndicatorView) {
        if (mLbIndicatorView != null) {
            removeView(mLbIndicatorView.getView());
        }
        this.mLbIndicatorView = lbIndicatorView;
        addView(lbIndicatorView.getView());
        lbIndicatorView.notifyDataSetChanged(lbViewPageAdapter.getRealCount(), lbViewPager.getRealItem());
        return this;
    }


    /**
     * 设置显示内容ViewHolder
     *
     * @param lbPageAdapterViewHolder
     */
    public LoopingBanner setLBPageView(LBViewPageAdapterViewHolder lbPageAdapterViewHolder) {
        this.lbPageAdapterViewHolder = lbPageAdapterViewHolder;
        lbViewPageAdapter = new LBViewPageAdapter<>(lbPageAdapterViewHolder, mData);
        lbViewPager.setAdapter(lbViewPageAdapter, canLoop);
        return this;
    }


    /**
     * 设置显示内容
     */
    @SuppressWarnings("unchecked")
    public LoopingBanner setLBPageData(List<T> data) {
        this.mData = data;
        lbViewPageAdapter.setData(mData);
        lbViewPageAdapter.notifyDataSetChanged();
        if (mLbIndicatorView != null) {
            mLbIndicatorView.notifyDataSetChanged(lbViewPageAdapter.getRealCount(), lbViewPager.getRealItem());
        }
        return this;
    }

    /**
     * 设置默认的指示器图片资源
     *
     * @param indicatorDefault
     * @param indicatorFocused
     * @return
     */
    public LoopingBanner setLBDefaultIndicatorIconRes(int indicatorDefault, int indicatorFocused) {
        setLBIndicatorView(DefaultLBIndicator.getInstance(mContext, indicatorDefault, indicatorFocused));
        return this;
    }

    /**
     * 自定义翻页动画效果
     *
     * @param transformer
     * @return
     */
    public LoopingBanner setLBTransformer(PageTransformer transformer) {
        lbViewPager.setPageTransformer(true, transformer);
        return this;
    }

    /**
     * 设置底部指示器是否可见
     *
     * @param visible
     */
    public LoopingBanner setLBIndicatorVisible(boolean visible) {
        if (mLbIndicatorView != null) {
            mLbIndicatorView.getView().setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        return this;
    }

    /**
     * /设置手动影响（设置了该项无法手动切换）
     */
    public void setLBCanManualScroll(boolean canManualScroll) {
        lbViewPager.setCanScroll(canManualScroll);
    }

    /**
     * 获取手动影响
     *
     * @return
     */
    public boolean isLBCanManualScroll() {
        return lbViewPager.isCanScroll();
    }

    /***
     * 是否开启了翻页
     *
     * @return
     */
    public boolean isTurning() {
        return turning;
    }

    public LoopingBanner setLBCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        lbViewPager.setCanLoop(canLoop);
        return this;
    }

    public boolean isLBCanLoop() {
        return lbViewPager.isCanLoop();
    }

    /**
     * 获取当前的页面index
     */

    public int getCurrentItem() {
        if (lbViewPager != null) {
            return lbViewPager.getRealItem();
        }
        return -1;
    }

    /**
     * 设置当前的页面index
     *
     * @param index
     */
    public void setLBCurrentItem(int index) {
        if (lbViewPager != null) {
            lbViewPager.setCurrentItem(index);
        }
    }

    /**
     * 设置ViewPager的滚动速度
     *
     * @param scrollDuration
     */
    public void setScrollDuration(int scrollDuration) {
        lbViewPagerScroller.setScrollDuration(scrollDuration);
    }

    /**
     * 获取ViewPager滚动速度
     *
     * @return
     */
    public int getScrollDuration() {
        return lbViewPagerScroller.getScrollDuration();
    }

    /**
     * 获取循环ViewPager
     *
     * @return
     */
    public LBViewPager getLbViewPager() {
        return lbViewPager;
    }

    /**
     * 获取PageChangeListener
     *
     * @return
     */
    public ViewPager.OnPageChangeListener getLBOnPageChangeListener() {
        return onPageChangeListener;
    }

    /**
     * 设置PageChangeListener
     *
     * @param onPageChangeListener
     * @return
     */
    public LoopingBanner setOnLBPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        return this;
    }

    /***
     * 开始翻页
     *
     * @param autoTurningTime 自动翻页时间
     * @return
     */
    public LoopingBanner startTurning(long autoTurningTime) {
        //如果是正在翻页的话先停掉
        if (turning) {
            stopTurning();
        }
        //设置可以翻页并开启翻页
        canTurn = true;
        this.autoTurningTime = autoTurningTime;
        turning = true;
        postDelayed(adSwitchTask, autoTurningTime);
        return this;
    }

    public void stopTurning() {
        turning = false;
        removeCallbacks(adSwitchTask);
    }

    /**
     * 定时自动播放
     */
    static class AdSwitchTask implements Runnable {

        private final WeakReference<LoopingBanner> reference;

        AdSwitchTask(LoopingBanner loopingBanner) {
            this.reference = new WeakReference<>(loopingBanner);
        }

        @Override
        public void run() {
            LoopingBanner loopingBanner = reference.get();

            if (loopingBanner != null) {
                if (loopingBanner.lbViewPager != null && loopingBanner.turning) {
                    int page = loopingBanner.lbViewPager.getCurrentItem() + 1;
                    loopingBanner.lbViewPager.setCurrentItem(page);
                    loopingBanner.postDelayed(loopingBanner.adSwitchTask, loopingBanner.autoTurningTime);
                }
            }
        }
    }


    //触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            // 开始翻页
            if (canTurn) startTurning(autoTurningTime);
        } else if (action == MotionEvent.ACTION_DOWN) {
            // 停止翻页
            if (canTurn) stopTurning();
        }
        return super.dispatchTouchEvent(ev);
    }


    private ViewPager.OnPageChangeListener onPageChangeListener;
    ViewPager.OnPageChangeListener myPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

        }

        @Override
        public void onPageSelected(int position) {
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageSelected(position);
            }
            if (mLbIndicatorView != null) {
                mLbIndicatorView.PageTurningChange(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };


    /**
     * 监听item点击
     *
     * @param onItemClickListener
     */
    public LoopingBanner setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener == null) {
            lbViewPager.setOnItemClickListener(null);
            return this;
        }
        lbViewPager.setOnItemClickListener(onItemClickListener);
        return this;
    }


}
