package com.aliyun.ayland.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.interfaces.ATOnEPLItemClickListener;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATChangeHouseEPLAdapter extends BaseExpandableListAdapter {
    private List<List<ATHouseBean>> mVillageList = new ArrayList<>();
    private Context context;
    private LayoutInflater inflater;
    private String iotSpaceId;

    // 是否有用户习惯
    public ATChangeHouseEPLAdapter(Context context, List<List<ATHouseBean>> mVillagesList) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.mVillageList.addAll(mVillagesList);
    }

    public synchronized void setList(List<List<ATHouseBean>> mVillageList, String iotSpaceId) {
        this.mVillageList.clear();
        this.mVillageList.addAll(mVillageList);
        this.iotSpaceId = iotSpaceId;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mVillageList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mVillageList.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mVillageList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mVillageList.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView == null) {
            holder = new GroupViewHolder();
            convertView = inflater.inflate(R.layout.at_item_epl_change_house_group, parent, false);
            holder.mTvVillageCode = convertView.findViewById(R.id.tv_village_code);
            convertView.setTag(holder);
            ATAutoUtils.autoSize(convertView);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        holder.mTvVillageCode.setText(mVillageList.get(groupPosition).get(0).getVillageName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
                             ViewGroup parent) {
        ChildViewHolder holder;
        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = inflater.inflate(R.layout.at_item_epl_change_house_child, parent, false);
            holder.mRlContent = convertView.findViewById(R.id.rl_content);
            holder.mImgSelect = convertView.findViewById(R.id.img_select);
            holder.mTvAddress = convertView.findViewById(R.id.tv_house_address);
            convertView.setTag(holder);
            ATAutoUtils.autoSize(convertView);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        holder.mRlContent.setOnClickListener(view -> mOnItemClickListener.onItemClick(groupPosition, childPosition));
        if (!TextUtils.isEmpty(iotSpaceId) && iotSpaceId.equals(mVillageList.get(groupPosition).get(childPosition).getIotSpaceId())) {
            holder.mImgSelect.setVisibility(View.VISIBLE);
            holder.mRlContent.setClickable(false);
            holder.mTvAddress.setTextColor(context.getResources().getColor(R.color._333333));
        } else {
            holder.mImgSelect.setVisibility(View.INVISIBLE);
            holder.mRlContent.setClickable(true);
            holder.mTvAddress.setTextColor(context.getResources().getColor(R.color._666666));
        }
        holder.mTvAddress.setText(mVillageList.get(groupPosition).get(childPosition).getHouseAddress());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class GroupViewHolder {
        TextView mTvVillageCode;
    }

    private class ChildViewHolder {
        RelativeLayout mRlContent;
        ImageView mImgSelect;
        TextView mTvAddress;
    }

    private ATOnEPLItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(ATOnEPLItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}