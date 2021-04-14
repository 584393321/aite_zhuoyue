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
import com.aliyun.ayland.data.ATShortcutBean;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ATHomeShortcutDeleteRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private boolean nine;
    private List<ATShortcutBean> list = new ArrayList<>();
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.common)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ATHomeShortcutDeleteRVAdapter(Activity context) {
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

    public void setLists(List<ATShortcutBean> list) {
        this.list.clear();
        this.list.addAll(list);
        if(this.list.size() < 9){
            this.list.add(new ATShortcutBean());
            nine = false;
        }else {
            nine = true;
        }
        notifyDataSetChanged();
    }

    public class ShortcutViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvName;
        private RelativeLayout mRlContent;
        private ImageView mImg, mImgDelete;

        private ShortcutViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mImg = itemView.findViewById(R.id.img);
            mImgDelete = itemView.findViewById(R.id.img_add_delete);
            mRlContent = itemView.findViewById(R.id.rl_content);
        }

        public void setData(int position) {
            if(position == list.size() - 1 && !nine){
                mRlContent.setBackground(mContext.getResources().getDrawable(R.drawable.at_shape_3pxeeeeee_xu));
                mImgDelete.setVisibility(View.GONE);
                mImg.setVisibility(View.GONE);
                mTvName.setText("");
            }else {
                mImgDelete.setVisibility(View.VISIBLE);
                mImg.setVisibility(View.VISIBLE);
                mRlContent.setBackground(mContext.getResources().getDrawable(R.drawable.at_selector_f5f5f5_eeeeee));
                mTvName.setText(list.get(position).getItemName());
                if (list.get(position).getShortcutType() == 2)
                    mImg.setImageResource(R.drawable.zhihui_ico_liandong);
                else
                    Glide.with(mContext).load(list.get(position).getItemIcon()).apply(options).into(mImg);
                mImgDelete.setImageResource(R.drawable.common_icon_delete_n);
                mImgDelete.setOnClickListener(view -> {
                    list.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());
                    mOnItemClickListener.onItemClick(view, getLayoutPosition());
                });
            }
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
