package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATCategory;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.utils.ATAddDeviceScanHelper;
import com.aliyun.ayland.utils.ATResourceUtils;
import com.aliyun.iot.aep.component.router.Router;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ATDiscoveryDeviceRightRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATCategory> list = new ArrayList<>();
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.zhihui_ico_common)
            .error(R.drawable.zhihui_ico_common);

    public ATDiscoveryDeviceRightRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_discovery_device_right, parent, false);
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

    public void setLists(List<ATCategory> list, int pageNo) {
        if (pageNo == 1) {
            this.list.clear();
        }
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRlContent;
        private TextView mTvDevice;
        private ImageView mImgDevice;
        private ImageView mImgJump;

        private ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            mTvDevice = itemView.findViewById(R.id.tv_device);
            mImgDevice = itemView.findViewById(R.id.img_device);
            mImgJump = itemView.findViewById(R.id.img_jump);
        }

        public void setData(int position) {
            mTvDevice.setText(list.get(position).getProductName());
            if (ATResourceUtils.getResIdByName(list.get(position).getCategoryKey().toLowerCase(), ATResourceUtils.ResourceType.DRAWABLE) != 0) {
                mImgDevice.setImageResource(ATResourceUtils.getResIdByName(list.get(position).getCategoryKey().toLowerCase(), ATResourceUtils.ResourceType.DRAWABLE));
            } else {
                Glide.with(mContext)
                        .load(list.get(position).getCategoryUrl())
                        .apply(options)
                        .into(mImgDevice);
            }
            mRlContent.setOnClickListener((view) -> {
                Bundle bundle = new Bundle();
                bundle.putString("productKey", list.get(position).getProductKey());
//                bundle.putString("deviceName", list.get(position).getProductName());
                Router.getInstance().toUrlForResult(mContext, ATConstants.RouterUrl.PLUGIN_ID_DEVICE_CONFIG,
                        ATAddDeviceScanHelper.REQUEST_CODE_CONFIG_WIFI, bundle);
            });
        }
    }
}