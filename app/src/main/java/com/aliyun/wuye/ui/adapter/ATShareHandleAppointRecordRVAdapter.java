package com.aliyun.wuye.ui.adapter;

import android.app.Activity;
import android.content.Context;
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

public class ATShareHandleAppointRecordRVAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<ATShareAppointRecordBean> list = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("#####0.00");

    public ATShareHandleAppointRecordRVAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_wy_rv_handle_share_appoint_record, parent, false);
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
        private TextView tvSpaceName, tvSubject, tvTime, tvPrice, tvStatus, tvUser;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            tvSpaceName = itemView.findViewById(R.id.tv_space_name);
            tvSubject = itemView.findViewById(R.id.tv_subject);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvUser = itemView.findViewById(R.id.tv_user);
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
        }
    }
}