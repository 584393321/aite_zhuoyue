package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATDeviceBean;
import com.aliyun.ayland.ui.activity.ATIntelligentMonitorActivity;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ATFamilyMonitorRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATDeviceBean> list = new ArrayList<>();
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.zhihui_ico_common)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ATFamilyMonitorRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_family_monitor, parent, false);
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

    public void setLists(List<ATDeviceBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRlContent;
        private TextView mTvName, mTvOffLine;
        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            img = itemView.findViewById(R.id.img);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvOffLine = itemView.findViewById(R.id.tv_off_line);
        }

        public void setData(int position) {
            if (list.get(position).getStatus() != 1) {
                mTvOffLine.setVisibility(View.VISIBLE);
            } else {
                mTvOffLine.setVisibility(View.GONE);
            }
            mTvName.setText(TextUtils.isEmpty(list.get(position).getNickName())
                    ? list.get(position).getProductName() : list.get(position).getNickName());
            Glide.with(mContext)
                    .load(list.get(position).getMyImage())
                    .apply(options)
                    .into(img);
            mRlContent.setOnClickListener(view -> mContext.startActivity(new Intent(mContext, ATIntelligentMonitorActivity.class)
                    .putExtra("productKey", list.get(position).getProductKey())
                    .putExtra("iotId", list.get(position).getIotId())));
        }
    }
}
