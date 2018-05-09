package com.wuli.delivery.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.app.log.Log;

/**
 * 用于ViewPager的单页视图，可实现懒加载(仅在Fragment可见时才加载数据).
 * 注意: ViewPager对应的Adapter处理tab的方式必须是attach和detach，而不能采用hide和show.
 * 所以该Fragment的生命周期与其所依附的activity将保持一致
 */
public abstract class BasePagedFragment extends BaseFragment {

    private static final String TAG = BasePagedFragment.class.getSimpleName();
    private View mRootView;
    private boolean mVisibleToUser;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            Log.debug(TAG, "onCreateView root is null !! [" + getClass().getSimpleName() + "]");
            mRootView = onCreatePage(inflater, container, savedInstanceState);
        } else {
            Log.debug(TAG, "onCreateView root reused !! [" + getClass().getSimpleName() + "]");
            ViewParent parent = mRootView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(mRootView);
            }
        }
        return mRootView;
    }

    /**
     * 每次在视图可见的时候，用于加载数据，比如启动网络数据的加载.
     * 调用场景：
     * 1.切换到对应当前页签时
     * 2.从其他Activity返回到当前Activity时
     */
    protected abstract void onPageStart();

    /**
     * 创建视图
     */
    protected abstract View onCreatePage(LayoutInflater inflater, @Nullable ViewGroup container,
                                         @Nullable Bundle savedInstanceState);

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        mVisibleToUser = isVisibleToUser;
        Log.debug(TAG, "setUserVisibleHint isVisibleToUser=" + isVisibleToUser + ", [" + getClass().getSimpleName() + "]");
        if(isVisibleToUser && mResumed) {
            Log.debug(TAG, "onPageStart !! [" + getClass().getSimpleName() + "]");
            onPageStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.debug(TAG, "onResume !! mVisibleToUser =" + mVisibleToUser + ", [" + getClass().getSimpleName() + "]");
        if(mVisibleToUser) {
            Log.debug(TAG, "onPageStart !! [" + getClass().getSimpleName() + "]");
            onPageStart();
        }
    }

}
