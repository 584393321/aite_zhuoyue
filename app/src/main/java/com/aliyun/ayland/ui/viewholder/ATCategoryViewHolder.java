package com.aliyun.ayland.ui.viewholder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.ayland.data.ATCategory;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.utils.ATAddDeviceScanHelper;
import com.aliyun.iot.aep.component.router.Router;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

/**
 * @author guikong on 18/4/8.
 */
public class ATCategoryViewHolder extends ATSettableViewHolder {

    private View action;
    private ImageView logo;
    private TextView title;

    public ATCategoryViewHolder(View view) {
        super(view);
        action = view;
        logo = view.findViewById(R.id.img_device);
        title = view.findViewById(R.id.tv_device);
    }


    @Override
    public void setData(Object object, int position, int count) {
        if (!(object instanceof ATCategory)) {
            return;
        }

        final ATCategory category = (ATCategory) object;
        title.setText(category.getProductName());

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.device_add_place_holder)
                .error(R.drawable.device_add_place_holder);

        Glide.with(logo).load(R.drawable.device_add_place_holder).into(logo);

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("productKey", category.productKey);
//                bundle.putString("deviceName", category.productName);
//                ((Activity) v.getContext()).startActivityForResult(new Intent(v.getContext(), DeviceBindActivity.class)
//                        .putExtras(bundle), DeviceBindActivity.REQUEST_CODE);
                Bundle bundle = new Bundle();
                bundle.putString("productKey", category.getProductKey());
//                bundle.putString("deviceName", category.getProductName());
                Router.getInstance().toUrlForResult((Activity) v.getContext(), ATConstants.RouterUrl.PLUGIN_ID_DEVICE_CONFIG,
                        ATAddDeviceScanHelper.REQUEST_CODE_CONFIG_WIFI, bundle);
            }
        });
    }
}
