package com.aliyun.ayland.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.utils.ATCameraUtil;
import com.anthouse.wyzhuoyue.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ATMyCarAddOneFragment extends ATBaseFragment {
    private static final int PERMISSION_READ_EXTERNAL_STORAGE = 1001;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 1002;
    private static final int RESULT_LOAD_IMAGE = 10;
    private ImageView imgAddPictureOk;
    private Context mContext;

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_add_car_one;
    }

    @Override
    protected void initPresenter() {
    }

    @Override
    protected void findView(View view) {
        imgAddPictureOk = view.findViewById(R.id.img_add_picture_ok);
        mContext = getContext();
        init();
    }

    private void init() {
        imgAddPictureOk.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
            } else {
                ATCameraUtil.getInstance().startCamera(getActivity(), RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE) {
                Bitmap bitmap1 = com.aliyun.ayland.utils.BitmapUtils.saveBefore(data.getStringExtra("IntentKeyFilePath"));
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 80, out);
                    byte[] data1 = out.toByteArray();
                    Bitmap bitmap = com.baidu.idl.face.platform.utils.BitmapUtils.createBitmap(getActivity(), data1, 0);
                    out.close();
                    imgAddPictureOk.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showToast("开启权限后方可进行拍照操作");
                }
                break;
            case PERMISSION_READ_EXTERNAL_STORAGE:
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    showToast("开启权限后方可进行读取相册操作");
                }
            default:
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}