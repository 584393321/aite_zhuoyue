package com.aliyun.ayland.ui.viewholder;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.ui.activity.ATWisdomSecurityOutAbnormalTimingActivity;
import com.anthouse.wyzhuoyue.R;

import static com.aliyun.ayland.ui.activity.ATWisdomSecurityOutAbnormalActivity.REQUEST_CODE_TIMING;

/**
 * @author guikong on 18/4/8.
 */

public class ATWisdomSecurityOutAbnormalAddViewHolder extends ATSettableViewHolder {
    private Activity context;
    private LinearLayout llContent;

    public ATWisdomSecurityOutAbnormalAddViewHolder(View view, Activity context) {
        super(view);
        ATAutoUtils.autoSize(view);
        llContent = view.findViewById(R.id.ll_content);
        this.context = context;
    }

    @Override
    public void setData(Object object, int position, int count) {
        if (object instanceof Integer) {
            llContent.setOnClickListener(v -> {
                context.startActivityForResult(new Intent(context, ATWisdomSecurityOutAbnormalTimingActivity.class).putExtra("week_day", 7)
                        .putExtra("position", (Integer)object), REQUEST_CODE_TIMING);
            });
        }
    }
}