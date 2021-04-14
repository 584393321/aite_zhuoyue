package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATApplicationBean;
import com.aliyun.ayland.ui.activity.ATAccessRecordActivity;
import com.aliyun.ayland.ui.activity.ATEnvironmentActivity;
import com.aliyun.ayland.ui.activity.ATEquipmentActivity;
import com.aliyun.ayland.ui.activity.ATFamilyMonitorActivity;
import com.aliyun.ayland.ui.activity.ATFamilySecurityActivity;
import com.aliyun.ayland.ui.activity.ATLinkageActivity;
import com.aliyun.ayland.ui.activity.ATSpaceSubscribeActivity;
import com.aliyun.ayland.ui.activity.ATUserFaceActivity;
import com.aliyun.ayland.ui.activity.ATVehicleCheckActivity;
import com.aliyun.ayland.ui.activity.ATVehiclePassageActivity;
import com.aliyun.ayland.ui.activity.ATVisiteRecordActivity;
import com.aliyun.ayland.ui.activity.ATVisitorRecordActivity;
import com.aliyun.ayland.ui.activity.ATVisualIntercomActivity;
import com.aliyun.ayland.ui.activity.ATWarningNoticeActivity;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ATHomeCardRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATApplicationBean> list = new ArrayList<>();

    public ATHomeCardRVAdapter(Activity context) {
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_home_card, parent, false);
        return new ViewHolder(view);
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

    public void setLists(List<ATApplicationBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mLlContent;
        private TextView mTvName;
        private ImageView mImg;

        private ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mLlContent = (RelativeLayout) itemView.findViewById(R.id.rl_content);
            mTvName = (TextView) itemView.findViewById(R.id.tv_name);
            mImg = (ImageView) itemView.findViewById(R.id.img);
        }

        public void setData(int position) {
            mTvName.setText(list.get(position).getApplicationName());
            Glide.with(mContext).load(list.get(position).getApplicationIcon()).into(mImg);
            mLlContent.setOnClickListener((view)-> {
                switch (list.get(position).getApplicationIdentification()){
                    case "app_park_select":
                        //车位查看
                        mContext.startActivity(new Intent(mContext, ATVehicleCheckActivity.class));
                        break;
                    case "app_my_car":
                        //我家的车
                        mContext.startActivity(new Intent(mContext, ATVehiclePassageActivity.class));
                        break;
                    case "app_visitor_appointment":
                        //访客预约
                        mContext.startActivity(new Intent(mContext, ATVisitorRecordActivity.class));
                        break;
                    case "app_face_access":
                        //人脸通行
                        mContext.startActivity(new Intent(mContext, ATUserFaceActivity.class));
                        break;
                    case "app_access_record":
                        //通行记录
                        mContext.startActivity(new Intent(mContext, ATAccessRecordActivity.class));
                        break;
                    case "app_video_intercom":
                        //可视对讲
                        mContext.startActivity(new Intent(mContext, ATVisualIntercomActivity.class));
                        break;
                    case "app_home_security":
                        //家庭安防
                        mContext.startActivity(new Intent(mContext, ATFamilySecurityActivity.class));
                        break;
                    case "app_public_security":
                        //公区安防
                        break;
                    case "app_scene_linkage":
                        //生活场景
                        mContext.startActivity(new Intent(mContext, ATLinkageActivity.class));
                        break;
                    case "app_wisdom_health":
                        //智慧健康
                        break;
                    case "app_care":
                        //老幼关怀
                        break;
                    case "app_home_monitor":
                        //家庭监控
                        mContext.startActivity(new Intent(mContext, ATFamilyMonitorActivity.class));
                        break;
                    case "app_my_equipment":
                        //我的设备
                        mContext.startActivity(new Intent(mContext, ATEquipmentActivity.class));
                        break;
                    case "app_intelligent_environment":
                        //智慧环境
                        mContext.startActivity(new Intent(mContext, ATEnvironmentActivity.class));
                        break;
                    case "app_community_invite":
                        //社区邀访
                        mContext.startActivity(new Intent(mContext, ATVisiteRecordActivity.class));
                        break;
                    case "app_space_appointment":
                        //空间预约
                        mContext.startActivity(new Intent(mContext, ATSpaceSubscribeActivity.class));
                        break;
                    case "app_alarm_notification":
                        //报警通知
                        mContext.startActivity(new Intent(mContext, ATWarningNoticeActivity.class));
                        break;
                }
            });
        }
    }
}