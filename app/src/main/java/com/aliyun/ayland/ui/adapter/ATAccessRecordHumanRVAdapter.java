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
import com.aliyun.ayland.data.ATAccessRecordHumanBean;
import com.aliyun.ayland.utils.ATResourceUtils;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATAccessRecordHumanRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATAccessRecordHumanBean> list = new ArrayList<>();

    public ATAccessRecordHumanRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_access_record, parent, false);
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

    public void setLists(List<ATAccessRecordHumanBean> list, int page) {
        if(page == 0){
            this.list.clear();
        }
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRlContent;
        private TextView tvLocation, mTvTime, tvWay, mTvName, mTvInOut;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            tvLocation = itemView.findViewById(R.id.tv_location);
            mTvTime = itemView.findViewById(R.id.tv_time);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvInOut = itemView.findViewById(R.id.tv_in_out);
            tvWay = itemView.findViewById(R.id.tv_way);
        }

        public void setData(int position) {
            tvLocation.setText(list.get(position).getDeviceName());
            mTvName.setText(list.get(position).getUserName());
            mTvTime.setText(list.get(position).getCreateTime());
            tvWay.setText(ATResourceUtils.getResIdByName(list.get(position).getMediaType().toLowerCase(), ATResourceUtils.ResourceType.STRING));
            if (list.get(position).getAccessType() == 1) {
                mTvInOut.setText("出");
            } else {
                mTvInOut.setText("进");
            }
        }
    }
}
