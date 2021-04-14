package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
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
import com.aliyun.ayland.data.ATHouseBean;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATChangeHouseRVAdapter extends RecyclerView.Adapter {
    private Activity context;
    private ATHouseBean houseBean;
    private List<ATHouseBean> list = new ArrayList<>();

    public ATChangeHouseRVAdapter(Activity context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.at_item_epl_change_house_child, parent, false);
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

    public void setList(List<ATHouseBean> list, ATHouseBean houseBean) {
        this.list.clear();
        this.list.addAll(list);
        this.houseBean = houseBean;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvAddress;
        private RelativeLayout rlContent;
        private ImageView mImgSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mImgSelect = itemView.findViewById(R.id.img_select);
            tvAddress = itemView.findViewById(R.id.tv_house_address);
            rlContent = itemView.findViewById(R.id.rl_content);
        }

        public void setData(int position) {
            tvAddress.setText(list.get(position).getHouseAddress());

            if (houseBean != null && list.get(position).getBuildingCode().equals(houseBean.getBuildingCode())) {
                mImgSelect.setVisibility(View.VISIBLE);
                rlContent.setClickable(false);
                tvAddress.setTextColor(context.getResources().getColor(R.color._333333));
            } else {
                mImgSelect.setVisibility(View.INVISIBLE);
                rlContent.setClickable(true);
                tvAddress.setTextColor(context.getResources().getColor(R.color._666666));
            }
            rlContent.setOnClickListener(view -> {
                mOnItemClickListener.onItemClick(view, position);
            });
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
