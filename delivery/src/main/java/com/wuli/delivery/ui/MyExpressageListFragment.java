package com.wuli.delivery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wuli.delivery.R;
import com.wuli.delivery.base.BasePagedFragment;
import com.wuli.delivery.portal.dao.ExpressageDao;
import com.wuli.delivery.portal.event.Event;
import com.wuli.delivery.portal.event.EventPublisher;
import com.wuli.delivery.ui.adapters.MyExpressageListAdapter;
import com.wuli.delivery.view.RoundedTextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyExpressageListFragment extends BasePagedFragment {

    @BindView(R.id.rtv_import_taobao_info)
    RoundedTextView rtvImportTaobaoInfo;
    @BindView(R.id.rtv_import_expressage_info)
    RoundedTextView rtvImportExpressageInfo;
    @BindView(R.id.rtv_import_short_message)
    RoundedTextView rtvImportShortMessage;
    @BindView(R.id.listview_expressage)
    ListView listviewExpressage;

    private Unbinder unbinder;
    private MyExpressageListAdapter mAdapter;


    public static MyExpressageListFragment newInstance() {
        MyExpressageListFragment myExpressageListFragment = new MyExpressageListFragment();
        return myExpressageListFragment;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    protected void onPageStart() {
        mAdapter.notifyDataSetChanged(ExpressageDao.getAllExpressageList());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInsertDataSuccEvent(Event.InsertDataSuccEvent event) {
        mAdapter.notifyDataSetChanged(ExpressageDao.getAllExpressageList());
    }

    @Override
    protected View onCreatePage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_expressage_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        mAdapter = new MyExpressageListAdapter(null);
        listviewExpressage.setAdapter(mAdapter);
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
