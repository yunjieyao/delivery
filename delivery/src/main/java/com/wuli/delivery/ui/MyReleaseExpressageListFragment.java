package com.wuli.delivery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wuli.delivery.AppConstants;
import com.wuli.delivery.R;
import com.wuli.delivery.base.BasePagedFragment;
import com.wuli.delivery.portal.dao.ExpressageDao;
import com.wuli.delivery.portal.event.Event;
import com.wuli.delivery.portal.event.EventPublisher;
import com.wuli.delivery.ui.adapters.MyReleaseExpressageListAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyReleaseExpressageListFragment extends BasePagedFragment {


    @BindView(R.id.listview_release_epressage)
    ListView listviewReleaseEpressage;

    private Unbinder unbinder;
    private MyReleaseExpressageListAdapter mAdapter;

    public static MyReleaseExpressageListFragment newInstance() {
        MyReleaseExpressageListFragment myReleaseExpressageListFragment = new MyReleaseExpressageListFragment();
        return myReleaseExpressageListFragment;
    }


    @Override
    public String getTitle() {
        return null;
    }

    @Override
    protected void onPageStart() {
        mAdapter.notifyDataSetChanged(ExpressageDao.getExpressageListByLeadType(AppConstants.DB.EXPRESSAGE_LEAD_TYPE_RELEASE));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInsertDataSuccEvent(Event.InsertDataSuccEvent event) {
        mAdapter.notifyDataSetChanged(ExpressageDao.getExpressageListByLeadType(AppConstants.DB.EXPRESSAGE_LEAD_TYPE_RELEASE));
    }

    @Override
    protected View onCreatePage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_release_expressage_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        mAdapter = new MyReleaseExpressageListAdapter(null);
        listviewReleaseEpressage.setAdapter(mAdapter);
        return rootView;
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
