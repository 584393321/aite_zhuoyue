package com.aliyun.ayland.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATSpaceSubsribeGymRVAdapter;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class ATSpaceSubsribeGymFragment extends ATBaseFragment implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATSpaceSubsribeGymRVAdapter mSpaceSubsribeGymRVAdapter;
    private ImageView imageView;
    private RecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_space_subscribe_gym;
    }

    @Override
    protected void findView(View view) {
        imageView = view.findViewById(R.id.imageView);
        recyclerView = view.findViewById(R.id.recyclerView);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(getActivity());
    }

    private void init() {
//        Glide.with(this).load(R.drawable.no_banner)
//            .apply(RequestOptions.bitmapTransform(new BlurTransformation(5,2)))
//            .into(imageView);
        mSpaceSubsribeGymRVAdapter = new ATSpaceSubsribeGymRVAdapter(getActivity());
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mSpaceSubsribeGymRVAdapter);
        List<String> list = new ArrayList<>();
        list.add("08:00-12：00");
        list.add("12:00-14：00");
        list.add("14:00-18：00");
        list.add("18:00-22：00");
        list.add("22:00-次日02：00");
        mSpaceSubsribeGymRVAdapter.setList(list);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_ADDUSERFACEVILLAGE:
                        break;
                    case ATConstants.Config.SERVER_URL_FACEVILLAGELIST:
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}