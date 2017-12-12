package com.SuperKotlin.pictureviewer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.List;

/**
 * 图片查看器
 * Created by zhuyong on 2017/5/12.
 */
public class ImagePagerActivity extends FragmentActivity implements View.OnClickListener{
    private static final String TAG = "ImagePagerActivity";
    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";

    private HackyViewPager mPager;
    private int pagerPosition;
    private TextView indicator;
    private static boolean mIsShowNumber = true;//是否显示数字下标

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); //透明状态栏
        }
        setContentView(R.layout.activity_image_detail_pager);
        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        List<String> urls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
        mPager = (HackyViewPager) findViewById(R.id.pager);
        ImagePagerAdapter mAdapter = new ImagePagerAdapter(
                getSupportFragmentManager(), urls);
        mPager.setAdapter(mAdapter);
        indicator = (TextView) findViewById(R.id.indicator);

        CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
        indicator.setText(text);
        // 更新下标
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                CharSequence text = getString(R.string.viewpager_indicator,
                        arg0 + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }

        mPager.setCurrentItem(pagerPosition);
        indicator.setVisibility(mIsShowNumber ? View.VISIBLE : View.GONE);
        findViewById(R.id.btn_back).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btn_back)
            finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public List<String> fileList;

        public ImagePagerAdapter(FragmentManager fm, List<String> fileList) {
            super(fm);
            this.fileList = fileList;
        }

        @Override
        public int getCount() {
            return fileList == null ? 0 : fileList.size();
        }

        @Override
        public Fragment getItem(int position) {
            String url = fileList.get(position).toString();
            return ImageDetailFragment.newInstance(url);
        }

    }

    public static void startActivity(Context context, PictureConfig config) {
        Intent intent = new Intent(context, ImagePagerActivity.class);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, config.list);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, config.position);
        ImageDetailFragment.mImageLoading = config.resId;
        ImageDetailFragment.mNeedDownload = config.needDownload;
        mIsShowNumber = config.mIsShowNumber;
        ImageUtil.path = config.path;
        context.startActivity(intent);
    }


    public void fullScreenMode() {
        if (findViewById(R.id.title_bar).getVisibility() == View.VISIBLE) {
            indicator.setVisibility(View.GONE);
            findViewById(R.id.title_bar).setVisibility(View.GONE);
        } else {
            if (mIsShowNumber)
                indicator.setVisibility(View.VISIBLE);
            findViewById(R.id.title_bar).setVisibility(View.VISIBLE);
        }
    }

    public void setTitleBarVisibility(int visibility) {
        findViewById(R.id.title_bar).setVisibility(visibility);
    }
}