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
import com.aliyun.ayland.data.ATMonitorRecordBean;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATAllRecordRVAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<ATMonitorRecordBean> list = new ArrayList<>();

    public ATAllRecordRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_all_record, parent, false);
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

    public void setList(List<ATMonitorRecordBean> mCameraRecordList, int offset) {
        if (offset == 0)
            list.clear();
        list.addAll(mCameraRecordList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvCameraName;
        private TextView mTvCameraStartTime;
        private TextView mTvCameraRecordTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mTvCameraName = itemView.findViewById(R.id.tv_camera_name);
            mTvCameraStartTime = itemView.findViewById(R.id.tv_camera_start_time);
            mTvCameraRecordTime = itemView.findViewById(R.id.tv_camera_record_time);
        }

        public void setData(int position) {
            mTvCameraName.setText(list.get(position).getDeviceName());
            mTvCameraStartTime.setText(list.get(position).getDate() + " " + list.get(position).getCreateTime());
            mTvCameraRecordTime.setText(list.get(position).getDuration());
        }
    }
}