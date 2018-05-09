package com.wuli.delivery.ui.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wuli.delivery.R;
import com.wuli.delivery.portal.bean.Expressage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * @author ziv
 * @date 2018/4/26
 */

public class HomeExpressageListAdapter extends BaseAdapter<Expressage, HomeExpressageListAdapter.HomeExpressageListViewHolder> {

    public HomeExpressageListAdapter(List<Expressage> list) {
        super(list);
    }

    @Override
    public HomeExpressageListViewHolder onCreateViewHolder(View convertView) {
        return new HomeExpressageListViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(HomeExpressageListViewHolder viewHolder, int position) {
        Object object = getItem(position);

        if (object != null && object instanceof Expressage) {
            Expressage expressage = (Expressage) object;
            viewHolder.tvDeliveryTypeAndSite.setText(String.format("%s %s", expressage.getExpressageType(), expressage.getDeliverySite()));
            viewHolder.tvExpressageLeadReward.setText(String.format("%s元", expressage.getExpressageLeadReward()));
            viewHolder.tvExpressageRemark.setText(expressage.getExpressageLeadRemark());

        }
    }

    @Override
    public int onCreateLayoutRes() {
        return R.layout.item_home_expressage_list;
    }

    public static class HomeExpressageListViewHolder extends BaseAdapter.ViewHolder {

        @BindView(R.id.iv_user_head)
        ImageView ivUserHead;
        @BindView(R.id.tv_expressage_lead_reward)
        TextView tvExpressageLeadReward;
        @BindView(R.id.tv_delivery_type_and_site)
        TextView tvDeliveryTypeAndSite;
        @BindView(R.id.tv_expressage_remark)
        TextView tvExpressageRemark;

        public HomeExpressageListViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
