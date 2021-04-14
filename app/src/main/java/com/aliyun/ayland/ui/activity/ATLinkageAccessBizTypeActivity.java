package com.aliyun.ayland.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.data.ATBizTypeBean;
import com.aliyun.ayland.ui.adapter.ATBizTypeRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.List;

public class ATLinkageAccessBizTypeActivity extends ATBaseActivity {
    private int current_position = 0;
    private List<ATBizTypeBean> bizTypeList = new ArrayList<>();
    private ATMyTitleBar titleBar;
    private RecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_recycleview;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        recyclerView = findViewById(R.id.recyclerView);
        init();
    }

    @Override
    protected void initPresenter() {
    }

    private void init() {
        JSONObject params = JSONObject.parseObject(getIntent().getStringExtra("params"));
        boolean replace = getIntent().getBooleanExtra("replace", false);
        String content = getIntent().getStringExtra("content");
        ATBizTypeBean BizTypeBean1 = new ATBizTypeBean();
        ATBizTypeBean BizTypeBean2 = new ATBizTypeBean();
        ATBizTypeBean BizTypeBean3 = new ATBizTypeBean();
        switch (getIntent().getIntExtra("bizType", 101)){
            case 101:
                BizTypeBean1.setBizType("FACE");
                BizTypeBean1.setBizTypeCN("进和出");
                bizTypeList.add(BizTypeBean1);
                titleBar.setTitle(getString(R.string.at_person_access));
                break;
            case 102:
                BizTypeBean1.setBizType("ENTRANCE_IN");
                BizTypeBean1.setBizTypeCN("进");
                BizTypeBean2.setBizType("ENTRANCE_OUT");
                BizTypeBean2.setBizTypeCN("出");
                BizTypeBean3.setBizType("ENTRANCE");
                BizTypeBean3.setBizTypeCN("进和出");
                bizTypeList.add(BizTypeBean1);
                bizTypeList.add(BizTypeBean2);
                bizTypeList.add(BizTypeBean3);
                titleBar.setTitle(getString(R.string.at_person_access));
                break;
            case 103:
                BizTypeBean1.setBizType("CAR_IN");
                BizTypeBean1.setBizTypeCN("进");
                BizTypeBean2.setBizType("CAR_OUT");
                BizTypeBean2.setBizTypeCN("出");
                BizTypeBean3.setBizType("CAR");
                BizTypeBean3.setBizTypeCN("进和出");
                bizTypeList.add(BizTypeBean1);
                bizTypeList.add(BizTypeBean2);
                bizTypeList.add(BizTypeBean3);
                titleBar.setTitle(getString(R.string.at_vehicle_access));
                break;
            case 109:
                break;
        }
        if (replace){
            for (int i = 0; i < bizTypeList.size(); i++) {
                if(content.split(" ")[1].equals(bizTypeList.get(i).getBizTypeCN())){
                    current_position = i;
                    break;
                }
            }
            content = content.split(" ")[0];
        }
        String finalContent = content;
        titleBar.setRightButtonText(getString(R.string.at_done));

        titleBar.setRightClickListener(() -> {
            params.put("bizType", bizTypeList.get(current_position).getBizType());
            params.put("sceneType", 6);
            setResult(RESULT_OK, getIntent()
                    .putExtra("content", finalContent + " " + bizTypeList.get(current_position).getBizTypeCN())
                    .putExtra("params", params.toJSONString()));
            finish();
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ATBizTypeRVAdapter bizTypeRVAdapter = new ATBizTypeRVAdapter(this);
        recyclerView.setAdapter(bizTypeRVAdapter);
        bizTypeRVAdapter.setOnItemClickListener((view, position) -> current_position = position);
        bizTypeRVAdapter.setList(bizTypeList, current_position);
    }
}