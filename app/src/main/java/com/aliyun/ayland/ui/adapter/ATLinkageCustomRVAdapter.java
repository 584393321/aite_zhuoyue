package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATSceneManualAutoBean;
import com.aliyun.ayland.interfaces.ATOnRVItemClickBooleanListener;
import com.aliyun.ayland.ui.activity.ATLinkageAddActivity;
import com.aliyun.ayland.widget.ATSwitchButton;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import static com.aliyun.ayland.ui.fragment.ATLinkageFragment.REQUEST_CODE_EDIT_LINKAGE;

public class ATLinkageCustomRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATSceneManualAutoBean> list = new ArrayList<>();
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.common_jiazaizhong_a)
            .diskCacheStrategy(DiskCacheStrategy.ALL);
    private TranslateAnimation mTranslateAnimation;
    private int mWidth;

    public ATLinkageCustomRVAdapter(Activity context) {
        mContext = context;
        WindowManager wm1 = context.getWindowManager();
        mWidth = wm1.getDefaultDisplay().getWidth() + 432;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_linkage_custom, parent, false);
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

    public void setLists(List<ATSceneManualAutoBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setShowing(int position, int showing) {
        list.get(position).setIsShowing(showing);
        if (showing == ATSceneManualAutoBean.SHOWING)
            notifyItemChanged(position);
    }

    public void remove(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mRlContent;
        private TextView mTvName;
        private ATSwitchButton mSwitchview;
        private ImageView mImg, mImgAni;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mRlContent = itemView.findViewById(R.id.rl_content);
            mTvName = itemView.findViewById(R.id.textView);
            mSwitchview = itemView.findViewById(R.id.switchview);
            mImg = itemView.findViewById(R.id.img);
            mImgAni = itemView.findViewById(R.id.img_ani);
        }

        public void setData(int position) {
            mTvName.setText(list.get(position).getSceneName());
            if (list.get(position).getSceneType() == 1) {
                mSwitchview.setVisibility(View.GONE);
            } else {
                mSwitchview.setVisibility(View.VISIBLE);
            }
            mRlContent.setOnClickListener(view -> mContext.startActivityForResult(new Intent(mContext, ATLinkageAddActivity.class)
                    .putExtra("sceneId", list.get(position).getSceneId())
                    .putExtra("sceneType", list.get(position).getSceneType()), REQUEST_CODE_EDIT_LINKAGE));
            if (list.get(position).getIsShowing() == ATSceneManualAutoBean.SHOWING) {
                animotionStart(mImgAni);
            }
            mSwitchview.setOnCheckedChangeListener((buttonView, isChecked) -> {

            });
            mSwitchview.setCheckedImmediately("1".equals(list.get(position).getDeployStatus()));
            mSwitchview.setOnCheckedChangeListener((buttonView, isChecked) -> {
                list.get(position).setDeployStatus(isChecked ? "1" : "2");
                mOnItemClickListener.onItemClick(getAdapterPosition(), isChecked);
            });
            Glide.with(mContext).load(list.get(position).getSceneIcon()).apply(options).into(mImg);
        }

        // 动画启动
        private void animotionStart(ImageView imgView) {
            imgView.setVisibility(View.VISIBLE);
            mTranslateAnimation = new TranslateAnimation(0, mWidth, 0, 0);
            imgView.setAnimation(mTranslateAnimation);
            mTranslateAnimation.setDuration(2500);
            mTranslateAnimation.setRepeatCount(2);
            mTranslateAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    imgView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    if (list.get(getAdapterPosition()).getIsShowing() == ATSceneManualAutoBean.NOTSHOW) {
                        animation.cancel();
                    }
                }
            });
            mTranslateAnimation.startNow();
//            Animation scaleAnimation = new ScaleAnimation(0f, 1.5f, 1f, 1.5f,
//                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
//            imgView1.startAnimation(scaleAnimation);
        }
    }

    private ATOnRVItemClickBooleanListener mOnItemClickListener;

    public void setOnItemClickListener(ATOnRVItemClickBooleanListener listener) {
        this.mOnItemClickListener = listener;
    }
}