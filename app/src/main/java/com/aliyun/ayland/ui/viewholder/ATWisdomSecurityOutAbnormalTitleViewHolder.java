package com.aliyun.ayland.ui.viewholder;

import android.view.View;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATFindFamilyMemberForOutAbnormalBean.MembersBean;
import com.aliyun.ayland.ui.adapter.ATWisdomSecurityOutAbnormalRVAdapter;
import com.aliyun.ayland.widget.ATSwitchButton;
import com.anthouse.wyzhuoyue.R;

/**
 * @author guikong on 18/4/8.
 */

public class ATWisdomSecurityOutAbnormalTitleViewHolder extends ATSettableViewHolder {
    private TextView tvName, tvState;
    private ATSwitchButton switchButton;
    private ATWisdomSecurityOutAbnormalRVAdapter adapter;

    public ATWisdomSecurityOutAbnormalTitleViewHolder(View view, ATWisdomSecurityOutAbnormalRVAdapter adapter) {
        super(view);
        ATAutoUtils.autoSize(view);
        tvName = view.findViewById(R.id.tv_name);
        tvState = view.findViewById(R.id.tv_state);
        switchButton = view.findViewById(R.id.switchButton);
        this.adapter = adapter;
    }

    @Override
    public void setData(Object object, int position, int count) {
        if (object instanceof MembersBean) {
            tvName.setText(((MembersBean) object).getLastname() + ((MembersBean) object).getFirstname());
            if (adapter.getButton_status() == 1) {
                tvState.setVisibility(View.VISIBLE);
                switchButton.setVisibility(View.GONE);
                tvState.setText(((MembersBean) object).getStatus() == 0 ? R.string.at_not_open : R.string.at_has_been_open);
            } else {
                tvState.setVisibility(View.GONE);
                switchButton.setVisibility(View.VISIBLE);
                switchButton.setChecked(((MembersBean) object).getStatus() == 1);
            }
            switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> adapter.setStatus(position, (isChecked ? 1 : 0)));
        }
    }
}