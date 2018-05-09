package com.wuli.delivery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wuli.delivery.base.BasePagedFragment;

public class WuliCircleFragment extends BasePagedFragment {


    public static WuliCircleFragment newInstance() {
        WuliCircleFragment wuliCircleFragment = new WuliCircleFragment();
        return wuliCircleFragment;
    }


    @Override
    public String getTitle() {
        return null;
    }

    @Override
    protected void onPageStart() {

    }

    @Override
    protected View onCreatePage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return null;
    }
}
