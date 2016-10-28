package com.bubu.loopingbanner.example;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bubu.loopingbanner.viewpager.LBViewPageAdapterViewHolder;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * Created by Sai on 15/8/4.
 * 网络图片加载例子
 */
public class NetworkImageLBViewPageAdapterViewHolderView implements LBViewPageAdapterViewHolder<String> {
    private static final String TAG = "NetworkImageHolderView";
    private ImageView imageView;

    @Override
    public View createView(Context context) {
        //你可以通过layout文件来创建，也可以像我一样用代码创建，不一定是Image，任何控件都可以进行翻页
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        Log.d(TAG, "UpdateUI:data " + data);
        imageView.setImageResource(R.mipmap.ic_default_adimage);
        ImageLoader.getInstance().displayImage(data, imageView);
    }
}
