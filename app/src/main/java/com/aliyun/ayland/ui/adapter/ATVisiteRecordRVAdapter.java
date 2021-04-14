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
import com.aliyun.ayland.data.ATVisitorReservateBean;
import com.aliyun.ayland.ui.activity.ATVisiteAppointResultActivity;
import com.anthouse.wyzhuoyue.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ATVisiteRecordRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    ;
    private List<ATVisitorReservateBean> list = new ArrayList<>();

    public ATVisiteRecordRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_visited_record, parent, false);
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
        if (pageNum == 0)
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
        private TextView mTvName, mTvPhone, tvAddress, mTvVisitTime, mTvLeaveTime, mTvTripMode, mTvStartTime;
        private ImageView mImgStatus;

        private ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            mTvName = itemView.findViewById(R.id.tv_name);
            mImgStatus = itemView.findViewById(R.id.img_status);
            mTvTripMode = itemView.findViewById(R.id.tv_trip_mode);
            mTvPhone = itemView.findViewById(R.id.tv_phone);
            tvAddress = itemView.findViewById(R.id.tv_address);
            mTvVisitTime = itemView.findViewById(R.id.tv_visit_time);
            mTvStartTime = itemView.findViewById(R.id.tv_start_time);
            mTvLeaveTime = itemView.findViewById(R.id.tv_leave_time);
        }

        public void setData(int position) {
            mTvName.setText(String.format(mContext.getString(R.string.at_visited_person_), list.get(position).getInviterName()));
            mTvPhone.setText(String.format(mContext.getString(R.string.at_phone_1), list.get(position).getInviterPhone()));
            tvAddress.setText(String.format(mContext.getString(R.string.at_visit_address_), list.get(position).getAddress()));
            mTvVisitTime.setText(String.format(mContext.getString(R.string.at_visit_time_3),
                    list.get(position).getReservationStartTime() + "  至  " + list.get(position).getReservationEndTime()));
            if (list.get(position).getHasCar() == -1) {
                mTvTripMode.setText(String.format(mContext.getString(R.string.at_trip_mode_), mContext.getString(R.string.at_walk)));
            } else {
                mTvTripMode.setText(String.format(mContext.getString(R.string.at_trip_mode_),
                        mContext.getString(R.string.at_car) + list.get(position).getCarNumber()));
            }
            mTvStartTime.setText(String.format(mContext.getString(R.string.at_visit_time_),
                    TextUtils.isEmpty(list.get(position).getReservationStartTime()) ? "无" : list.get(position).getReservationStartTime()));
            mTvLeaveTime.setText(String.format(mContext.getString(R.string.at_leave_time_),
                    TextUtils.isEmpty(list.get(position).getReservationEndTime()) ? "无" : list.get(position).getReservationEndTime()));
            mRlContent.setClickable(false);
            if (getTimeCompareSize(list.get(position).getReservationEndTime())) {
                //未过期
                if (list.get(position).getVisitorStatus() == -1) {
                    //未到访
                    mImgStatus.setImageResource(R.drawable.at_selector_ffffff_cfcfcf);
                    mRlContent.setOnClickListener(view -> {
                        mContext.startActivity(new Intent(mContext, ATVisiteAppointResultActivity.class)
                                .putExtra("id", list.get(position).getId()));
                    });
                } else {
                    //已到访
                    mImgStatus.setImageResource(R.drawable.o_icon_invitevisitors_visited_data);
                }
                mTvName.setTextColor(mContext.getResources().getColor(R.color._666666));
                mTvPhone.setTextColor(mContext.getResources().getColor(R.color._666666));
                tvAddress.setTextColor(mContext.getResources().getColor(R.color._666666));
                mTvVisitTime.setTextColor(mContext.getResources().getColor(R.color._666666));
                mTvLeaveTime.setTextColor(mContext.getResources().getColor(R.color._666666));
                mTvTripMode.setTextColor(mContext.getResources().getColor(R.color._666666));
                mTvStartTime.setTextColor(mContext.getResources().getColor(R.color._666666));
            } else {
                //已过期
                if (list.get(position).getVisitorStatus() == -1) {
                    //未到访
                    mImgStatus.setImageResource(R.drawable.o_icon_invitevisitors_unvisited_outdata);
                } else {
                    //已到访
                    mImgStatus.setImageResource(R.drawable.o_icon_invitevisitors_visited_outdata);
                }
                mTvName.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvPhone.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                tvAddress.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvVisitTime.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvLeaveTime.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvTripMode.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvStartTime.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
            }
        }
    }
}