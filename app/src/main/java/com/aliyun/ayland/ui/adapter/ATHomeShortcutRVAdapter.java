package com.aliyun.ayland.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATSceneManualAutoBean;
import com.aliyun.ayland.data.ATShortcutBean;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ATHomeShortcutRVAdapter extends RecyclerView.Adapter {
    private Activity mContext;
    private List<ATShortcutBean> list = new ArrayList<>();
    private Handler handler = new Handler();
    private Vibrator mVibrator;
    private TranslateAnimation mTranslateAnimation;
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.common)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    public ATHomeShortcutRVAdapter(Activity context) {
        mContext = context;
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.at_item_rv_home_card, parent, false);
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

    public void setLists(List<ATShortcutBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void setShowing(int position, int showing) {
        list.get(position).setIsShowing(showing);
        if (showing == ATSceneManualAutoBean.SHOWING)
            notifyItemChanged(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvName;
        private RelativeLayout mLlContent;
        private ImageView mImg, mImgAni;
        ;

        public ViewHolder(View itemView) {
            super(itemView);
            ATAutoUtils.autoSize(itemView);
            mLlContent = itemView.findViewById(R.id.rl_content);
            mTvName = itemView.findViewById(R.id.tv_name);
            mImg = itemView.findViewById(R.id.img);
            mImgAni = itemView.findViewById(R.id.img_ani);
        }

        public void setData(int position) {
            mTvName.setText(list.get(position).getItemName());
            if (list.get(position).getShortcutType() == 2) {
                mImg.setImageResource(R.drawable.zhihui_ico_liandong);
                if (list.get(position).getIsShowing() == ATSceneManualAutoBean.SHOWING) {
                    animotionStart(mImgAni, mImg);
                }
            }else {
                Glide.with(mContext).load(list.get(position).getItemIcon()).apply(options).into(mImg);
            }
            mLlContent.setOnClickListener(view -> {
                if (mVibrator != null)
                    mVibrator.vibrate(200);
                setShowing(position, ATSceneManualAutoBean.SHOWING);
                mOnItemClickListener.onItemClick(view, position);
            });
        }

        // 动画启动
        private void animotionStart(ImageView imgView, ImageView imgView1) {
            imgView.setVisibility(View.VISIBLE);
            mTranslateAnimation = new TranslateAnimation(0, 600, 0, 0);
            imgView.setAnimation(mTranslateAnimation);
            mTranslateAnimation.setDuration(2000);
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

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
