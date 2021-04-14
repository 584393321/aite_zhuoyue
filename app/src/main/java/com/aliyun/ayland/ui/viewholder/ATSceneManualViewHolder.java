package com.aliyun.ayland.ui.viewholder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATLocalDevice;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.utils.ATAddDeviceScanHelper;
import com.aliyun.iot.aep.component.router.Router;
import com.anthouse.wyzhuoyue.R;

/**
 * @author guikong on 18/4/8.
 */
public class ATSceneManualViewHolder extends ATSettableViewHolder {

    TextView name;
    View action;

    public ATSceneManualViewHolder(View view) {
        super(view);
        ATAutoUtils.autoSize(itemView);
        name = view.findViewById(R.id.tv_device_name);
        action = view.findViewById(R.id.rl_content);
    }

    @Override
    public void setData(Object object, int position, int count) {
        if (!(object instanceof ATLocalDevice)) {
            return;
        }

        final ATLocalDevice localDevice = (ATLocalDevice) object;
        name.setText(localDevice.productName);

        action.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("productKey", localDevice.productKey);
            bundle.putString("deviceName", localDevice.deviceName);
            bundle.putString("token", localDevice.token);
            bundle.putString("addDeviceFrom", localDevice.addDeviceFrom);
            Router.getInstance().toUrlForResult((Activity) v.getContext(), ATConstants.RouterUrl.PLUGIN_ID_DEVICE_CONFIG,
                    ATAddDeviceScanHelper.REQUEST_CODE_CONFIG_WIFI, bundle);
        });
    }
}
