package com.wuli.delivery.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class MVPBasePagedFragment<V, T extends BasePresenter> extends BasePagedFragment {

    /**
     * presenter对象
     */
    private T mPresenter;

    protected abstract T createPresenter();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.detachView();
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    protected void onPageStart() {
        onFragmentPageStart();
    }

    @Override
    protected View onCreatePage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.mPresenter = createPresenter();
        mPresenter.attachView(this);
        return onCreateFragmentPage(inflater, container, savedInstanceState);
    }

    /**
     * 每次在视图可见的时候，用于加载数据，比如启动网络数据的加载.
     * 调用场景：
     * 1.切换到对应当前页签时
     * 2.从其他Activity返回到当前Activity时
     */
    protected abstract void onFragmentPageStart();

    /**
     * 创建视图
     */
    protected abstract View onCreateFragmentPage(LayoutInflater inflater, @Nullable ViewGroup container,
                                                 @Nullable Bundle savedInstanceState);

}
