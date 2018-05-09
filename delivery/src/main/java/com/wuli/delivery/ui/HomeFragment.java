package com.wuli.delivery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.wuli.delivery.AppConstants;
import com.wuli.delivery.R;
import com.wuli.delivery.base.BasePagedFragment;
import com.wuli.delivery.portal.dao.ExpressageDao;
import com.wuli.delivery.portal.event.Event;
import com.wuli.delivery.portal.event.EventPublisher;
import com.wuli.delivery.ui.adapters.HomeExpressageListAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends BasePagedFragment {

    @BindView(R.id.spinner)
    Spinner spinner;
    @BindView(R.id.listview_home_expressage_list)
    ListView listviewHomeExpressageList;

    private Unbinder unbinder;
    private View rootView;
    private HomeExpressageListAdapter mAdapter;


    public static HomeFragment newInstance() {
        HomeFragment homeFragment = new HomeFragment();
        return homeFragment;
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
            rootView = inflater.inflate(R.layout.fragment_home, container, false);
            unbinder = ButterKnife.bind(this, rootView);
            EventPublisher.getInstance().register(this);
            initData();
        }
        return rootView;
    }

    private void initData() {
        spinner.setAdapter(ArrayAdapter.createFromResource(getActivity(), R.array.search_condition_items, android.R.layout.simple_spinner_item));
        mAdapter = new HomeExpressageListAdapter(ExpressageDao.getExpressageListByLeadType(AppConstants.DB.EXPRESSAGE_LEAD_TYPE_RECEIVE));
        listviewHomeExpressageList.setAdapter(mAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInsertDataSuccEvent(Event.InsertDataSuccEvent event) {
        mAdapter.notifyDataSetChanged(ExpressageDao.getExpressageListByLeadType(AppConstants.DB.EXPRESSAGE_LEAD_TYPE_RECEIVE));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventPublisher.getInstance().unRegister(this);
    }
}
