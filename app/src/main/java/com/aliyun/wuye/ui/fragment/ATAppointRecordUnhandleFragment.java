package com.aliyun.wuye.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.contract.ATSceneContract;
import com.aliyun.ayland.data.ATEventClazz;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATShareAppointRecordBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATScenePresenter;
import com.aliyun.ayland.utils.ATResourceUtils;
import com.aliyun.wuye.ui.adapter.ATShareAppointRecordRVAdapter;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATAppointRecordUnhandleFragment extends ATBaseFragment implements ATSceneContract.View {
    private ATScenePresenter mPresenter;
    private ATShareAppointRecordRVAdapter mATShareAppointRecordRVAdapter;
    private ATHouseBean houseBean;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;
    private int pageNo = 1, mPosition, appointmentStatus, payStatus;
    private List<ATShareAppointRecordBean> mAtShareAppointBeanList = new ArrayList<>();
    private Dialog dialog, dialogPay;
    private TextView tvTitle;

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_recyclerview_sml;
    }

    @Override
    protected void findView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATScenePresenter(this);
        mPresenter.install(getActivity());
    }

    private void setPayStatusApp() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", mAtShareAppointBeanList.get(mPosition).getAppointmentOrderCode());
        jsonObject.put("payStatus", payStatus);
        mPresenter.request(ATConstants.Config.SERVER_URL_SETPAYSTATUSAPP, jsonObject, 0);
    }

    private void setAppointmentStatusApp() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appointmentOrderCode", mAtShareAppointBeanList.get(mPosition).getAppointmentOrderCode());
        jsonObject.put("appointmentStatus", appointmentStatus);
        jsonObject.put("confirmPerson", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_SETAPPOINTMENTSTATUSAPP, jsonObject, 0);
    }

    private void findAppointmentStatusWuye() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageCode", houseBean.getVillageId());
        jsonObject.put("pageNo", pageNo);
        jsonObject.put("pageSize", 10);
        jsonObject.put("handle", 0);
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDAPPOINTMENTSTATUSWUYE, jsonObject, 0);
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        mATShareAppointRecordRVAdapter = new ATShareAppointRecordRVAdapter(getActivity());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mATShareAppointRecordRVAdapter);
        mATShareAppointRecordRVAdapter.setATOnRVItemIntegerClickListener((view, position, type) -> {
            mPosition = position;
            if (type == 3 || type == 4 || type == 5) {
                appointmentStatus = type;
                tvTitle.setText(ATResourceUtils.getString(ATResourceUtils.getResIdByName(String.format(
                        getActivity().getString(R.string.at_sure_appoint_operate), appointmentStatus), ATResourceUtils.ResourceType.STRING)));
                dialog.show();
            }else if(type == 1){
                payStatus = type;
                dialogPay.show();
            }
        });
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNo++;
                findAppointmentStatusWuye();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNo = 1;
                findAppointmentStatusWuye();
                smartRefreshLayout.setNoMoreData(false);
            }
        });
        initDialog();
    }

    @SuppressLint("InflateParams")
    private void initDialog() {
        dialog = new Dialog(getActivity(), R.style.nameDialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.at_dialog_normal, null, false);
        tvTitle = view.findViewById(R.id.tv_title);
        view.findViewById(R.id.tv_cancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.tv_sure).setOnClickListener(v -> setAppointmentStatusApp());
        dialog.setContentView(view);

        dialogPay = new Dialog(getActivity(), R.style.nameDialog);
        View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.at_dialog_normal, null, false);
        ((TextView)view1.findViewById(R.id.tv_title)).setText(getString(R.string.at_sure_pay));
        view1.findViewById(R.id.tv_cancel).setOnClickListener(v -> dialogPay.dismiss());
        view1.findViewById(R.id.tv_sure).setOnClickListener(v -> setPayStatusApp());
        dialogPay.setContentView(view1);
    }

    @Override
    public void requestResult(String result, String url, Object o) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                if (ATConstants.Config.SERVER_URL_FINDAPPOINTMENTSTATUSWUYE.equals(url) && (Integer) o == 0) {
                    List<ATShareAppointRecordBean> atShareAppointBeanList = gson.fromJson(jsonResult.getString("result"), new TypeToken<List<ATShareAppointRecordBean>>() {
                    }.getType());
                    if (pageNo == 1)
                        mAtShareAppointBeanList.clear();
                    if (atShareAppointBeanList.size() == 0) {
                        if (pageNo != 1)
                            pageNo--;
                        smartRefreshLayout.setNoMoreData(true);
                        return;
                    }
                    mAtShareAppointBeanList.addAll(atShareAppointBeanList);
                    mATShareAppointRecordRVAdapter.setLists(mAtShareAppointBeanList);
                } else if (ATConstants.Config.SERVER_URL_SETPAYSTATUSAPP.equals(url)) {
                    showToast(getString(R.string.at_sure_appoint_pay_success));
                    mAtShareAppointBeanList.get(mPosition).setPayStatus(1);
                    mATShareAppointRecordRVAdapter.setLists(mAtShareAppointBeanList);
                    dialogPay.dismiss();
                } else if (ATConstants.Config.SERVER_URL_SETAPPOINTMENTSTATUSAPP.equals(url)) {
                    if (appointmentStatus == 3 || appointmentStatus == 4 || appointmentStatus == 5)
                        showToast(ATResourceUtils.getString(ATResourceUtils.getResIdByName(String.format(
                            getString(R.string.at_sure_appoint_operate_success), appointmentStatus), ATResourceUtils.ResourceType.STRING)));
                    if (appointmentStatus == 5)
                        mAtShareAppointBeanList.get(mPosition).setAppointmentStatus(appointmentStatus);
                    else
                        mAtShareAppointBeanList.remove(mPosition);
                    mATShareAppointRecordRVAdapter.setLists(mAtShareAppointBeanList);
                    EventBus.getDefault().post(new ATEventClazz("ATAppointRecordHandleFragment"));
                    dialog.dismiss();
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            smartRefreshLayout.finishRefresh();
            smartRefreshLayout.finishLoadMore();
            closeBaseProgressDlg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        smartRefreshLayout.autoRefresh();
    }
}