package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATDeviceTslOutputDataType;
import com.aliyun.ayland.interfaces.ATOnRVItemClickListener;
import com.anthouse.wyzhuoyue.R;

import java.util.List;

public class ATLinkageStatusToRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATDeviceTslOutputDataType> list;

    public ATLinkageStatusToRVAdapter(Activity context, List<ATDeviceTslOutputDataType> list) {
        mContext = context;
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_linkage_status, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRlContent;
        private TextView mTvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            mTvName = itemView.findViewById(R.id.tv_name);
        }

        public void setData(int position) {
            mTvName.setText(list.get(position).getName());
            mRlContent.setOnClickListener((view) -> {
                if(lis != null){
                    lis.onItemClick(view, position);
                }
            });
        }
    }

    private ATOnRVItemClickListener lis;

    public void setOnRVClickListener(ATOnRVItemClickListener lis) {
        this.lis = lis;
    }
}
