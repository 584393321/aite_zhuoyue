package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliyun.ayland.data.ATFindFamilyMemberForOutAbnormalBean.MembersBean;
import com.aliyun.ayland.data.ATFindFamilyMemberForOutAbnormalBean.MembersBean.CycleListBean;
import com.aliyun.ayland.interfaces.ATOnRecyclerViewItemClickListener;
import com.aliyun.ayland.ui.viewholder.ATSettableViewHolder;
import com.aliyun.ayland.ui.viewholder.ATWisdomSecurityOutAbnormalAddViewHolder;
import com.aliyun.ayland.ui.viewholder.ATWisdomSecurityOutAbnormalEditViewHolder;
import com.aliyun.ayland.ui.viewholder.ATWisdomSecurityOutAbnormalTitleViewHolder;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ATWisdomSecurityOutAbnormalRVAdapter extends RecyclerView.Adapter<ATSettableViewHolder> {
    private static final int TYPE_TITLE = 0;
    private static final int TYPE_TITLE_ADD = 1;
    private static final int TYPE_TITLE_EDIT = 2;
    private List<Object> list = new ArrayList<>();
    private Activity mContext;
    private int button_status;
    private HashMap<Integer, List<CycleListBean>> cycleListMap = new HashMap<>();
    private List<MembersBean> membersBeanlist = new ArrayList<>();

    public ATWisdomSecurityOutAbnormalRVAdapter(Activity context) {
        mContext = context;
    }

    public List<Object> getLists() {
        return list;
    }

    public int getButton_status() {
        return button_status;
    }

    public void setButton_status(int button_status) {
        this.button_status = button_status;
        notifyDataSetChanged();
    }

    public void setLists(List<MembersBean> list, int status) {
        button_status = status;
        membersBeanlist.clear();
        membersBeanlist.addAll(list);
        this.list.clear();
        for (MembersBean membersBean : list) {
            this.list.add(membersBean);
            if (status != 1)
                this.list.add(membersBean.getPersonCode());
            this.list.addAll(membersBean.getCycleList());
            List<CycleListBean> cycleListBeanList = new ArrayList<>();
            for (int j = 0; j < 7; j++) {
                CycleListBean cycleListBean = new CycleListBean();
                cycleListBean.setHide(true);
                cycleListBean.setWeekDay(j);
                cycleListBean.setPersonCode(membersBean.getPersonCode());
                cycleListBeanList.add(cycleListBean);
            }
            for (CycleListBean cycleListBean : membersBean.getCycleList()) {
                cycleListBeanList.get(cycleListBean.getWeekDay()).setBeginTime(cycleListBean.getBeginTime());
                cycleListBeanList.get(cycleListBean.getWeekDay()).setEndTime(cycleListBean.getEndTime());
                cycleListBeanList.get(cycleListBean.getWeekDay()).setHide(false);
            }
            cycleListMap.put(membersBean.getPersonCode(), cycleListBeanList);
        }
        notifyDataSetChanged();
    }

    public void setStatus(int position, int specs) {
        Log.e("setStatus: ", list.get(position).toString()+"---"+position+"---"+specs);
        if (list.get(position) instanceof MembersBean) {
            ((MembersBean) list.get(position)).setStatus(specs);
            for (MembersBean membersBean : membersBeanlist) {
                if (((MembersBean) list.get(position)).getPersonCode() == membersBean.getPersonCode()) {
                    membersBean.setStatus(specs);
                    break;
                }
            }
        }
    }

    public void replaceCondition(int personCode, String cron_week, int beginTime, int endTime) {
        if ("7".equals(cron_week)) {
            //每天
            for (int i = 0; i < 7; i++) {
                cycleListMap.get(personCode).get(i).setBeginTime(beginTime);
                cycleListMap.get(personCode).get(i).setEndTime(endTime);
                cycleListMap.get(personCode).get(i).setHide(false);
            }
        } else if (cron_week.length() != 13 && cron_week.length() != 0) {
            for (String s : cron_week.split(",")) {
                cycleListMap.get(personCode).get(Integer.parseInt(s)).setBeginTime(beginTime);
                cycleListMap.get(personCode).get(Integer.parseInt(s)).setEndTime(endTime);
                cycleListMap.get(personCode).get(Integer.parseInt(s)).setHide(false);
            }
        } else {
            cycleListMap.get(personCode).get(Integer.parseInt(cron_week)).setBeginTime(beginTime);
            cycleListMap.get(personCode).get(Integer.parseInt(cron_week)).setEndTime(endTime);
            cycleListMap.get(personCode).get(Integer.parseInt(cron_week)).setHide(false);
        }
        this.list.clear();
        for (MembersBean membersBean : membersBeanlist) {
            this.list.add(membersBean);
            if (button_status != 1)
                this.list.add(membersBean.getPersonCode());
            for (CycleListBean cycleListBean : cycleListMap.get(membersBean.getPersonCode())) {
                if (!cycleListBean.isHide()) {
                    this.list.add(cycleListBean);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void replaceCondition(int position, int beginTime, int endTime) {
        ((CycleListBean) list.get(position)).setBeginTime(beginTime);
        ((CycleListBean) list.get(position)).setEndTime(endTime);
        cycleListMap.get(((CycleListBean) list.get(position)).getPersonCode()).get(((CycleListBean) list.get(position)).getWeekDay()).setBeginTime(beginTime);
        cycleListMap.get(((CycleListBean) list.get(position)).getPersonCode()).get(((CycleListBean) list.get(position)).getWeekDay()).setEndTime(endTime);
        notifyItemChanged(position);
    }

    public void removePosition(int position) {
        cycleListMap.get(((CycleListBean) list.get(position)).getPersonCode()).get(((CycleListBean) list.get(position)).getWeekDay()).setHide(true);
        list.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        Object item = list.get(position);
        if (item instanceof MembersBean) {
            return TYPE_TITLE;
        } else if (item instanceof Integer) {
            return TYPE_TITLE_ADD;
        } else {
            return TYPE_TITLE_EDIT;
        }
    }

    @NonNull
    @Override
    public ATSettableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        if (viewType == TYPE_TITLE) {
            view = inflater.inflate(R.layout.at_item_rv_wisdom_security_out_alone_member, parent, false);
            return new ATWisdomSecurityOutAbnormalTitleViewHolder(view, ATWisdomSecurityOutAbnormalRVAdapter.this);
        } else if (viewType == TYPE_TITLE_ADD) {
            view = inflater.inflate(R.layout.at_item_rv_wisdom_security_out_abnormal_add, parent, false);
            return new ATWisdomSecurityOutAbnormalAddViewHolder(view, mContext);
        } else {
            view = inflater.inflate(R.layout.at_item_rv_wisdom_security_out_abnormal_edit, parent, false);
            return new ATWisdomSecurityOutAbnormalEditViewHolder(view, mContext, ATWisdomSecurityOutAbnormalRVAdapter.this);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ATSettableViewHolder holder, int position) {
        Object item = list.get(position);
        holder.setData(item, position, list.size());
    }

    private ATOnRecyclerViewItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(ATOnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}