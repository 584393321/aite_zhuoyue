package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATDeviceManageBean;
import com.aliyun.ayland.ui.activity.ATDeviceManageSharedToActivity;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static com.aliyun.ayland.ui.activity.ATDeviceManageToActivity.REQUEST_CODE_CHANGE;

public class ATDeviceManageRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private boolean checkable;
    private int type;
    private HashSet<Integer> checkSet = new HashSet<>();
    private List<ATDeviceManageBean> list = new ArrayList<>();
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.zhihui_ico_common)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ATDeviceManageRVAdapter(Activity context, int type) {
        mContext = context;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_unbind_device, parent, false);
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

    public void setList(List<ATDeviceManageBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setCheckable(boolean checkable) {
        this.checkable = checkable;
        notifyDataSetChanged();
    }

    public HashSet<Integer> getCheckSet() {
        return checkSet;
    }

    public void remove(int position) {
        list.remove(position);
        checkSet.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private ImageView img, imgAddDelete;
        private CheckBox checkBox;
        private ImageView imgJump;
        private LinearLayout llBind;
        private RelativeLayout rlContent;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            img = itemView.findViewById(R.id.img);
            tvName = itemView.findViewById(R.id.tv_name);
            imgAddDelete = itemView.findViewById(R.id.img_add_delete);
            llBind = itemView.findViewById(R.id.ll_bind);
            rlContent = itemView.findViewById(R.id.rl_content);
            checkBox = itemView.findViewById(R.id.checkBox);
            imgJump = itemView.findViewById(R.id.img_jump);
        }

        public void setData(int position) {
            tvName.setText(list.get(position).getProductName());
            Glide.with(mContext)
                    .load(list.get(position).getMyImage())
                    .apply(options)
                    .into(img);
            switch (type){
                case 1:
                    llBind.setVisibility(View.VISIBLE);
                    imgJump.setVisibility(View.GONE);
                    imgAddDelete.setImageDrawable(mContext.getResources().getDrawable(R.drawable.common_icon_add_n));
                    llBind.setOnClickListener(view -> mOnItemClickListener.onItemClick(view, position));
                    break;
                case 2:
                    llBind.setVisibility(View.GONE);
                    imgJump.setVisibility(View.GONE);
                    rlContent.setOnClickListener(view -> {
                        if(checkBox.isChecked()){
                            checkSet.remove(position);
                        }else {
                            checkSet.add(position);
                        }
                        notifyItemChanged(position);
                    });
                    if(checkable){
                        rlContent.setClickable(true);
                        checkBox.setVisibility(View.VISIBLE);
                    }else {
                        rlContent.setClickable(false);
                        checkBox.setVisibility(View.GONE);
                    }
                    break;
                case 3:
                    llBind.setVisibility(View.GONE);
                    imgJump.setVisibility(View.VISIBLE);
                    rlContent.setOnClickListener(view -> mContext.startActivityForResult(new Intent(mContext, ATDeviceManageSharedToActivity.class)
                            .putParcelableArrayListExtra("sharedUsers", (ArrayList<? extends Parcelable>) list.get(position).getSharedUsers())
                            .putExtra("position", position).putExtra("type", type)
                            .putExtra("iotId", list.get(position).getIotId()), REQUEST_CODE_CHANGE));
                    break;
                case 4:
                    llBind.setVisibility(View.VISIBLE);
                    imgJump.setVisibility(View.GONE);
                    imgAddDelete.setImageResource(R.drawable.common_icon_delete_n);
                    llBind.setOnClickListener(view -> mOnItemClickListener.onItemClick(view, position));
                    break;
            }
            checkBox.setChecked(checkSet.contains(position));
        }
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
