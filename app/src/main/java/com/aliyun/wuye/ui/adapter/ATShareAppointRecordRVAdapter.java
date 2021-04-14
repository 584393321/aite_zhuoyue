package com.aliyun.wuye.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATShareAppointRecordBean;
import com.aliyun.ayland.interfaces.ATOnRVItemIntegerClickListener;
import com.aliyun.ayland.utils.ATResourceUtils;
import com.anthouse.wyzhuoyue.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ATShareAppointRecordRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATShareAppointRecordBean> list = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("#####0.00");

    public ATShareAppointRecordRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_wy_rv_share_appoint_record, parent, false);
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

    public void setLists(List<ATShareAppointRecordBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSpaceName, tvSubject, tvTime, tvPrice, tvStatus, tvPayStatus, tvUser, tvInter, tvOut, tvPay, tvCancel;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            tvSpaceName = itemView.findViewById(R.id.tv_space_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvPayStatus = itemView.findViewById(R.id.tv_pay_status);
            tvUser = itemView.findViewById(R.id.tv_user);
            tvInter = itemView.findViewById(R.id.tv_inter);
            tvOut = itemView.findViewById(R.id.tv_out);
            tvPay = itemView.findViewById(R.id.tv_pay);
            tvCancel = itemView.findViewById(R.id.tv_cancel);
        }

        public void setData(int position) {
            tvUser.setText(list.get(position).getPersonName());
            tvSpaceName.setText(list.get(position).getSpaceName());

            StringBuilder projectName = new StringBuilder();
            for (String s : list.get(position).getProjectName()) {
                projectName.append(s).append("，");
            }
            projectName = new StringBuilder(projectName.substring(0, projectName.length() - 1));
            tvSubject.setText(projectName.toString());

            StringBuilder appointmentTime = new StringBuilder();
            appointmentTime.append(String.format("%s\r", list.get(position).getAppointmentDay()));
            for (String s : list.get(position).getAppointmentTime()) {
                appointmentTime.append("\r").append(s).append("\r|");
            }
            appointmentTime = new StringBuilder(appointmentTime.substring(0, appointmentTime.length() - 1));
            tvTime.setText(appointmentTime.toString());

            tvPrice.setText(String.format(mContext.getString(R.string.at_price_), df.format(list.get(position).getPrice())));
            tvStatus.setText(ATResourceUtils.getString(ATResourceUtils.getResIdByName(String.format(mContext.getString(R.string.at_appoint_status_)
                    , list.get(position).getAppointmentStatus()), ATResourceUtils.ResourceType.STRING)));
            tvPayStatus.setText(ATResourceUtils.getString(ATResourceUtils.getResIdByName(String.format(mContext.getString(R.string.at_appoint_pay_status_)
                    , list.get(position).getPayStatus()), ATResourceUtils.ResourceType.STRING)));
            tvPay.setVisibility((list.get(position).getAppointmentStatus() == 5 && list.get(position).getPayStatus() == 0) ? View.VISIBLE : View.GONE);
            switch (list.get(position).getAppointmentStatus()) {
                case 0:
                    //已预约
                    tvInter.setVisibility(View.VISIBLE);
                    tvOut.setVisibility(View.GONE);
                    tvCancel.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    //已入场
                    tvInter.setVisibility(View.GONE);
                    tvOut.setVisibility(View.VISIBLE);
                    tvCancel.setVisibility(View.GONE);
                    break;
                default:
                    //已过期
                    tvInter.setVisibility(View.GONE);
                    tvOut.setVisibility(View.GONE);
                    tvCancel.setVisibility(View.GONE);
                    break;
            }

            tvInter.setOnClickListener(v -> mATOnRVItemIntegerClickListener.onItemClick(v, position, 5));
            tvOut.setOnClickListener(v -> mATOnRVItemIntegerClickListener.onItemClick(v, position, 4));
            tvCancel.setOnClickListener(v -> mATOnRVItemIntegerClickListener.onItemClick(v, position, 3));
            tvPay.setOnClickListener(v -> mATOnRVItemIntegerClickListener.onItemClick(v, position, 1));
        }
    }

    private ATOnRVItemIntegerClickListener mATOnRVItemIntegerClickListener = null;

    public void setATOnRVItemIntegerClickListener(ATOnRVItemIntegerClickListener ATOnRVItemIntegerClickListener) {
        mATOnRVItemIntegerClickListener = ATOnRVItemIntegerClickListener;
    }
}