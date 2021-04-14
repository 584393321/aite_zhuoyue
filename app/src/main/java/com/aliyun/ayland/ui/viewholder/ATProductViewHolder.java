package com.aliyun.ayland.ui.viewholder;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.aliyun.ayland.data.ATProduct;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.ui.activity.ATProductListActivity;
import com.aliyun.iot.aep.component.router.Router;
import com.anthouse.wyzhuoyue.R;

/**
 * @author guikong on 18/4/8.
 */
public class ATProductViewHolder extends RecyclerView.ViewHolder {

    private View action, divideLine;
    private TextView title;

    public ATProductViewHolder(View view) {
        super(view);
        action = view;
        title = view.findViewById(R.id.deviceadd_product_list_product_title_tv);
        divideLine = view.findViewById(R.id.deviceadd_product_list_divide_line);
    }

    public void setProduct(final ATProduct product, int position, int count) {
        title.setText(product.name);

        final String pk = product.productKey;
        final String dn = product.name;

        action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("productKey", pk);
                bundle.putString("deviceName", dn);
                Activity activity = (Activity) v.getContext();
                Router.getInstance().toUrlForResult(activity,
                        ATConstants.RouterUrl.PLUGIN_ID_DEVICE_CONFIG,
                        ATProductListActivity.REQUEST_CODE_PRODUCT_ADD, bundle);
            }
        });

        if (position == count - 1) {
            divideLine.setVisibility(View.GONE);
        }
    }
}
