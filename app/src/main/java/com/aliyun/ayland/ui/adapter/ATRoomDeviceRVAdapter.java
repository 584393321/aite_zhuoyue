package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATDeviceBean;
import com.aliyun.ayland.interfaces.ATOnBindRVItemClickListener;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ATRoomDeviceRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private boolean bind;
    private List<ATDeviceBean> list = new ArrayList<>();
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.common)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ATRoomDeviceRVAdapter(Activity context, boolean bind) {
        mContext = context;
        this.bind = bind;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_room_device, parent, false);
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

    public void setLists(List<ATDeviceBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvName;
        private ImageView mImgDelete, mImgAdd, mImgDevice;
        private LinearLayout mLlRight;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mImgDevice = itemView.findViewById(R.id.img_device);
            mImgDelete = itemView.findViewById(R.id.img_delete);
            mImgAdd = itemView.findViewById(R.id.img_add);
            mLlRight = itemView.findViewById(R.id.ll_right);
            mImgDelete.setVisibility(bind ? View.GONE : View.VISIBLE);
            mImgAdd.setVisibility(bind ? View.VISIBLE : View.GONE);
        }

        public void setData(int position) {
            mTvName.setText(list.get(position).getProductName());
            mLlRight.setOnClickListener((view) -> {
                if (lis != null) {
                    lis.onItemClick(view, list.get(position).getIotId(), position);
                }
            });
            Glide.with(mContext)
                    .load(list.get(position).getMyImage())
                    .apply(options)
                    .into(mImgDevice);
        }
    }

    protected ATOnBindRVItemClickListener lis;

    public void setOnRVClickListener(ATOnBindRVItemClickListener lis) {
        this.lis = lis;
    }
}
