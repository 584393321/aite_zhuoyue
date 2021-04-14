package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATPublicDeviceBean;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ATHomeShortcutCommunityRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATPublicDeviceBean> list = new ArrayList<>();

    public ATHomeShortcutCommunityRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_shortcut, parent, false);
        return new ShortcutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ShortcutViewHolder shortcutViewHolder = (ShortcutViewHolder) holder;
        shortcutViewHolder.setData(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setLists(List<ATPublicDeviceBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ShortcutViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvName;
        private ImageView mImg, mImgAdd;

        private ShortcutViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mImg = itemView.findViewById(R.id.img);
            mImgAdd = itemView.findViewById(R.id.img_add_delete);
        }

        public void setData(int position) {
            mTvName.setText(list.get(position).getName());
            Glide.with(mContext).load(list.get(position).getImageUrl()).into(mImg);
            mImgAdd.setImageResource(list.get(position).isAdd() ? R.drawable.common_icon_delete_n : R.drawable.common_icon_add_n);
            mImgAdd.setOnClickListener(view -> {
                list.get(position).setAdd(!list.get(position).isAdd());
                notifyItemChanged(position);
                mOnItemClickListener.onItemClick(list.get(position).isAdd(), position);
            });
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(boolean add, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
