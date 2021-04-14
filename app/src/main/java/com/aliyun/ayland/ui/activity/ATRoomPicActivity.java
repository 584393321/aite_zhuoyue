package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.ui.adapter.ATRoomPicRVAdapter;
import com.aliyun.ayland.utils.ATResourceUtils;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;

public class ATRoomPicActivity extends ATBaseActivity {
    private boolean create;
    private String[] mTitles;
    private int current_position;
    private ATMyTitleBar titleBar;
    private RecyclerView rvRoomPic;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_room_pic;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        rvRoomPic = findViewById(R.id.rv_room_pic);
        init();
    }

    @Override
    protected void initPresenter() {
    }

    private void init() {
        create = !TextUtils.isEmpty(getIntent().getStringExtra("allDeviceData"));

        mTitles = getResources().getStringArray(R.array.room_type);
        titleBar.setTitle(getString(R.string.at_choose_room_type));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        rvRoomPic.setLayoutManager(gridLayoutManager);
        ATRoomPicRVAdapter mRoomPicRVAdapter = new ATRoomPicRVAdapter(this, mTitles);
        rvRoomPic.setAdapter(mRoomPicRVAdapter);
        mRoomPicRVAdapter.setOnItemClickListener((view, position) -> {
            current_position = position;
            if (create) {
                startActivity(getIntent().putExtra("room_type", mTitles[current_position])
                        .putExtra("room_name", ATResourceUtils.getString(ATResourceUtils.getResIdByName(mTitles[position], ATResourceUtils.ResourceType.STRING)))
                        .setClass(this, ATEditRoomActivity.class));
                finish();
            } else {
                setResult(RESULT_OK, new Intent().putExtra("room_type", mTitles[position]));
                finish();
            }
        });
    }
}