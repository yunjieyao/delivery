package com.wuli.delivery.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @author ziv
 * @date 2018/5/5
 */

public abstract class BaseAdapter<T, V extends BaseAdapter.ViewHolder> extends android.widget.BaseAdapter {

    private List<T> list;

    public BaseAdapter(List<T> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {

        if (list != null) {
            return list.get(position);
        }

        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        V v = null;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(onCreateLayoutRes(), parent, false);
            v = onCreateViewHolder(convertView);
            convertView.setTag(v);
        } else {
            v = (V) convertView.getTag();
        }

        onBindViewHolder(v, position);

        return convertView;
    }

    public abstract V onCreateViewHolder(View convertView);

    public abstract void onBindViewHolder(V viewHolder, int position);

    public abstract int onCreateLayoutRes();

    public void notifyDataSetChanged(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public abstract static class ViewHolder {
    }
}
