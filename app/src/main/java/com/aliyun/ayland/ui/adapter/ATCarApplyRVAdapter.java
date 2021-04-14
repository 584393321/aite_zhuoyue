package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATCarListBean;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATCarApplyRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATCarListBean> list = new ArrayList<>();

    public ATCarApplyRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_car_apply, parent, false);
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

    public void setLists(List<ATCarListBean> list, int pageNo) {
        if (pageNo == 1)
            this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        public void setData(int position) {
            tvName.setText(list.get(position).getPlateNumber());
            switch (list.get(position).getStatus()){
                case 0:
                    tvStatus.setText(mContext.getString(R.string.at_pass_check));
                    tvStatus.setTextColor(mContext.getResources().getColor(R.color._FDA448));
                    break;
                case 1:
                    tvStatus.setText(mContext.getString(R.string.at_pass));
                    tvStatus.setTextColor(mContext.getResources().getColor(R.color._4181EB));
                    break;
                case 2:
                    tvStatus.setText(mContext.getString(R.string.at_pass_refuse));
                    tvStatus.setTextColor(mContext.getResources().getColor(R.color._FDA448));
                    break;
            }
        }
    }
}