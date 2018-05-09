package com.wuli.delivery.ui;

import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuli.delivery.R;
import com.wuli.delivery.base.BaseDialogFragment;
import com.wuli.delivery.view.RoundedTextView;

import butterknife.BindView;
import butterknife.Unbinder;

public class AppreciateDialogFragment extends BaseDialogFragment {

    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.layout_delivery_speed)
    LinearLayout layoutDeliverySpeed;
    @BindView(R.id.tv_service_attitude)
    TextView tvServiceAttitude;
    @BindView(R.id.layout_service_attitude)
    LinearLayout layoutServiceAttitude;
    @BindView(R.id.layout_expressage_excellent)
    LinearLayout layoutExpressageExcellent;
    @BindView(R.id.et_appraise)
    EditText etAppraise;
    @BindView(R.id.tv_appraise_info)
    TextView tvAppraiseInfo;
    @BindView(R.id.rtv_confirm)
    RoundedTextView rtvConfirm;
    @BindView(R.id.tv_appreciate_info)
    TextView tvAppreciateInfo;
    @BindView(R.id.rtv_appreciate)
    RoundedTextView rtvAppreciate;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container) {
        View view = inflater.inflate(R.layout.dialog_fragment_appraise, container, false);
        setGravity(Gravity.CENTER);
        return view;
    }
}
