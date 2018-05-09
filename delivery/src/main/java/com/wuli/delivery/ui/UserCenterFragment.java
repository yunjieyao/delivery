package com.wuli.delivery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wuli.delivery.R;
import com.wuli.delivery.base.BasePagedFragment;

public class UserCenterFragment extends BasePagedFragment {

    private View rootView;


    public static UserCenterFragment newInstance() {
        UserCenterFragment userCenterFragment = new UserCenterFragment();
        return userCenterFragment;
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
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_user_center, container, false);
        }
        return rootView;
    }
}
