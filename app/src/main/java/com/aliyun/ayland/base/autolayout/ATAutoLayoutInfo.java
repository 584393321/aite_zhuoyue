package com.aliyun.ayland.base.autolayout;

import android.view.View;

import com.aliyun.ayland.base.autolayout.attr.ATAutoAttr;

import java.util.ArrayList;
import java.util.List;

public class ATAutoLayoutInfo {
    private List<ATAutoAttr> mATAutoAttrs = new ArrayList<ATAutoAttr>();

    public void addAttr(ATAutoAttr autoAttr) {
        mATAutoAttrs.add(autoAttr);
    }

    public void fillAttrs(View view) {
        for (ATAutoAttr autoAttr : mATAutoAttrs) {
            autoAttr.apply(view);
        }
    }

    @Override
    public String toString() {
        return "AutoLayoutInfo{" +
                "autoAttrs=" + mATAutoAttrs +
                '}';
    }
}