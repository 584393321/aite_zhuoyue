package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATApplicationBean;
import com.aliyun.ayland.interfaces.ATOnRecyclerViewItemClickListener;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ATWisdomPublicAreaRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATApplicationBean> list = new ArrayList<>();
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.common)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ATWisdomPublicAreaRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_wisdom_public_area, parent, false);
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

    public void setLists(List<ATApplicationBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLlContent;
        private ImageView mImg;
        private TextView mTvName;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mLlContent = itemView.findViewById(R.id.ll_content);
            mImg = itemView.findViewById(R.id.img);
            mTvName = itemView.findViewById(R.id.textView);
        }

        public void setData(int position) {
            mTvName.setText(list.get(position).getApplicationName());
            Glide.with(mContext).load(list.get(position).getApplicationIcon()).apply(options).into(mImg);
            mLlContent.setOnClickListener(view ->
                mOnItemClickListener.onItemClick(view,list.get(position).getApplicationIdentification(), position));
        }
    }

    private ATOnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(ATOnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}