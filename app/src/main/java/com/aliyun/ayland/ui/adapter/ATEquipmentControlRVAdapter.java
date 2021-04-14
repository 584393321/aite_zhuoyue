package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATEventInteger;
import com.aliyun.ayland.data.ATRoomBean1;
import com.anthouse.wyzhuoyue.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ATEquipmentControlRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATRoomBean1> list = new ArrayList<>();
    private int select = 0;

    public ATEquipmentControlRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_equipment_control, parent, false);
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

    public void setLists(List<ATRoomBean1> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private View view;
        private RelativeLayout rlContent;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            view = itemView.findViewById(R.id.view);
            rlContent = itemView.findViewById(R.id.rl_content);
        }

        public void setData(int position) {
            tvName.setText(list.get(position).getName());
            if (position == select) {
                view.setVisibility(View.VISIBLE);
                tvName.setTextColor(mContext.getResources().getColor(R.color._1478C8));
            } else {
                view.setVisibility(View.GONE);
                tvName.setTextColor(mContext.getResources().getColor(R.color._666666));
            }
            rlContent.setOnClickListener(view -> {
                EventBus.getDefault().post(new ATEventInteger("EquipmentActivity", position));
                setSelectItem(position);
            });
            itemView.setTag(position);
        }
    }

    public void setSelectItem(int select) {
        this.select = select;
        notifyDataSetChanged();
    }
}