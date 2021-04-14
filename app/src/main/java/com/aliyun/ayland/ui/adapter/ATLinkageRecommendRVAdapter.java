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
import com.aliyun.ayland.data.ATCarBean;
import com.aliyun.ayland.ui.activity.ATLinkageRecommendActivity;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATLinkageRecommendRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATCarBean> list = new ArrayList<>();

    public ATLinkageRecommendRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_linkage_recommend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(position);
    }

    @Override
    public int getItemCount() {
        return 6;
    }

    public void setLists(List<ATCarBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRlContent;
        private TextView mTextView;
        private ImageView img;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            mTextView = itemView.findViewById(R.id.textView);
            img = itemView.findViewById(R.id.img);
        }

        public void setData(int position) {
            if(position == 0){
                img.setImageResource(R.drawable.szwj_ld_bg_a);
                mTextView.setText(R.string.at_home_model);
            }else if(position == 1){
                img.setImageResource(R.drawable.szwj_ld_bg_b);
                mTextView.setText(R.string.at_out_model);
            }else if(position == 2){
                img.setImageResource(R.drawable.szwj_ld_bg_c);
                mTextView.setText(R.string.at_security_model);
            }else if(position == 3){
                img.setImageResource(R.drawable.szwj_ld_bg_d);
                mTextView.setText("环境联动-空调");
            }else if(position == 4){
                img.setImageResource(R.drawable.szwj_ld_bg_e);
                mTextView.setText("舒适环境联动-地暖");
            }else{
                img.setImageResource(R.drawable.szwj_ld_bg_f);
                mTextView.setText("舒适环境联动-窗帘");
            }
            mRlContent.setOnClickListener(view -> mContext.startActivity(new Intent(mContext, ATLinkageRecommendActivity.class).putExtra("position", position)));
        }
    }
}
