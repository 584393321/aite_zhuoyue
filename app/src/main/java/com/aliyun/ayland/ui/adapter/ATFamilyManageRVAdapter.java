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
import com.aliyun.ayland.data.ATFamilyMemberBean;
import com.aliyun.ayland.ui.activity.ATFamilyManageMemberActivity;
import com.aliyun.ayland.utils.ATResourceUtils;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ATFamilyManageRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private boolean ifAdmin;
    private List<ATFamilyMemberBean> list = new ArrayList<>();
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.ico_touxiang_mr)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ATFamilyManageRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_family_manage, parent, false);
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

    public void setLists(List<ATFamilyMemberBean> list, boolean ifAdmin) {
        this.list.clear();
        this.list.addAll(list);
        this.ifAdmin = ifAdmin;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRlContent;
        private ImageView imageView;
        private TextView mTvName, mTvStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvStatus = itemView.findViewById(R.id.tv_status);
            imageView = itemView.findViewById(R.id.imageView);
        }

        public void setData(int position) {
//            Glide.with(mContext).load(list.get(position).getAvatarUrl()).apply(options).into(imageView);
            mTvName.setText(list.get(position).getNickname());
            if (TextUtils.isEmpty(list.get(position).getHouseholdtype()))
                mTvStatus.setText(R.string.at_other);
            else
                mTvStatus.setText(ATResourceUtils.getResIdByName(String.format(mContext.getString(R.string.at_householdtype_), list.get(position).getHouseholdtype()), ATResourceUtils.ResourceType.STRING));
            mRlContent.setOnClickListener(view -> mContext.startActivity(new Intent(mContext, ATFamilyManageMemberActivity.class)
                    .putExtra("FamilyMemberBean", list.get(position))
                    .putExtra("ifAdmin", ifAdmin)));
        }
    }
}