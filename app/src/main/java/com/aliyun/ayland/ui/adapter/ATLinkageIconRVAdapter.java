package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ATLinkageIconRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private int select = -1;
    private List<String> list = new ArrayList<>();
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.common_jiazaizhong_b)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ATLinkageIconRVAdapter(Activity context) {
        mContext = context;
    }

    public void setList(List<String> list, int selectPosition) {
        this.list.clear();
        this.list.addAll(list);
        select = selectPosition;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_linkage_icon, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImg, mImgCheck, mImgBackground;
        private RelativeLayout rlContent;

        private ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mImg = itemView.findViewById(R.id.img);
            mImgBackground = itemView.findViewById(R.id.img_background);
            rlContent = itemView.findViewById(R.id.rl_content);
            mImgCheck = itemView.findViewById(R.id.img_check);
        }

        public void setData(int position) {
            if(select == position){
                mImgCheck.setVisibility(View.VISIBLE);
                mImgBackground.setVisibility(View.VISIBLE);
            }else{
                mImgCheck.setVisibility(View.INVISIBLE);
                mImgBackground.setVisibility(View.INVISIBLE);
            }
            Glide.with(mContext).load(list.get(position)).apply(options).into(mImg);

            mImg.setOnClickListener(view -> {
                mOnItemClickListener.onItemClick(view, position);
                notifyItemChanged(select);
                notifyItemChanged(position);
                select = position;
            });
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
