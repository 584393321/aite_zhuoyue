package com.aliyun.ayland.ui.activity;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

public class ATCalendarActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_calendar;
    }

    @Override
    protected void findView() {
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void getVisitorReservationRecordList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_GETVISITORRESERVATIONRECORDLIST, jsonObject);
    }

    private void init() {
//        int[] data = CalendarUtil.getYMD(new Date());
//        titlebar.setTitle(data[0] + "/" + data[1] + "/" + data[2]);
//        initList();
    }

    private void initList() {
//        list.setAdapter(new BaseAdapter() {
//            @Override
//            public int getCount() {
//                return 100;
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return null;
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return 0;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(CalendarActivity.this).inflate(android.R.layout.simple_list_item_1, null);
//                }
//
//                TextView textView = (TextView) convertView;
//                textView.setText("position:" + position);
//
//                return convertView;
//            }
//        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_GETVISITORRESERVATIONRECORDLIST:

                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}