package com.wuli.delivery.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wuli.delivery.R;
import com.wuli.delivery.view.ListViewForScrollView;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.Unbinder;


public class LeftContentFragment extends Fragment {

    @BindView(R.id.iv_qrcode)
    ImageView ivQrcode;
    @BindView(R.id.listview_left_content)
    ListViewForScrollView listviewLeftContent;
    @BindView(R.id.btn_exit)
    TextView btnExit;
    @BindArray(R.array.left_content_items)
    String[] leftContentItems;

    private Unbinder unbinder;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_left_content, container, false);
            unbinder = ButterKnife.bind(this, rootView);
            initData();
        }
        return rootView;
    }

    private void initData() {
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, leftContentItems);
        listviewLeftContent.setAdapter(arrayAdapter);
    }

    @OnClick({R.id.iv_qrcode, R.id.btn_exit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_qrcode:
                break;
            case R.id.btn_exit:
                System.exit(0);
                break;
            default:
                break;
        }
    }

    @OnItemClick({R.id.listview_left_content})
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getActivity(), leftContentItems[position] + "暂未开放，敬请期待!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
