package com.wuli.delivery.ui;

import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.wuli.delivery.R;
import com.wuli.delivery.base.BaseDialogFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class ReleaseExpressageLeadDialogFragment extends BaseDialogFragment {

    @BindView(R.id.iv_release_expressage)
    ImageView ivReleaseExpressage;
    @BindView(R.id.iv_receive_expressage)
    ImageView ivReceiveExpressage;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.dialog_fragment_release_expressage, container, false);
        return view;
    }

    @OnClick({R.id.iv_release_expressage, R.id.iv_receive_expressage})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_receive_expressage:
                Toast.makeText(getActivity(), "求带领", Toast.LENGTH_LONG).show();
                break;
            case R.id.iv_release_expressage:
                Toast.makeText(getActivity(), "帮代领", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
    }

}
