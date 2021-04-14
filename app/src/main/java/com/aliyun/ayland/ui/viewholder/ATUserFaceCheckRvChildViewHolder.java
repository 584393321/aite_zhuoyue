package com.aliyun.ayland.ui.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATEventInteger;
import com.aliyun.ayland.data.ATUserFaceCheckBean;
import com.anthouse.wyzhuoyue.R;

import org.greenrobot.eventbus.EventBus;

/**
 * @author guikong on 18/4/8.
 */

public class ATUserFaceCheckRvChildViewHolder extends ATSettableViewHolder {
    private Context mContext;
    private TextView mTvDeviceName, mTvStatus, mTvFaceStatus;

    public ATUserFaceCheckRvChildViewHolder(View view) {
        super(view);
        mContext = view.getContext();
        mTvDeviceName = view.findViewById(R.id.tv_device_name);
        mTvStatus = view.findViewById(R.id.tv_status);
        mTvFaceStatus = view.findViewById(R.id.tv_retry);
        ATAutoUtils.autoSize(view);
    }

    @Override
    public void setData(Object object, int position, int count) {
        if (!(object instanceof ATUserFaceCheckBean))
            return;
        mTvDeviceName.setText(((ATUserFaceCheckBean) object).getDeviceName());
        mTvFaceStatus.setOnClickListener(view -> {
            if (0 == ((ATUserFaceCheckBean) object).getFaceStatus() || -1 == ((ATUserFaceCheckBean) object).getFaceStatus())
                EventBus.getDefault().post(new ATEventInteger("UserFaceCheckFragment", position));
        });
        switch (((ATUserFaceCheckBean) object).getFaceStatus()) {
            case -1:
                //失败
                mTvFaceStatus.setText(mContext.getString(R.string.at_fail_to_apply));
                mTvFaceStatus.setTextColor(mContext.getResources().getColor(R.color.white));
                mTvFaceStatus.setBackground(mContext.getResources().getDrawable(R.drawable.at_selector_66px_1478c8_005395));
                break;
            case 0:
                //未录入
                mTvFaceStatus.setText(mContext.getString(R.string.at_applying));
                mTvFaceStatus.setTextColor(mContext.getResources().getColor(R.color.white));
                mTvFaceStatus.setBackground(mContext.getResources().getDrawable(R.drawable.at_selector_66px_1478c8_005395));
                break;
            case 1:
                //已录入
                mTvFaceStatus.setText(mContext.getString(R.string.at_apply_success));
                mTvFaceStatus.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvFaceStatus.setBackground(null);
                break;
            case 2:
                //授权中
                mTvFaceStatus.setText(mContext.getString(R.string.at_authorization));
                mTvFaceStatus.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvFaceStatus.setBackground(null);
                break;
            case 3:
                //失败
                mTvFaceStatus.setText(mContext.getString(R.string.at_off_line));
                mTvFaceStatus.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                mTvFaceStatus.setBackground(null);
                break;
        }
    }
}
