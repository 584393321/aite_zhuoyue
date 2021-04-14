package com.aliyun.ayland.ui.viewholder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.data.ATLocalDevice;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.utils.ATAddDeviceScanHelper;
import com.aliyun.iot.aep.component.router.Router;
import com.anthouse.wyzhuoyue.R;

/**
 * @author guikong on 18/4/8.
 */
public class ATLocalDeviceViewHolder extends ATSettableViewHolder {

    TextView tvProduct, tvDevice;
    RelativeLayout rlContent;

    public ATLocalDeviceViewHolder(View view) {
        super(view);
        tvProduct = view.findViewById(R.id.tv_product_key);
        rlContent = view.findViewById(R.id.rl_content);
        tvDevice = view.findViewById(R.id.tv_device_name);
    }

    @Override
    public void setData(Object object, int position, int count) {
        if (!(object instanceof ATLocalDevice)) {
            return;
        }
        final ATLocalDevice localDevice = (ATLocalDevice) object;
        tvProduct.setText(localDevice.productName);
        tvDevice.setText(localDevice.deviceName);
        rlContent.setOnClickListener(v -> {
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
