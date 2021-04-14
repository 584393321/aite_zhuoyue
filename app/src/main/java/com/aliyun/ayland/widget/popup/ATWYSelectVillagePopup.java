package com.aliyun.ayland.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.AllVillageDetailBean;
import com.aliyun.ayland.widget.popup.base.ATBasePopupWindow;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ATWYSelectVillagePopup<T> extends ATBasePopupWindow {
    private Activity context;
    private View popupView;
    private InnerPopupAdapter mAdapter;
    private int rank = 0;
    private String name = "", code = "", mAllVillageList;
    private HashMap<Integer, Integer> hashMap = new HashMap<>();

    public ATWYSelectVillagePopup(Activity context, String allVillageList, HashMap<Integer, Integer> hashMap) {
        super(context);
        setAdjustInputMethod(true);
        setBackPressEnable(false);
        setPopupWindowFullScreen(true);
        setDismissWhenTouchOuside(true);
        mAllVillageList = allVillageList;
        this.hashMap = hashMap;

        findViewById(R.id.imageView).setOnClickListener(v -> {
            if (rank == 0) {
                dismiss();
            } else {
                hashMap.put(rank, 0);
                rank--;
                mAdapter.setItemList(getCurrentList(rank, -1), hashMap.get(rank));
            }
        });
        ListView listView = (ListView) findViewById(R.id.listView);
        mAdapter = new InnerPopupAdapter(context);
        mAdapter.setItemList(getCurrentList(rank, -1), -1);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            hashMap.put(rank, position);
            rank++;
            hashMap.put(rank, 0);
            if (getCurrentList(rank, position) == null || getCurrentList(rank, position).size() == 0) {
                dismiss();
                mOnClickListener.onItemClick(hashMap, rank, position);
            } else
                mAdapter.setItemList(getCurrentList(rank, position), -1);
        });
    }

    private List<AllVillageDetailBean> getCurrentList(Integer rank, Integer position) {
        String allVillageList = mAllVillageList;
        List<AllVillageDetailBean> list = new ArrayList<>();
        List<String> keyList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        Iterator<String> it;
        try {
            for (int i = 0; i < rank + 1; i++) {
                org.json.JSONArray jsonArray = new org.json.JSONArray(allVillageList);
                list.clear();
                list = new ArrayList<>();
                int j = -1;
                do {
                    j++;
                    for (int k = 0; k < jsonArray.length(); k++) {
                        keyList.clear();
                        valueList.clear();
                        org.json.JSONObject jsonObject = jsonArray.getJSONObject(k);
                        it = jsonObject.keys();
                        AllVillageDetailBean allVillageDetailBean = new AllVillageDetailBean();
                        while (it.hasNext()) {
                            keyList.add(it.next());
                        }
                        it = jsonObject.keys();
                        while (it.hasNext()) {
                            valueList.add(jsonObject.getString(it.next()));
                        }
                        for (int l = 0; l < keyList.size(); l++) {
                            if ("name".equals(keyList.get(l))) {
                                allVillageDetailBean.setName(valueList.get(l));
                            } else if ("buildingCode".equals(keyList.get(l))) {
                                allVillageDetailBean.setCode(valueList.get(l));
                            } else {
                                allVillageDetailBean.setString(valueList.get(l));
                            }
                        }
                        list.add(allVillageDetailBean);
                    }
                } while (!(position == -1 || j == hashMap.get(i).intValue() || (i == rank && j == position)));
                Log.e("weis", i + "---");
                Log.e("weis", hashMap.get(i).intValue() + "----");
                Log.e("weis", "----" + list.get(hashMap.get(i).intValue()));
                allVillageList = list.get(hashMap.get(i).intValue()).getString();
                if (TextUtils.isEmpty(allVillageList))
                    return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected Animation initShowAnimation() {
        return getTranslateAnimation(250 * 2, 0, 300);
    }

    @Override
    protected Animation initExitAnimation() {
        return getTranslateAnimation(0, 250 * 2, 300);
    }

    @Override
    public View getClickToDismissView() {
        return popupView.findViewById(R.id.click_to_dismiss);
    }

    @Override
    public View onCreatePopupView() {
        popupView = LayoutInflater.from(getContext()).inflate(R.layout.at_popup_select_village, null);
        return popupView;
    }

    @Override
    public View initAnimaView() {
        return popupView.findViewById(R.id.popup_container);
    }

    private static class InnerPopupAdapter<T> extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;
        private List<AllVillageDetailBean> list = new ArrayList<>();
        private int mPosition = -1;

        private InnerPopupAdapter(Context context) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        private void setItemList(List<AllVillageDetailBean> itemList, int position) {
            list = itemList;
            mPosition = position;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public AllVillageDetailBean getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            InnerPopupAdapter.ViewHolder vh;
            if (convertView == null) {
                vh = new InnerPopupAdapter.ViewHolder();
                convertView = mInflater.inflate(R.layout.at_item_lv_select_building, parent, false);
                vh.mTvName = convertView.findViewById(R.id.tv_name);
                vh.mImgCheck = convertView.findViewById(R.id.img_check);
                convertView.setTag(vh);
            } else {
                vh = (InnerPopupAdapter.ViewHolder) convertView.getTag();
            }
            vh.mTvName.setText(list.get(position).getName());
            if (mPosition == position) {
                vh.mTvName.setTextColor(mContext.getResources().getColor(R.color._5F7EE1));
                vh.mImgCheck.setVisibility(View.VISIBLE);
            } else {
                vh.mTvName.setTextColor(mContext.getResources().getColor(R.color._515669));
                vh.mImgCheck.setVisibility(View.GONE);
            }
            ATAutoUtils.autoSize(convertView);
            return convertView;
        }

        class ViewHolder {
            private TextView mTvName;
            private ImageView mImgCheck;
        }
    }

    private OnClickListener mOnClickListener = null;

    public interface OnClickListener {
        void onItemClick(HashMap<Integer, Integer> hashMap, int rank, int position);
    }

    public void setOnItemClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }
}