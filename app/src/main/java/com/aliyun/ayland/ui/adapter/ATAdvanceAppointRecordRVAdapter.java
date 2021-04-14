package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATVisitorReservateBean;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATAdvanceAppointRecordRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATVisitorReservateBean> list = new ArrayList<>();

    public ATAdvanceAppointRecordRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_advance_appoint_record, parent, false);
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvName, tvCreateTime, mTvPhone, tvVisiteAddress, mTvVisiteTime, mTvTripMode;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            tvCreateTime = itemView.findViewById(R.id.tv_create_time);
            mTvPhone = itemView.findViewById(R.id.tv_phone);
            tvVisiteAddress = itemView.findViewById(R.id.tv_visite_address);
            mTvVisiteTime = itemView.findViewById(R.id.tv_visite_time);
            mTvTripMode = itemView.findViewById(R.id.tv_trip_mode);
        }

        public void setData(int position) {
            mTvName.setText(String.format(mContext.getString(R.string.at_visitor_), TextUtils.isEmpty(list.get(position).getVisitorName())
                    ? mContext.getString(R.string.at_do_no_fill_in_yet) : list.get(position).getVisitorName()));
            tvCreateTime.setText(String.format(mContext.getString(R.string.at_create_time_), TextUtils.isEmpty(list.get(position).getCreateTime())
                    ? mContext.getString(R.string.at_do_no_fill_in_yet) : list.get(position).getCreateTime()));
            mTvPhone.setText(String.format(mContext.getString(R.string.at_phone_1), TextUtils.isEmpty(list.get(position).getVisitorTel())
                    ? mContext.getString(R.string.at_do_no_fill_in_yet) : list.get(position).getVisitorTel()));
            tvVisiteAddress.setText(String.format(mContext.getString(R.string.at_visite_address_), TextUtils.isEmpty(list.get(position).getVisitorHouse())
                    ? mContext.getString(R.string.at_do_no_fill_in_yet) : list.get(position).getVisitorHouse()));
            mTvVisiteTime.setText(String.format(mContext.getString(R.string.at_visit_time_3),
                    list.get(position).getReservationStartTime() + "  至  " + list.get(position).getReservationEndTime()));
            if (list.get(position).getHasCar() == -1) {
                mTvTripMode.setText(String.format(mContext.getString(R.string.at_trip_mode_), mContext.getString(R.string.at_walk)));
            } else {
                mTvTripMode.setText(String.format(mContext.getString(R.string.at_trip_mode_),
                        mContext.getString(R.string.at_car) + list.get(position).getCarNumber()));
            }
        }
    }
}