package com.wuli.delivery.ui.adapters;

import android.view.View;
import android.widget.TextView;

import com.wuli.delivery.R;
import com.wuli.delivery.portal.bean.Expressage;
import com.wuli.delivery.view.RoundedTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ziv
 * @date 2018/5/7
 */

public class MyReceiveExpressageAdapter extends BaseAdapter<Expressage, MyReceiveExpressageAdapter.VH> {

    private OnConfirmFinishedClickListener mListener;

    public void setOnConfirmFinishedClickListener(OnConfirmFinishedClickListener listener) {
        this.mListener = listener;
    }


    public MyReceiveExpressageAdapter(List<Expressage> list) {
        super(list);
    }

    @Override
    public VH onCreateViewHolder(View convertView) {
        return new VH(convertView);
    }

    @Override
    public void onBindViewHolder(VH viewHolder, final int position) {
        Object object = getItem(position);
        if (object != null && object instanceof Expressage) {
            final Expressage expressage = (Expressage) object;
            viewHolder.tvReleaseTime.setText(expressage.getReleaseTime());
            viewHolder.tvDeliveryType.setText(String.format("%s %s：%s %s：%s", expressage.getExpressageType(), "取货号", expressage.getExpressageLeadNum(), "手机尾号", expressage.getDeliveryPhoneNumber().substring(7)));
            viewHolder.tvDeliverySite.setText(String.format("%s：%s", "送达地点", expressage.getDeliverySite()));
            viewHolder.tvDeliveryRemark.setText(String.format("%s：%s", "代领备注", expressage.getExpressageLeadRemark()));
            viewHolder.tvDeliveryTime.setText(String.format("%s：%s", "预计送达时间", expressage.getDeliveryTime()));
            viewHolder.tvDeliveryStatus.setText(expressage.getExpressageStatus());

            boolean isFinished = "1".equals(expressage.getExpressageReceiveStatus()) && "2".equals(expressage.getExpressageLeadType());

            viewHolder.tvConfirmExpressage.setVisibility(isFinished ? View.GONE : View.VISIBLE);
            viewHolder.tvConfirmFinished.setVisibility(isFinished ? View.GONE : View.VISIBLE);
            viewHolder.tvProblemFeedback.setVisibility(isFinished ? View.GONE : View.VISIBLE);
            viewHolder.tvContactExpressageLeader.setVisibility(isFinished ? View.GONE : View.VISIBLE);
            viewHolder.tvCheckAppraise.setVisibility(isFinished ? View.VISIBLE : View.GONE);

            viewHolder.tvConfirmFinished.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onConfirmFinishedClick(expressage.getExpressageID());
                    }
                }
            });

        }
    }

    @Override
    public int onCreateLayoutRes() {
        return R.layout.item_receive_expressage_list;
    }

    static class VH extends BaseAdapter.ViewHolder {

        @BindView(R.id.tv_release_time)
        TextView tvReleaseTime;
        @BindView(R.id.tv_delivery_type)
        TextView tvDeliveryType;
        @BindView(R.id.tv_delivery_site)
        TextView tvDeliverySite;
        @BindView(R.id.tv_delivery_remark)
        TextView tvDeliveryRemark;
        @BindView(R.id.tv_delivery_time)
        TextView tvDeliveryTime;
        @BindView(R.id.tv_expressage_lead_reward)
        TextView tvExpressageLeadReward;
        @BindView(R.id.tv_delivery_status)
        RoundedTextView tvDeliveryStatus;
        @BindView(R.id.tv_confirm_expressage)
        RoundedTextView tvConfirmExpressage;
        @BindView(R.id.tv_confirm_finished)
        RoundedTextView tvConfirmFinished;
        @BindView(R.id.tv_check_appraise)
        RoundedTextView tvCheckAppraise;
        @BindView(R.id.tv_problem_feedback)
        RoundedTextView tvProblemFeedback;
        @BindView(R.id.tv_contact_expressage_leader)
        RoundedTextView tvContactExpressageLeader;

        public VH(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnConfirmFinishedClickListener {
        void onConfirmFinishedClick(String expressageID);
    }
}
