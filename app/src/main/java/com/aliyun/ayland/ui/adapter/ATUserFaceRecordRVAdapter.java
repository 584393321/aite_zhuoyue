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
import com.aliyun.ayland.data.ATAccessRecordHumanBean;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATUserFaceRecordRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATAccessRecordHumanBean> list = new ArrayList<>();

    public ATUserFaceRecordRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_user_face_record, parent, false);
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

    public void setLists(List<ATAccessRecordHumanBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvAddress, mTvTime, mTvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mTvAddress = itemView.findViewById(R.id.tv_address);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvTime = itemView.findViewById(R.id.tv_time);
        }

        public void setData(int position) {
            if (!TextUtils.isEmpty(list.get(position).getAddress()))
                mTvAddress.setText(list.get(position).getAddress());
            else
                mTvAddress.setText("");

            mTvName.setText(list.get(position).getDeviceName());
            mTvTime.setText(list.get(position).getCreateTime());
        }
    }
}