package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATApplicationBean;
import com.aliyun.ayland.ui.activity.ATEquipmentActivity;
import com.aliyun.ayland.ui.activity.ATFamilySecurityActivity;
import com.aliyun.ayland.ui.activity.ATFamilySecurityActivity1;
import com.aliyun.ayland.ui.activity.ATLinkageActivity;
import com.aliyun.ayland.ui.activity.ATWisdomPublicAreaActivity;
import com.aliyun.ayland.ui.activity.ATWisdomSecurityActivity;
import com.aliyun.ayland.utils.ATResourceUtils;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ATHomeBottomRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATApplicationBean> list = new ArrayList<>();
//    private RequestOptions options = new RequestOptions()
//            .placeholder(R.drawable.szwj_ld_bg_a)
//            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ATHomeBottomRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_home_bottom, parent, false);
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
        private RelativeLayout mRlContent;
        private TextView mTvName, mTvName1, mTvDetail, mTvDetail1;
        private ImageView mImg;

        private ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvDetail = itemView.findViewById(R.id.tv_detail);
            mTvDetail1 = itemView.findViewById(R.id.tv_detail1);
            mTvName1 = itemView.findViewById(R.id.tv_name1);
            mImg = itemView.findViewById(R.id.img);
        }

        public void setData(int position) {
            if(position % 2 == 0){
                mTvName.setText(list.get(position).getApplicationName());
                mTvDetail.setText(ATResourceUtils.getResIdByName(String.format(mContext.getString(R.string.at_applicationIdentification_), list.get(position).getApplicationIdentification())
                        , ATResourceUtils.ResourceType.STRING));
                mTvName1.setText("");
                mTvDetail1.setText("");
            }else {
                mTvName.setText("");
                mTvDetail.setText("");
                mTvName1.setText(list.get(position).getApplicationName());
                mTvDetail1.setText(ATResourceUtils.getResIdByName(String.format(mContext.getString(R.string.at_applicationIdentification_), list.get(position).getApplicationIdentification())
                        , ATResourceUtils.ResourceType.STRING));
            }
            Glide.with(mContext).load(list.get(position).getApplicationIcon()).into(mImg);
            mRlContent.setOnClickListener((view) -> {
                switch (list.get(position).getApplicationIdentification()) {
                    case "app_intelligent_home":
                        //智能家居
                        mContext.startActivity(new Intent(mContext, ATEquipmentActivity.class));
                        break;
                    case "app_intelligent_linkage":
                        //智慧联动
                        mContext.startActivity(new Intent(mContext, ATLinkageActivity.class));
                        break;
                    case "app_intelligent_public":
                        //智慧公区
                        mContext.startActivity(new Intent(mContext, ATWisdomPublicAreaActivity.class));
                        break;
                    case "app_intelligent_security":
                        //智慧安防
                        mContext.startActivity(new Intent(mContext, ATFamilySecurityActivity1.class));
                        break;
                }
            });
        }
    }
}