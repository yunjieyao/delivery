package com.wuli.delivery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wuli.delivery.R;
import com.wuli.delivery.base.BasePagedFragment;
import com.wuli.delivery.view.CustomViewPager;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class ExpressageFragment extends BasePagedFragment {

    @BindView(R.id.layout_express_delivery)
    TabLayout layoutExpressDelivery;
    @BindView(R.id.viewPager)
    CustomViewPager viewPager;
    @BindArray(R.array.expressage_list_titles)
    String[] expressagelistTitles;

    private Unbinder unbinder;
    private View rootView;

    private static final BasePagedFragment[] fragments = {MyReleaseExpressageListFragment.newInstance(), MyReceiveExpressageListFragment.newInstance(), MyExpressageListFragment.newInstance()};


    public static ExpressageFragment newInstance() {
        ExpressageFragment expressageFragment = new ExpressageFragment();
        return expressageFragment;
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
            rootView = inflater.inflate(R.layout.fragment_expressage, container, false);
            unbinder = ButterKnife.bind(this, rootView);
            initViews();
        }
        return rootView;
    }

    private void initViews() {
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPagingEnabled(false);
        viewPager.setAdapter(new MyPageAdapter(getChildFragmentManager(), expressagelistTitles));
        layoutExpressDelivery.setupWithViewPager(viewPager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }


    static class MyPageAdapter extends FragmentPagerAdapter {

        private String[] expressagelistTitles;

        public MyPageAdapter(FragmentManager fm, String[] expressagelistTitles) {
            super(fm);
            this.expressagelistTitles = expressagelistTitles;

        }

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return expressagelistTitles[position];
        }
    }
}
