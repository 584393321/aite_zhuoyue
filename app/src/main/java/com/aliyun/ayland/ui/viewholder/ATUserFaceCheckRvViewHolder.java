package com.aliyun.ayland.ui.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATUserFaceCheckListBean;
import com.anthouse.wyzhuoyue.R;

/**
 * @author guikong on 18/4/8.
 */

public class ATUserFaceCheckRvViewHolder extends ATSettableViewHolder {
    private TextView mTvVillageName, mTvStatus;
    private Context mContext;

    public ATUserFaceCheckRvViewHolder(View view) {
        super(view);
        mContext = view.getContext();
        mTvVillageName = view.findViewById(R.id.tv_village_name);
        mTvStatus = view.findViewById(R.id.tv_status);
        ATAutoUtils.autoSize(view);
    }

    @Override
    public void setData(Object object, int position, int count) {
        if (object instanceof ATUserFaceCheckListBean) {
            mTvVillageName.setText(((ATUserFaceCheckListBean) object).getVillageName());
            if (((ATUserFaceCheckListBean) object).isFromFamily())
                if (((ATUserFaceCheckListBean) object).getCheckStatus() == 1) {
                    mTvStatus.setTextColor(mContext.getResources().getColor(R.color._1478C8));
                    mTvStatus.setText(mContext.getString(R.string.at_have_check));
                } else if (((ATUserFaceCheckListBean) object).getCheckStatus() == 0) {
                    mTvStatus.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                    mTvStatus.setText(mContext.getString(R.string.at_have_no_check));
                } else {
                    mTvStatus.setTextColor(mContext.getResources().getColor(R.color._BBBBBB));
                    mTvStatus.setText(mContext.getString(R.string.at_cannot_pass));
                }
        }
    }
}
