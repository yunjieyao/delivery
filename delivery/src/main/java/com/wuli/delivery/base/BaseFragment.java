package com.wuli.delivery.base;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.log.Log;
import com.app.util.AndroidUtil;


public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    protected boolean mResumed;
    protected boolean mCreate;
    protected boolean mStopped;
    protected boolean mDestroyed;
    protected boolean mHidden;

    /**
     * 对应神策中$title
     *
     * @return
     */
    public abstract String getTitle();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDestroyed = false;
        mCreate = true;
        mHidden = true;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onCreate, Hidden = " + mHidden);
    }

    @Override
    public void onStart() {
        super.onStart();
        mStopped = false;
        mHidden = false;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onStart, Hidden = " + mHidden);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mHidden = hidden;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onHiddenChanged " + hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        mResumed = true;
        mHidden = false;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onResume, Hidden = " + mHidden);
    }

    @Override
    public void onPause() {
        super.onPause();
        mResumed = false;
        mHidden = false;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onPause, Hidden = " + mHidden);
    }

    @Override
    public void onStop() {
        super.onStop();
        mHidden = true;
        mStopped = true;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onStop, Hidden = " + mHidden);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDestroyed = true;
        mHidden = true;
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onDestroy, Hidden = " + mHidden);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onDetach");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onAttach");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onViewCreated");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: onActivityCreated");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.debug(TAG, "[" + getClass().getSimpleName() + "]: setUserVisibleHint " + isVisibleToUser);
    }

    public void safetyDismissDialog(Dialog dialog) {
        if (AndroidUtil.isValidContext(getActivity()) && dialog != null && dialog.isShowing()) {
            try {
                dialog.dismiss();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}