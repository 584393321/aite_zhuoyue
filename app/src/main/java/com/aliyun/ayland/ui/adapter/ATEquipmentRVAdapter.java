package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATDeviceBean;
import com.aliyun.ayland.data.ATEventInteger2;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.interfaces.ATOnIntegerClickListener;
import com.aliyun.ayland.interfaces.ATOnRVItemClickListener;
import com.aliyun.ayland.ui.activity.ATIntelligentMonitorActivity;
import com.aliyun.ayland.widget.ATSwitchButton;
import com.aliyun.iot.aep.component.router.Router;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ATEquipmentRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATDeviceBean> list = new ArrayList<>();
    private int specs, current = 0;
    private long clickTime = 0;
    private boolean slow;
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.common)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ATEquipmentRVAdapter(Activity context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_equipment_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.setData(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setLists(List<ATDeviceBean> list, int current) {
        this.current = current;
        this.list.clear();
        this.list.addAll(list);
        if (current != 0)
            this.list.add(new ATDeviceBean());
        notifyDataSetChanged();
    }

    public void setCheck(List<ATDeviceBean> list, int current_position) {
        this.list.clear();
        this.list.addAll(list);
        slow = true;
        notifyItemChanged(current_position);
    }

    public void notifyItemChanged(List<ATDeviceBean> list, int current_position, String s) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvName, mTvCenter, mTvSpecs, mTvStatus;
        private ImageView mImgDevice, mImgReduce, mImgIncrease, mImgCurtainOpen, mImgCurtainClose, mImgCurtainStop;
        private RelativeLayout mRlContent, mRlAddReduce, mRlCurtain, mRlSwitchbutton;
        private ATSwitchButton mSwitchview;
        private LinearLayout mLlRoomManage;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            mImgDevice = itemView.findViewById(R.id.img_device);
            mTvName = itemView.findViewById(R.id.tv_name);
            mTvSpecs = itemView.findViewById(R.id.tv_specs);
            mSwitchview = itemView.findViewById(R.id.switchview);
            mRlAddReduce = itemView.findViewById(R.id.rl_add_reduce);
            mImgReduce = itemView.findViewById(R.id.img_reduce);
            mTvCenter = itemView.findViewById(R.id.tv_center);
            mImgIncrease = itemView.findViewById(R.id.img_increase);
            mTvStatus = itemView.findViewById(R.id.tv_status);
            mRlCurtain = itemView.findViewById(R.id.rl_curtain);
            mImgCurtainOpen = itemView.findViewById(R.id.img_curtain_open);
            mImgCurtainStop = itemView.findViewById(R.id.img_curtain_stop);
            mImgCurtainClose = itemView.findViewById(R.id.img_curtain_close);
            mLlRoomManage = itemView.findViewById(R.id.ll_room_manage);
            mRlSwitchbutton = itemView.findViewById(R.id.rl_switchbutton);
        }

        public void setData(int position) {
            if (current != 0 && position == list.size() - 1) {
                mLlRoomManage.setVisibility(View.VISIBLE);
                mLlRoomManage.setOnClickListener(view -> {
                    listener.onItemClick(view, position);
                });
            } else {
                mLlRoomManage.setVisibility(View.GONE);
                if (list.get(position).getAttributes().size() > 0) {
                    mTvSpecs.setText(list.get(position).getAttributes().get(0).getName());
                    final int i = position;
                    if (!TextUtils.isEmpty(list.get(position).getAttributes().get(0).getDataType()))
                        switch (list.get(position).getAttributes().get(0).getDataType()) {
                            case "BOOL":
                            case "bool":
                                mSwitchview.setVisibility(View.VISIBLE);
                                mTvStatus.setVisibility(View.GONE);
                                mRlAddReduce.setVisibility(View.GONE);
                                mRlCurtain.setVisibility(View.GONE);
                                if (slow)
                                    mSwitchview.setChecked("1".equals(list.get(position).getAttributes().get(0).getValue()));
                                else
                                    mSwitchview.setCheckedImmediately("1".equals(list.get(position).getAttributes().get(0).getValue()));
                                slow = false;
                                mRlSwitchbutton.setOnClickListener(view -> {
                                    if ((System.currentTimeMillis() - clickTime) > 500) {
                                        clickTime = System.currentTimeMillis();
                                        EventBus.getDefault().post(new ATEventInteger2("EquipmentActivity", i, mSwitchview.isChecked() ? 0 : 1));
                                    }
                                });
                                break;
                            case "ENUM":
                            case "enum":
                                if ("a1Z9cgRNvul".equals(list.get(position).getProductKey())) {
                                    //窗帘
                                    mSwitchview.setVisibility(View.GONE);
                                    mRlAddReduce.setVisibility(View.GONE);
                                    mRlCurtain.setVisibility(View.VISIBLE);
                                    mTvStatus.setVisibility(View.GONE);

                                    mImgCurtainOpen.setOnClickListener(view -> EventBus.getDefault().post(new ATEventInteger2("EquipmentActivity", i, 0)));
                                    mImgCurtainClose.setOnClickListener(view -> EventBus.getDefault().post(new ATEventInteger2("EquipmentActivity", i, 1)));
                                    mImgCurtainStop.setOnClickListener(view -> EventBus.getDefault().post(new ATEventInteger2("EquipmentActivity", i, 2)));
                                }
                                break;
                            case "INT":
                            case "DOUBLE":
                            case "FLOAT":
                            case "int":
                            case "double":
                            case "float":
                                mSwitchview.setVisibility(View.GONE);
                                mRlAddReduce.setVisibility(View.VISIBLE);
                                mTvStatus.setVisibility(View.GONE);
                                mRlCurtain.setVisibility(View.GONE);
                                mTvCenter.setText(list.get(position).getAttributes().get(0).getValue() + list.get(position).getAttributes().get(0).getSpecs().getString("unit"));

                                mImgIncrease.setOnClickListener(view -> {
                                    specs = Integer.parseInt(mTvCenter.getText().toString().replace(list.get(position).getAttributes().get(0).getSpecs().getString("unit"), ""))
                                            + Integer.parseInt(list.get(position).getAttributes().get(0).getSpecs().getString("step"));
                                    if (specs > Integer.parseInt(list.get(position).getAttributes().get(0).getSpecs().getString("max"))) {
                                        return;
                                    }
                                    mTvCenter.setText(specs + list.get(position).getAttributes().get(0).getSpecs().getString("unit"));
                                    EventBus.getDefault().post(new ATEventInteger2("EquipmentActivity", i, specs));
                                });
                                mImgReduce.setOnClickListener(view -> {
                                    specs = Integer.parseInt(mTvCenter.getText().toString().replace(list.get(position).getAttributes().get(0).getSpecs().getString("unit"), ""))
                                            - Integer.parseInt(list.get(position).getAttributes().get(0).getSpecs().getString("step"));
                                    if (specs < Integer.parseInt(list.get(position).getAttributes().get(0).getSpecs().getString("min"))) {
                                        return;
                                    }
                                    mTvCenter.setText(specs + list.get(position).getAttributes().get(0).getSpecs().getString("unit"));
                                    EventBus.getDefault().post(new ATEventInteger2("EquipmentActivity", i, specs));
                                });
                                break;
                            default:
                                break;
                        }
                } else {
                    mTvSpecs.setText("");
                    mSwitchview.setVisibility(View.GONE);
                    mRlAddReduce.setVisibility(View.GONE);
                    mRlCurtain.setVisibility(View.GONE);
                    mTvStatus.setVisibility(View.VISIBLE);
                }
                if (list.get(position).getStatus() != 1) {
                    mTvSpecs.setText(mContext.getString(R.string.at_off_line));
                    mTvSpecs.setTextColor(mContext.getResources().getColor(R.color._FF5656));
                    mTvStatus.setText(mContext.getString(R.string.at_off));
                } else {
                    mTvSpecs.setText(mContext.getString(R.string.at_online));
                    mTvSpecs.setTextColor(mContext.getResources().getColor(R.color._999999));
                    mTvStatus.setText(mContext.getString(R.string.at_on));
                }
                mTvName.setText(TextUtils.isEmpty(list.get(position).getNickName())
                        ? list.get(position).getProductName() : list.get(position).getNickName());
                Glide.with(mContext)
                        .load(list.get(position).getMyImage())
                        .apply(options)
                        .into(mImgDevice);
                mRlContent.setOnClickListener(view -> {
                    if (ATConstants.ProductKey.CAMERA_HAIKANG.equals(list.get(position).getProductKey())
                            || ATConstants.ProductKey.CAMERA_AITE.equals(list.get(position).getProductKey())
                            || ATConstants.ProductKey.CAMERA_XIAOMIYAN.equals(list.get(position).getProductKey())
                            || ATConstants.ProductKey.CAMERA_IVP.equals(list.get(position).getProductKey())
                            || "Camera".equals(list.get(position).getCategoryKey())) {
                        mContext.startActivity(new Intent(mContext, ATIntelligentMonitorActivity.class)
                                .putExtra("productKey", list.get(position).getProductKey())
                                .putExtra("iotId", list.get(position).getIotId()));
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("iotId", list.get(position).getIotId());
                        Router.getInstance().toUrl(mContext, "link://router/" + list.get(position).getProductKey(), bundle);
                    }
                });
                mRlContent.setLongClickable(true);
                mRlContent.setOnLongClickListener(v -> {
                    onIntegerClickListener.onItemClick(position);
                    return true;
                });
            }
        }
    }

    private ATOnRVItemClickListener listener;

    public void setOnRVClickListener(ATOnRVItemClickListener listener) {
        this.listener = listener;
    }

    private ATOnIntegerClickListener onIntegerClickListener;

    public void addItemLongClickListener(ATOnIntegerClickListener onIntegerClickListener){
        this.onIntegerClickListener = onIntegerClickListener;
    }
}