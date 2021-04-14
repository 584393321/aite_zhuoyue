package com.aliyun.ayland.widget;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.utils.ATKeyBoardHeightUtils;
import com.aliyun.ayland.utils.ATKeyBoardUtils;
import com.aliyun.ayland.utils.ATResourcesUtils;

public class ATKeyBoardUI implements ATKeyBoardHeightUtils.KeyBoardHigthListener {

    private EditText edtextVew;//activity的输入框
    private Activity activity;
    private Dialog mDialog;
    private ATNullMenuEditText popuEdtext;//popu的输入框
    private int screenWeight = 0;//屏幕宽度
    private ATKeyBoardHeightUtils keyBoardHeightUtils;

    public static ATKeyBoardUI buildKeyBoardUI(Activity activity) {
        return new ATKeyBoardUI(activity);
    }


    private ATKeyBoardUI(Activity activity) {
        this.activity = activity;
        getScreen();
        initDialog();
        keyBoardHeightUtils = ATKeyBoardHeightUtils.setKeyBoardHeigthListener(activity, this);
    }

    private void getScreen() {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWeight = dm.widthPixels;
    }

    private void initDialog() {
        LayoutInflater inflater = (LayoutInflater) activity.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popuView = inflater.inflate(ATResourcesUtils.getLayoutResources(activity, "at_popuwindow"), null);
        RelativeLayout populay = (RelativeLayout) popuView.findViewById(ATResourcesUtils.getIdResources(activity, "popu_lay"));
        popuEdtext = (ATNullMenuEditText) popuView.findViewById(ATResourcesUtils.getIdResources(activity, "ed_text"));
        mDialog = new Dialog(activity, ATResourcesUtils.getStyleResources(activity, "dialog"));
        mDialog.setContentView(popuView);
        populay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ATKeyBoardUtils.closeKeybord(popuEdtext, activity);
                mDialog.dismiss();
            }
        });

    }

    @Override
    public void keyBoardHigthListener(int heigth, boolean showKeyBoard, View contentView) {
        if (showKeyBoard) {
            if (contentView != null) {
                View childView = contentView.findFocus();
                if (childView != null) {
                    if (childView instanceof EditText) {
                        edtextVew = (EditText) childView;
                        if (!checkViewVisiable()) {
                            mDialog.show();
                            Window dialogWindow = mDialog.getWindow();
                            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
                            p.height = heigth;
                            p.width = screenWeight;
                            dialogWindow.setAttributes(p);
                            dialogWindow.setWindowAnimations(ATResourcesUtils.getStyleResources(activity, "PopupAnimation"));
                            onEdChange();
                            ATKeyBoardUtils.openKeybord(edtextVew, activity);
                        } else {
                            if (mDialog != null) {
                                mDialog.dismiss();
                            }
                        }
                    }
                }
            }
        } else {
            if (mDialog != null) {
                mDialog.dismiss();
            }
        }
    }

    private boolean checkViewVisiable() {
        Rect localRect = new Rect();
        boolean visiable = edtextVew.getLocalVisibleRect(localRect);
        System.out.println("visiable=" + visiable);
        return visiable;
    }

    private void onEdChange() {
        String hintStr = "";
        String text = "";
        if (!TextUtils.isEmpty(edtextVew.getText())) {
            text = edtextVew.getText().toString();
        }
        if (!TextUtils.isEmpty(edtextVew.getHint())) {
            hintStr = edtextVew.getHint().toString();
        }
        popuEdtext.findFocus();
        popuEdtext.setInputType(edtextVew.getInputType());
        popuEdtext.setHint(hintStr);
        popuEdtext.setText(text);
        popuEdtext.setSelection(text.length());
        popuEdtext.setMaxEms(edtextVew.getMaxEms());
        popuEdtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                edtextVew.setText(s);
                edtextVew.setSelection(s.length());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        popuEdtext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == 0) {
                    ATKeyBoardUtils.closeKeybord(popuEdtext, activity);
                    mDialog.dismiss();
                    return true;
                }
                return false;
            }
        });
    }

    public void removeKeyboardHeightListener() {
        keyBoardHeightUtils.removeKeyboardHeightListener();
    }
}