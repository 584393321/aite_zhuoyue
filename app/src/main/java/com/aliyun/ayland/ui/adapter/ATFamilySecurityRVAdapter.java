package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATPublicCameraBean;
import com.aliyun.ayland.data.ATPublicSecurityCameraBean;
import com.aliyun.ayland.interfaces.ATOnIntegerClickListener;
import com.aliyun.ayland.interfaces.ATOnRecyclerViewItemClickListener;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ATFamilySecurityRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private Drawable drawableJump;
    private List<ATPublicSecurityCameraBean> list = new ArrayList<>();
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.common)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ATFamilySecurityRVAdapter(Activity context) {
        mContext = context;
        drawableJump = context.getResources().getDrawable(R.drawable.bjsh_btn_xiaotiaozhuan_a);
        drawableJump.setBounds(0, 0, drawableJump.getIntrinsicWidth(), drawableJump.getIntrinsicHeight());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_family_security, parent, false);
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

    public void setLists(List<ATPublicSecurityCameraBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mLlContent;
        private TextView mTextView;
        private ImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mLlContent = itemView.findViewById(R.id.ll_content);
            mImageView = itemView.findViewById(R.id.img);
            mTextView = itemView.findViewById(R.id.textView);
        }

        public void setData(int position) {
            mTextView.setText(list.get(position).getName());
            mLlContent.setOnClickListener(view ->
                    mOnItemClickListener.onItemClick(view, list.get(position), position));
        }
    }

    private ATOnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(ATOnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}