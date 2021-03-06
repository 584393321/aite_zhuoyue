package com.aliyun.wuye.ui.adapter;

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
import com.aliyun.ayland.data.ATVisitorReservateBean;
import com.anthouse.wyzhuoyue.R;
import com.aliyun.wuye.ui.activity.ATVisitorAppointResultActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ATVisitorAppointRecordRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    ;
    private List<ATVisitorReservateBean> list = new ArrayList<>();

    public ATVisitorAppointRecordRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_wy_rv_visitor_appoint_record, parent, false);
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

    public void setLists(List<ATVisitorReservateBean> list, int pageNum) {
        if (pageNum == 1)
            this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    private boolean getTimeCompareSize(String time) {
        boolean flag = false;
        try {
            flag = sdf.parse(time).getTime() > System.currentTimeMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRlContent;
        private TextView mTvName, mTvPhone, mTvVisiteTime, mTvLeaveTime, mTvTripMode, mTvStartTime, tvOwner, tvAddress;
        private ImageView mImgStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            mTvName = itemView.findViewById(R.id.tv_name);
            mImgStatus = itemView.findViewById(R.id.img_status);
            mTvPhone = itemView.findViewById(R.id.tv_phone);
            mTvVisiteTime = itemView.findViewById(R.id.tv_visite_time);
            mTvStartTime = itemView.findViewById(R.id.tv_start_time);
            mTvLeaveTime = itemView.findViewById(R.id.tv_leave_time);
            mTvTripMode = itemView.findViewById(R.id.tv_trip_mode);
            tvOwner = itemView.findViewById(R.id.tv_owner);
            tvAddress = itemView.findViewById(R.id.tv_address);
        }

        public void setData(int position) {
            tvOwner.setText(String.format(mContext.getString(R.string.at_owner_), list.get(position).getOwnerName()));
            tvAddress.setText(String.format(mContext.getString(R.string.at_visite_address_), list.get(position).getVisitorHouse()));
            mTvName.setText(String.format(mContext.getString(R.string.at_visitor_), list.get(position).getVisitorName()));
            mTvPhone.setText(String.format(mContext.getString(R.string.at_phone_1), list.get(position).getVisitorTel()));
            mTvVisiteTime.setText(String.format(mContext.getString(R.string.at_visit_time_3),
                    list.get(position).getReservationStartTime() + "  ???  " + list.get(position).getReservationEndTime()));
            if (list.get(position).getHasCar() == -1) {
                mTvTripMode.setText(String.format(mContext.getString(R.string.at_trip_mode_), mContext.getString(R.string.at_walk)));
            } else {
                mTvTripMode.setText(String.format(mContext.getString(R.string.at_trip_mode_),
                        mContext.getString(R.string.at_car) + (TextUtils.isEmpty(list.get(position).getCarNumber()) ? "" : list.get(position).getCarNumber())));
            }
            mTvStartTime.setText(String.format(mContext.getString(R.string.at_visit_time_2),
                    TextUtils.isEmpty(list.get(position).getActualStartTime()) ? "???" : list.get(position).getActualStartTime()));
            mTvLeaveTime.setText(String.format(mContext.getString(R.string.at_leave_time_),
                    TextUtils.isEmpty(list.get(position).getActualEndTime()) ? "???" : list.get(position).getActualEndTime()));
            mRlContent.setClickable(false);
            if (getTimeCompareSize(list.get(position).getReservationEndTime())) {
                //?????????
                if (list.get(position).getVisitorStatus() == -1) {
                    //?????????
                    mImgStatus.setImageResource(R.drawable.at_selector_ffffff_cfcfcf);
                    mRlContent.setOnClickListener(view -> {
                        mContext.startActivity(new Intent(mContext, ATVisitorAppointResultActivity.class)
                                .putExtra("id", list.get(position).getId()));
                    });
                } else {
                    //?????????
                    mImgStatus.setImageResource(R.drawable.o_icon_invitevisitors_visited_data);
                }
                tvOwner.setTextColor(mContext.getResources().getColor(R.color._666666));
                tvAddress.setTextColor(mContext.getResources().getColor(R.color._666666));
                mTvName.setTextColor(mContext.getResources().getColor(R.color._666666));
                mTvPhone.setTextColor(mContext.getResources().getColor(R.color._666666));
                mTvVisiteTime.setTextColor(mContext.getResources().getColor(R.color._666666));
                mTvLeaveTime.setTextColor(mContext.getResources().getColor(R.color._666666));
                mTvTripMode.setTextColor(mContext.getResources().getColor(R.color._666666));
                mTvStartTime.setTextColor(mContext.getResources().getColor(R.color._666666));
            } else {
                //?????????
                if (list.get(position).getVisitorStatus() == -1) {
                    //?????????
                    mImgStatus.setImageResource(R.drawable.o_icon_invitevisitors_unvisited_outdata);
                } else {
                    //?????????
                    mImgStatus.setImageResource(R.drawable.o_icon_invitevisitors_visited_outdata);
                }
                tvOwner.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                tvAddress.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvName.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvPhone.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvVisiteTime.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvLeaveTime.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvTripMode.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvStartTime.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
            }
        }
    }
}