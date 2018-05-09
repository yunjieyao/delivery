package com.wuli.delivery.ui.adapters;

import android.view.View;
import android.widget.TextView;

import com.wuli.delivery.R;
import com.wuli.delivery.portal.bean.Expressage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ziv on 2018/5/6.
 */

public class MyExpressageListAdapter extends BaseAdapter<Expressage, MyExpressageListAdapter.VH> {

    public MyExpressageListAdapter(List<Expressage> list) {
        super(list);
    }

    @Override
    public VH onCreateViewHolder(View convertView) {
        return new VH(convertView);
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        Object object = getItem(position);
        if (object != null && object instanceof Expressage) {
            Expressage expressage = (Expressage) object;
            viewHolder.tvExpressageNumber.setText(String.format("%s：%s", "取货号", expressage.getExpressageLeadNum()));
            viewHolder.tvExpressageType.setText(String.format("%s %s", expressage.getExpressageType(), expressage.getExpressageDesc()));
            viewHolder.tvExpressageRemark.setText(String.format("%s：%s", "备注", expressage.getExpressageLeadRemark()));
        }
    }

    @Override
    public int onCreateLayoutRes() {
        return R.layout.item_expressage_list;
    }

    public static class VH extends BaseAdapter.ViewHolder {

        @BindView(R.id.tv_expressage_type)
        TextView tvExpressageType;
        @BindView(R.id.tv_expressage_number)
        TextView tvExpressageNumber;
        @BindView(R.id.tv_expressage_remark)
        TextView tvExpressageRemark;


        public VH(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
