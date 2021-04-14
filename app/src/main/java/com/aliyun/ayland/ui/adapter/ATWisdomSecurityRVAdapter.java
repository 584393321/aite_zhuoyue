package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATFindSafetyApplicationBean;
import com.aliyun.ayland.interfaces.ATOnRecyclerViewItemClickListener;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ATWisdomSecurityRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATFindSafetyApplicationBean> list = new ArrayList<>();

    public ATWisdomSecurityRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_wisdom_security, parent, false);
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

    public void setLists(List<ATFindSafetyApplicationBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRlContent;
        private ImageView imageView;
        private TextView tvName, tvStatus, tvDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            imageView = itemView.findViewById(R.id.imageView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDetail = itemView.findViewById(R.id.tv_detail);
            tvStatus = itemView.findViewById(R.id.tv_status);
        }

        public void setData(int position) {
            tvName.setText(list.get(position).getApplicationName());
            Glide.with(mContext).load(list.get(position).getApplicationIcon()).into(imageView);

            if(list.get(position).getStatus() == 0) {
                tvStatus.setText(R.string.at_have_no_subscribed);
                tvStatus.setTextColor(mContext.getResources().getColor(R.color._999999));
            }else {
                tvStatus.setText(R.string.at_subscribed);
                tvStatus.setTextColor(mContext.getResources().getColor(R.color._1478C8));
            }
            switch (list.get(position).getApplicationIdentification()) {
                case "app_elderly_care_subscribe":
                    //独居老人关怀订阅
                    tvDetail.setText(R.string.at_solitary_elderly_care_subscription_tip);
                    break;
                case "app_go_out_abnormal_subscribe":
                    //出门习惯异常关怀订阅
                    tvDetail.setText(R.string.at_go_out_abnormal_subscribe_tip);
                    break;
                case "app_independent_care_subscribe":
                    //独出社区关怀订阅
                    tvDetail.setText(R.string.at_exclusive_community_care_subscription_tip);
                    break;
            }
            mRlContent.setOnClickListener(view ->
                mOnItemClickListener.onItemClick(view,list.get(position), position));
        }
    }

    private ATOnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(ATOnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}