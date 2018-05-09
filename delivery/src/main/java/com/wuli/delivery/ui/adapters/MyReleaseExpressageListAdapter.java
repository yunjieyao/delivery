package com.wuli.delivery.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wuli.delivery.R;
import com.wuli.delivery.portal.bean.Expressage;
import com.wuli.delivery.view.RoundedTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ziv
 * @date 2018/5/6
 */

public class MyReleaseExpressageListAdapter extends android.widget.BaseAdapter {


    private final static int ITEM_TYPE_WAITTING_RECEIVED = 0;
    private final static int ITEM_TYPE_RECEIVED = 1;
    private final static int ITEM_TYPE_FINISHED = 2;


    private List<Expressage> expressageList;

    public MyReleaseExpressageListAdapter(List<Expressage> expressageList) {
        this.expressageList = expressageList;
    }

    public void notifyDataSetChanged(List<Expressage> expressageList) {
        this.expressageList = expressageList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (expressageList == null) {
            return 0;
        }
        return expressageList.size();
    }

    @Override
    public Object getItem(int position) {
        if (expressageList == null) {
            return null;
        }
        return expressageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BaseViewHolder baseViewHolder;

        if (convertView == null) {
            int itemType = getItemViewType(position);
            convertView = getConvertView(itemType, parent);
            baseViewHolder = getViewHolder(itemType, convertView);
            convertView.setTag(baseViewHolder);
        } else {
            baseViewHolder = (BaseViewHolder) convertView.getTag();
        }

        Expressage expressage = expressageList.get(position);
        String[] leadTypeList = parent.getContext().getResources().getStringArray(R.array.expressage_release_status_list);
        int expressageStatus = Integer.valueOf(expressage.getExpressageReleaseStatus());

        if (baseViewHolder instanceof WaittingReceivedViewholder) {
            WaittingReceivedViewholder viewholder = (WaittingReceivedViewholder) baseViewHolder;
            viewholder.tvWaitDeliveryRemark.setText(String.format("%s：%s", "代领备注", expressage.getExpressageLeadRemark()));
            viewholder.tvWaitDeliveryTime.setText(expressage.getDeliveryTime());
            viewholder.tvWaitDeliveryStatus.setText(leadTypeList[expressageStatus]);
            viewholder.tvWaitDeliveryNumber.setText(String.format("%s：%s", "取货号", expressage.getExpressageLeadNum()));
            viewholder.tvWaitDeliveryType.setText(String.format("%s %s", expressage.getExpressageType(), expressage.getExpressageDesc()));
            viewholder.tvWaitExpressageLeadReward.setText(String.format("%s：%s元", "酬金", expressage.getExpressageLeadReward()));
        } else if (baseViewHolder instanceof FinshedViewholder) {
            FinshedViewholder viewholder = (FinshedViewholder) baseViewHolder;
            viewholder.tvFinishedDeliveryTime.setText(expressage.getDeliveryTime());
            viewholder.tvFinishedDeliveryStatus.setText(leadTypeList[expressageStatus]);
            viewholder.tvFinishedDeliveryType.setText(String.format("%s %s", expressage.getExpressageType(), expressage.getExpressageDesc()));
            viewholder.tvFinishedExpressageLeadReward.setText(String.format("%s：%s元", "酬金", expressage.getExpressageLeadReward()));
        } else {
            ReceivedViewholder viewholder = (ReceivedViewholder) baseViewHolder;
            viewholder.tvReceivedDeliveryRemark.setText(String.format("%s：%s", "代领备注", expressage.getExpressageLeadRemark()));
            viewholder.tvReceivedDeliveryTime.setText(expressage.getDeliveryTime());
            viewholder.tvReceivedDeliveryStatus.setText(leadTypeList[expressageStatus]);
            viewholder.tvReceivedDeliveryNumber.setText(String.format("%s：%s", "取货号", expressage.getExpressageLeadNum()));
            viewholder.tvReceivedDeliveryType.setText(String.format("%s %s", expressage.getExpressageType(), expressage.getExpressageDesc()));
            viewholder.tvReceivedExpressageLeadReward.setText(String.format("%s：%s元", "酬金", expressage.getExpressageLeadReward()));
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {

        if (expressageList != null) {
            return Integer.valueOf(expressageList.get(position).getExpressageReleaseStatus());
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    private View getConvertView(int type, ViewGroup parent) {
        if (type == ITEM_TYPE_WAITTING_RECEIVED) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_release_wait_received_expressage_list, parent, false);
        } else if (type == ITEM_TYPE_RECEIVED) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_release_received_expressage_list, parent, false);
        } else {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_release_finished_expressage_list, parent, false);
        }
    }

    private BaseViewHolder getViewHolder(int type, View convertView) {
        if (type == ITEM_TYPE_WAITTING_RECEIVED) {
            return new WaittingReceivedViewholder(convertView);
        } else if (type == ITEM_TYPE_RECEIVED) {
            return new ReceivedViewholder(convertView);
        } else {
            return new FinshedViewholder(convertView);
        }
    }

    static class WaittingReceivedViewholder implements BaseViewHolder {

        @BindView(R.id.tv_wait_delivery_time)
        TextView tvWaitDeliveryTime;
        @BindView(R.id.tv_wait_delivery_type)
        TextView tvWaitDeliveryType;
        @BindView(R.id.tv_wait_delivery_number)
        TextView tvWaitDeliveryNumber;
        @BindView(R.id.tv_wait_delivery_remark)
        TextView tvWaitDeliveryRemark;
        @BindView(R.id.tv_wait_delivery_status)
        RoundedTextView tvWaitDeliveryStatus;
        @BindView(R.id.tv_cancel_release)
        RoundedTextView tvCancelRelease;
        @BindView(R.id.tv_edit_content)
        RoundedTextView tvEditContent;
        @BindView(R.id.tv_wait_expressage_lead_reward)
        TextView tvWaitExpressageLeadReward;

        public WaittingReceivedViewholder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class FinshedViewholder implements BaseViewHolder {

        @BindView(R.id.tv_finished_delivery_time)
        TextView tvFinishedDeliveryTime;
        @BindView(R.id.tv_finished_delivery_type)
        TextView tvFinishedDeliveryType;
        @BindView(R.id.tv_complaint)
        RoundedTextView tvComplaint;
        @BindView(R.id.tv_appraise)
        RoundedTextView tvAppraise;
        @BindView(R.id.tv_finished_delivery_status)
        RoundedTextView tvFinishedDeliveryStatus;
        @BindView(R.id.tv_finished_expressage_lead_reward)
        TextView tvFinishedExpressageLeadReward;

        public FinshedViewholder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    static class ReceivedViewholder implements BaseViewHolder {

        @BindView(R.id.tv_received_delivery_time)
        TextView tvReceivedDeliveryTime;
        @BindView(R.id.tv_received_delivery_type)
        TextView tvReceivedDeliveryType;
        @BindView(R.id.tv_received_delivery_number)
        TextView tvReceivedDeliveryNumber;
        @BindView(R.id.tv_received_delivery_remark)
        TextView tvReceivedDeliveryRemark;
        @BindView(R.id.tv_received_expressage_lead_reward)
        TextView tvReceivedExpressageLeadReward;
        @BindView(R.id.tv_received_delivery_status)
        RoundedTextView tvReceivedDeliveryStatus;
        @BindView(R.id.tv_check_expressage_lead_info)
        RoundedTextView tvCheckExpressageLeadInfo;
        @BindView(R.id.tv_check_expressage_lead_person)
        RoundedTextView tvCheckExpressageLeadPerson;

        public ReceivedViewholder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    interface BaseViewHolder {

    }
}
