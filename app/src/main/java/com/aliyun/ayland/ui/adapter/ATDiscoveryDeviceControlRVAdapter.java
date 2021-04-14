package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATCategoryBean;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATDiscoveryDeviceControlRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATCategoryBean> list = new ArrayList<>();
    private int select = 0;

    public ATDiscoveryDeviceControlRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_discovery_device_control, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setLists(List<ATCategoryBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private View itemView, view;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            this.itemView = itemView;
            tvName = itemView.findViewById(R.id.tv_name);
            view = itemView.findViewById(R.id.view);
        }

        public void setData(int position) {
            tvName.setText(list.get(position).getCategoryName());
            if (position == select) {
                view.setVisibility(View.VISIBLE);
                itemView.setBackgroundColor(Color.WHITE);
                tvName.setTextColor(mContext.getResources().getColor(R.color._1478C8));
            } else {
                view.setVisibility(View.GONE);
                itemView.setBackgroundColor(Color.TRANSPARENT);
                tvName.setTextColor(mContext.getResources().getColor(R.color._333333));
            }

            itemView.setOnClickListener(view1 -> {
                mOnItemClickListener.onItemClick(position);
                setSelectItem(position);
            });
            itemView.setTag(position);
        }
    }

    private void setSelectItem(int select) {
        this.select = select;
        notifyDataSetChanged();
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}