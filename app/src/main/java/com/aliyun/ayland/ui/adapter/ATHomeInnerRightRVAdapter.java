package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.os.Bundle;
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
import com.aliyun.ayland.ui.activity.ATLinkageDeviceActivity;
import com.aliyun.ayland.ui.activity.ATLinkageStatusActivity;
import com.aliyun.ayland.ui.activity.ATMainActivity;
import com.aliyun.iot.aep.component.router.Router;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import static com.aliyun.ayland.ui.activity.ATLinkageAddActivity.REQUEST_CODE_ADD_CONDITION;

public class ATHomeInnerRightRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATDeviceBean> list = new ArrayList<>();
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.common)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ATHomeInnerRightRVAdapter(Activity context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_home_device, parent, false);
        HomeHolder homeHolder = new HomeHolder(view);
        return homeHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeHolder homeHolder = (HomeHolder) holder;
        homeHolder.setData(position);
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

    public class HomeHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRlContent;
        private TextView mTvDevice;
        private ImageView mImgDevice;
        private ImageView mImgJump;

        public HomeHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            mTvDevice = itemView.findViewById(R.id.tv_device);
            mImgDevice = itemView.findViewById(R.id.img_device);
            mImgJump = itemView.findViewById(R.id.img_jump);
        }

        public void setData(int position) {
            mTvDevice.setText(list.get(position).getProductName());
            Glide.with(mContext)
                    .load(list.get(position).getMyImage())
                    .apply(options)
                    .into(mImgDevice);
            if (mContext instanceof ATLinkageDeviceActivity)
                mImgJump.setVisibility(View.GONE);

            mRlContent.setOnClickListener((view) -> {
                if (mContext instanceof ATMainActivity) {
                    Bundle bundle = new Bundle();
                    bundle.putString("iotId", list.get(position).getIotId());
                    Router.getInstance().toUrl(mContext, "link://router/" + list.get(position).getProductKey(), bundle);
                } else {
                    mContext.startActivityForResult(mContext.getIntent().setClass(mContext, ATLinkageStatusActivity.class)
                            .putExtra("iotId", list.get(position).getIotId())
                            .putExtra("name", TextUtils.isEmpty(list.get(position).getNickName()) ? list.get(position).getProductName() : list.get(position).getNickName())
                            .putExtra("productKey", list.get(position).getProductKey()), REQUEST_CODE_ADD_CONDITION);
                }
            });
        }
    }
}
