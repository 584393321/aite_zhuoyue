package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATFindFamilyMemberForOutAloneBean;
import com.aliyun.ayland.interfaces.ATOnRecyclerViewItemClickListener;
import com.aliyun.ayland.widget.ATSwitchButton;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATWisdomSecurityOutAloneMemberRVAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private int status = 0;
    private List<ATFindFamilyMemberForOutAloneBean.MembersBean> list = new ArrayList<>();

    public ATWisdomSecurityOutAloneMemberRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_wisdom_security_out_alone_member, parent, false);
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

    public void setList(List<ATFindFamilyMemberForOutAloneBean.MembersBean> list, int status) {
        this.list.clear();
        this.list.addAll(list);
        this.status = status;
        notifyDataSetChanged();
    }

    public void setStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvState;
        private ATSwitchButton switchButton;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvState = itemView.findViewById(R.id.tv_state);
            switchButton = itemView.findViewById(R.id.switchButton);
        }

        public void setData(int position) {
            tvName.setText(list.get(position).getLastname() + list.get(position).getFirstname());
            if (status == 1) {
                tvState.setVisibility(View.VISIBLE);
                switchButton.setVisibility(View.GONE);
                tvState.setText(list.get(position).getStatus() == 0 ? R.string.at_not_open : R.string.at_has_been_open);
            } else {
                tvState.setVisibility(View.GONE);
                switchButton.setVisibility(View.VISIBLE);
                switchButton.setChecked(list.get(position).getStatus() == 1);
            }
            switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> mOnItemClickListener.onItemClick(buttonView, isChecked ? 1 : 0, position));
        }
    }

    private ATOnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(ATOnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}