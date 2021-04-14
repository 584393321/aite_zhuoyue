package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATFamilyManageRoomBean;
import com.aliyun.ayland.interfaces.ATOnRecyclerViewItemClickListener;
import com.aliyun.ayland.widget.ATSwitchView;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATFamilyManageMemberRoomRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private boolean ifAdmin;
    private List<ATFamilyManageRoomBean> list = new ArrayList<>();

    public ATFamilyManageMemberRoomRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_family_manage_member_room, parent, false);
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

    public void setLists(List<ATFamilyManageRoomBean> roomList, boolean ifAdmin) {
        this.list.clear();
        this.list.addAll(roomList);
        this.ifAdmin = ifAdmin;
        notifyDataSetChanged();
    }

    public void notifyItem(int current_position, int status) {
        list.get(current_position).setCanSee(status);
        notifyItemChanged(current_position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvName, mTvStatus;
        private ATSwitchView switchView;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvStatus = itemView.findViewById(R.id.tv_status);
            switchView = itemView.findViewById(R.id.switchView);
        }

        public void setData(int position) {
            mTvName.setText(list.get(position).getName());
            if (ifAdmin) {
                switchView.setOpened(list.get(position).getCanSee() == 1);
                switchView.setOnStateChangedListener(new ATSwitchView.OnStateChangedListener() {
                    @Override
                    public void toggleToOn(ATSwitchView view) {
                        mOnItemClickListener.onItemClick(view, list.get(position).getCanSee() == 1 ? 0 : 1, position);
                    }

                    @Override
                    public void toggleToOff(ATSwitchView view) {
                        mOnItemClickListener.onItemClick(view, list.get(position).getCanSee() == 1 ? 0 : 1, position);
                    }
                });
            } else {
                if (list.get(position).getCanSee() == 1) {
                    mTvStatus.setTextColor(mContext.getResources().getColor(R.color._1478C8));
                    mTvStatus.setText(mContext.getString(R.string.at_can_see));
                } else {
                    mTvStatus.setTextColor(mContext.getResources().getColor(R.color._CCCCCC));
                    mTvStatus.setText(mContext.getString(R.string.at_not_see));
                }
            }
        }
    }

    private ATOnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(ATOnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}