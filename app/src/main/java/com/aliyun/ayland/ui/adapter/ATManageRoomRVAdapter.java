package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATRoomBean1;
import com.aliyun.ayland.interfaces.ATOnRVItemClickListener;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATManageRoomRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATRoomBean1> list = new ArrayList<>();

    public ATManageRoomRVAdapter(Activity context) {
        mContext = context;
        list.add(new ATRoomBean1());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_manage_room, parent, false);
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
        this.list.add(new ATRoomBean1());
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRlContent;
        private TextView mTvRoomName;
        private LinearLayout mLlAddRoom;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            mTvRoomName = itemView.findViewById(R.id.tv_room_name);
            mLlAddRoom = itemView.findViewById(R.id.ll_add_room);
        }

        public void setData(int position) {
            if (position == list.size() - 1) {
                mRlContent.setVisibility(View.GONE);
                mLlAddRoom.setVisibility(View.VISIBLE);
                mLlAddRoom.setOnClickListener((view) -> {
                    if (lis != null) {
                        lis.onItemClick(view, position);
                    }
                });
            } else {
                mRlContent.setVisibility(View.VISIBLE);
                mLlAddRoom.setVisibility(View.GONE);
                mTvRoomName.setText(list.get(position).getName());
                mRlContent.setOnClickListener((view) -> {
                    if (lis != null) {
                        lis.onItemClick(view, position);
                    }
                });
            }
        }
    }

    private ATOnRVItemClickListener lis;

    public void setOnRVClickListener(ATOnRVItemClickListener lis) {
        this.lis = lis;
    }
}
