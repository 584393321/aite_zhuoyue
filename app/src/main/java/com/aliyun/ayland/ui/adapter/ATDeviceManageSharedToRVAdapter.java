package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATDeviceManageBean;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATDeviceManageSharedToRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATDeviceManageBean.SharedUsersBean> list = new ArrayList<>();

    public ATDeviceManageSharedToRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_device_manage_share, parent, false);
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

    public void removeItem(int adapterPosition) {
        list.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    public void setList(List<ATDeviceManageBean.SharedUsersBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTime, mTvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mTvTime = itemView.findViewById(R.id.tv_time);
            mTvName = itemView.findViewById(R.id.tv_name);
        }

        public void setData(int position) {
            mTvName.setText(list.get(position).getUsername());
            if (!TextUtils.isEmpty(list.get(position).getShareTime()) && list.get(position).getShareTime().length() > 10)
                mTvTime.setText(String.format(mContext.getString(R.string.at_add_share_), list.get(position).getShareTime().substring(0, 11)));
        }
    }
}