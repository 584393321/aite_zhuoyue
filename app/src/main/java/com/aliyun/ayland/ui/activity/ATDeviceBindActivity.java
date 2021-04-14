package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;

import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.data.ATDevice;
import com.aliyun.ayland.presenter.ATDeviceBindPresenter;
import com.aliyun.ayland.ui.fragment.ATDeviceBindFragment;
import com.aliyun.ayland.utils.ATDeviceBindBusiness;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;

public class ATDeviceBindActivity extends ATBaseActivity {
    public static final int REQUEST_CODE = 0x898;
    private ATDeviceBindBusiness deviceBindBusiness;
    private ATDeviceBindPresenter mPresenter = null;
    private ATDeviceBindFragment deviceBindFragment = null;
    private ATMyTitleBar titleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_device_bind;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
    }

    @Override
    protected void initPresenter() {
    }

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String productKey = intent.getStringExtra("productKey");
        String deviceName = intent.getStringExtra("deviceName");
        String netType = intent.getStringExtra("netType");

        ATDevice device = new ATDevice();
        device.dn = deviceName;
        device.pk = productKey;
        device.token = intent.getStringExtra("token");
        device.iotId = intent.getStringExtra("iotId");

        deviceBindBusiness = new ATDeviceBindBusiness();

        if (null == savedInstanceState) {
            deviceBindFragment = new ATDeviceBindFragment();
            mPresenter = new ATDeviceBindPresenter(deviceBindBusiness, device, deviceBindFragment);
            deviceBindFragment.setPresenter(mPresenter);
        }
        titleBar.showRightButton(false);
        titleBar.setTitle(getResources().getString(R.string.at_label_bind_device));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, deviceBindFragment, deviceBindFragment.getClass().getSimpleName())
                .disallowAddToBackStack()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
//        if (null != device.roomId) {
//            titleBar.showRightButton(false);
//            titleBar.setTitle(getResources().getString(R.string.label_bind_device));
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, deviceBindFragment, deviceBindFragment.getClass().getSimpleName())
//                    .disallowAddToBackStack()
//                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                    .commit();
//        } else {
//            titleBar.showRightButton(true);
//            titleBar.setRightClickListener(new OnTitleRightClickInter() {
//                @Override
//                public void rightClick() {
//                    onRoomSelected();
//                }
//            });
//
//            getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//                @Override
//                public void onBackStackChanged() {
//                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//                        titleBar.showRightButton(true);
//                        ((TextView) findViewById(R.id.title)).setText(R.string.label_room_list);
//                    } else {
//                        titleBar.showRightButton(false);
//                        ((TextView) findViewById(R.id.title)).setText(R.string.label_bind_device);
//                    }
//                }
//            });
//            setupRecyclerView();
//            initRooms();
//        }
    }

    private void onRoomSelected() {
//        if (adapter.checkedPosition != RecyclerView.NO_POSITION &&
//                adapter.getItem(adapter.checkedPosition) != null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.fragment_container, deviceBindFragment, deviceBindFragment.getClass().getSimpleName())
//                    .addToBackStack(deviceBindFragment.getClass().getSimpleName())
//                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                    .commit();
//            mPresenter.setRoomId(adapter.getItem(adapter.checkedPosition).roomId);
//        } else {
//            LinkToast.makeText(this, R.string.hint_no_room_selected, Toast.LENGTH_SHORT).show();
//        }
    }
}
