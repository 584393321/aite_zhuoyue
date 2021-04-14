package com.aliyun.ayland.ui.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATFindFamilyMemberForOutAbnormalBean.MembersBean.CycleListBean;
import com.aliyun.ayland.ui.activity.ATWisdomSecurityOutAbnormalTimingActivity;
import com.aliyun.ayland.ui.adapter.ATWisdomSecurityOutAbnormalRVAdapter;
import com.aliyun.ayland.utils.ATTimeFormatUtils;
import com.anthouse.wyzhuoyue.R;

import static com.aliyun.ayland.ui.activity.ATWisdomSecurityOutAbnormalActivity.REQUEST_CODE_TIMING;

/**
 * @author guikong on 18/4/8.
 */

public class ATWisdomSecurityOutAbnormalEditViewHolder extends ATSettableViewHolder {
    private Activity context;
    private RelativeLayout rlContent;
    private TextView tvWeek, tvTime;
    private LinearLayout llDelete;
    private ATWisdomSecurityOutAbnormalRVAdapter adapter;

    public ATWisdomSecurityOutAbnormalEditViewHolder(View view, Activity context, ATWisdomSecurityOutAbnormalRVAdapter adapter) {
        super(view);
        ATAutoUtils.autoSize(view);
        rlContent = view.findViewById(R.id.rl_content);
        tvWeek = view.findViewById(R.id.tv_week);
        tvTime = view.findViewById(R.id.tv_time);
        llDelete = view.findViewById(R.id.ll_delete);
        this.context = context;
        this.adapter = adapter;
    }

    @Override
    public void setData(Object object, int position, int count) {
        if (object instanceof CycleListBean) {
            tvWeek.setText(context.getResources().getStringArray(R.array.weekday)[((CycleListBean) object).getWeekDay()]);

            tvTime.setText(String.format(context.getString(R.string.at_begin_to_end_), ATTimeFormatUtils.minuteToTime(((CycleListBean) object).getBeginTime())
                    , ATTimeFormatUtils.minuteToTime(((CycleListBean) object).getEndTime())));
            if (adapter.getButton_status() == 1) {
                llDelete.setVisibility(View.GONE);
                rlContent.setClickable(false);
            } else {
                llDelete.setVisibility(View.VISIBLE);
                llDelete.setOnClickListener(v -> adapter.removePosition(position));
                rlContent.setClickable(true);
                rlContent.setOnClickListener(v -> {
                    context.startActivityForResult(new Intent(context, ATWisdomSecurityOutAbnormalTimingActivity.class)
                            .putExtra("week_day", ((CycleListBean) object).getWeekDay())
                            .putExtra("beginTime", ((CycleListBean) object).getBeginTime())
                            .putExtra("endTime", ((CycleListBean) object).getEndTime())
                            .putExtra("position", position), REQUEST_CODE_TIMING);
                });
            }
        }
    }
}