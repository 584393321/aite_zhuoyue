package com.aliyun.ayland.ui.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliyun.ayland.data.ATUserFaceCheckBean;
import com.aliyun.ayland.data.ATUserFaceCheckListBean;
import com.aliyun.ayland.ui.viewholder.ATSceneTitleAutoViewHolder;
import com.aliyun.ayland.ui.viewholder.ATSettableViewHolder;
import com.aliyun.ayland.ui.viewholder.ATUserFaceCheckRvChildViewHolder;
import com.aliyun.ayland.ui.viewholder.ATUserFaceCheckRvViewHolder;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATUserFaceCheckRvAdapter extends RecyclerView.Adapter<ATSettableViewHolder> {
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_CONTENT = 1;
    private static final int TYPE_END = 2;
    private List<Object> data = new ArrayList<>();

    public void setList(List<ATUserFaceCheckListBean> villageDetailBeanList, boolean fromFamily) {
        data.clear();
        for (ATUserFaceCheckListBean mUserFaceCheckListBean : villageDetailBeanList) {
            mUserFaceCheckListBean.setFromFamily(fromFamily);
            data.add(mUserFaceCheckListBean);
            if (mUserFaceCheckListBean.getCheckStatus() == 1)
                for (ATUserFaceCheckBean userFaceCheckBean : mUserFaceCheckListBean.getDeviceList()) {
                    if (userFaceCheckBean.getFaceStatus() != 1)
                        data.add(userFaceCheckBean);
                }
            data.add(0);
        }
        notifyDataSetChanged();
    }

    public List<Object> getData() {
        return data;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = data.get(position);
        if (item instanceof ATUserFaceCheckListBean) {
            return TYPE_TITLE;
        } else if (item instanceof Integer) {
            return TYPE_END;
        } else {
            return TYPE_CONTENT;
        }
    }

    @NonNull
    @Override
    public ATSettableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        if (TYPE_TITLE == viewType) {
            view = inflater.inflate(R.layout.at_item_rv_user_face_check, parent, false);
            return new ATUserFaceCheckRvViewHolder(view);
        } else if (TYPE_END == viewType) {
            view = inflater.inflate(R.layout.at_item_rv_user_face_check_end, parent, false);
            return new ATSceneTitleAutoViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.at_item_rv_user_face_check_child, parent, false);
            return new ATUserFaceCheckRvChildViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ATSettableViewHolder holder, int position) {
        Object item = data.get(position);
        holder.setData(item, position, data.size());
    }
}