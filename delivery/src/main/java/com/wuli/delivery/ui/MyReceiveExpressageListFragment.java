package com.wuli.delivery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.wuli.delivery.AppConstants;
import com.wuli.delivery.R;
import com.wuli.delivery.base.BasePagedFragment;
import com.wuli.delivery.base.CommonDialogFragment;
import com.wuli.delivery.portal.OnDialogViewClickListener;
import com.wuli.delivery.portal.dao.ExpressageDao;
import com.wuli.delivery.portal.event.Event;
import com.wuli.delivery.portal.event.EventPublisher;
import com.wuli.delivery.ui.adapters.MyReceiveExpressageAdapter;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyReceiveExpressageListFragment extends BasePagedFragment implements MyReceiveExpressageAdapter.OnConfirmFinishedClickListener {


    @BindView(R.id.listview_receive_expresage)
    ListView listviewReceiveExpresage;
    @BindString(R.string.msg_confirm_delivery)
    String msgConfirmDelivery;

    private Unbinder unbinder;
    private MyReceiveExpressageAdapter mAdapter;


    public static MyReceiveExpressageListFragment newInstance() {
        MyReceiveExpressageListFragment myReceiveExpressageListFragment = new MyReceiveExpressageListFragment();
        return myReceiveExpressageListFragment;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    protected void onPageStart() {
        mAdapter.notifyDataSetChanged(ExpressageDao.getExpressageListByLeadType(AppConstants.DB.EXPRESSAGE_LEAD_TYPE_RECEIVE));
    }

    @Override
    protected View onCreatePage(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receive_expressage_list, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        EventPublisher.getInstance().register(this);
        mAdapter = new MyReceiveExpressageAdapter(null);
        listviewReceiveExpresage.setAdapter(mAdapter);
        mAdapter.setOnConfirmFinishedClickListener(this);
        return rootView;
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

    @Override
    public void onConfirmFinishedClick(final String expressageID) {
        CommonDialogFragment.createWithConfirmAndCancelButton(msgConfirmDelivery, new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                fragment.dismiss();
            }
        }, new OnDialogViewClickListener() {
            @Override
            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                fragment.dismiss();
                ExpressageDao.updateExpressageReceiveStatusByExpressageID(expressageID);
                mAdapter.notifyDataSetChanged(ExpressageDao.getExpressageListByLeadType(AppConstants.DB.EXPRESSAGE_LEAD_TYPE_RECEIVE));
                AppreciateDialogFragment dialogFragment = new AppreciateDialogFragment();
                dialogFragment.show(getActivity().getSupportFragmentManager(), "AppreciateDialogFragment");
            }
        }).show(getActivity(), "MyReceiveExpressageListFragment");
    }
}
