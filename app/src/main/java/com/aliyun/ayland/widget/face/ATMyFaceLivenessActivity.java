package com.aliyun.ayland.widget.face;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity1;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.baidu.aip.face.stat.Ast;
import com.baidu.idl.face.platform.FaceConfig;
import com.baidu.idl.face.platform.FaceSDKManager;
import com.baidu.idl.face.platform.FaceStatusEnum;
import com.baidu.idl.face.platform.ILivenessStrategy;
import com.baidu.idl.face.platform.ILivenessStrategyCallback;
import com.baidu.idl.face.platform.ui.FaceSDKResSettings;
import com.baidu.idl.face.platform.ui.utils.CameraUtils;
import com.baidu.idl.face.platform.ui.utils.VolumeUtils;
import com.baidu.idl.face.platform.ui.widget.FaceDetectRoundView;
import com.baidu.idl.face.platform.utils.APIUtils;
import com.baidu.idl.face.platform.utils.BitmapUtils;
import com.baidu.idl.face.platform.utils.CameraPreviewUtils;
import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * 活体检测界面
 */
public class ATMyFaceLivenessActivity extends ATBaseActivity1 implements ATMainContract.View,
        SurfaceHolder.Callback,
        Camera.PreviewCallback,
        Camera.ErrorCallback,
        VolumeUtils.VolumeCallback,
        ILivenessStrategyCallback {
    public static final String TAG = ATMyFaceLivenessActivity.class.getSimpleName();
    private ATMainPresenter mPresenter;
    // View
    protected SurfaceView mSurfaceView;
    protected SurfaceHolder mSurfaceHolder;
    // 人脸信息
    protected FaceConfig mFaceConfig;
    protected ILivenessStrategy mILivenessStrategy;
    // 显示Size
    private Rect mPreviewRect = new Rect();
    protected int mDisplayWidth = 0;
    protected int mDisplayHeight = 0;
    protected int mSurfaceWidth = 0;
    protected int mSurfaceHeight = 0;
    protected Drawable mTipsIcon;
    // 状态标识
    protected HashMap<String, String> mBase64ImageMap = new HashMap<>();
    protected boolean mIsCreateSurface = false;
    protected boolean mIsCompletion = false;
    // 相机
    protected Camera mCamera;
    protected Camera.Parameters mCameraParam;
    protected int mCameraId;
    protected int mPreviewWidth;
    protected int mPreviewHight;
    protected int mPreviewDegree;
    private String imageBase64 = "", userId;
    private Intent intent;
    private boolean mRefresh;
    private double mDouble;
    private ATMyTitleBar titleBar;
    private FrameLayout livenessSurfaceLayout;
    private Button btnLogging;
    private ImageView imgResult, livenessSuccessImage;
    private TextView livenessBottomTips;
    private FaceDetectRoundView livenessFaceRound;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_my_face_liveness;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        livenessSurfaceLayout = findViewById(R.id.liveness_surface_layout);
        btnLogging = findViewById(R.id.btn_logging);
        imgResult = findViewById(R.id.img_result);
        livenessBottomTips = findViewById(R.id.liveness_bottom_tips);
        livenessFaceRound = findViewById(R.id.liveness_face_round);
        livenessSuccessImage = findViewById(R.id.liveness_success_image);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void deleteFace() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userType", "OPEN");
        jsonObject.put("userId", TextUtils.isEmpty(userId) ? ATGlobalApplication.getHid() : userId);
        mPresenter.request(ATConstants.Config.SERVER_URL_DELETEFACE, jsonObject);
    }

    private void addFace() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userType", "OPEN");
        jsonObject.put("userId", TextUtils.isEmpty(userId) ? ATGlobalApplication.getHid() : userId);
        jsonObject.put("imageBase64", imageBase64);
//        jsonObject.put("userName", "");
//        jsonObject.put("expiredTime", "");//过期时间
        mPresenter.request(ATConstants.Config.SERVER_URL_ADDFACE, jsonObject);
    }

    private void init() {
        userId = getIntent().getStringExtra("personCode");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        DisplayMetrics dm = new DisplayMetrics();
        Display display = this.getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);
        mDisplayWidth = dm.widthPixels;
        mDisplayHeight = dm.heightPixels;

        FaceSDKResSettings.initializeResId();
        mFaceConfig = FaceSDKManager.getInstance().getFaceConfig();

        intent = getIntent();
        mRefresh = intent.getBooleanExtra("refresh", false);

        mSurfaceView = new SurfaceView(this);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.setSizeFromLayout();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        int w = mDisplayWidth;
        int h = mDisplayHeight;
        FrameLayout.LayoutParams cameraFL = new FrameLayout.LayoutParams(
                (int) (w * FaceDetectRoundView.SURFACE_RATIO), (int) (h * FaceDetectRoundView.SURFACE_RATIO),
                Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        mSurfaceView.setLayoutParams(cameraFL);
        livenessSurfaceLayout.addView(mSurfaceView);

        titleBar.getLlTitleContent().setPadding(0, 0, 0, 0);
        titleBar.setRightButtonText(getString(R.string.at_done));
        titleBar.showRightButton(false);
        titleBar.setRightClickListener(() -> {
            if ("删除".equals(titleBar.getSendText())) {
                deleteFace();
            } else {
                addFace();
            }
        });
        if (mBase64ImageMap != null) {
            mBase64ImageMap.clear();
        }
        mSurfaceView.setVisibility(View.VISIBLE);
        if (mRefresh) {
            imgResult.setVisibility(View.GONE);
            btnLogging.setClickable(false);
            btnLogging.setText(R.string.at_logging);
            btnLogging.setBackground(getResources().getDrawable(R.drawable.at_shape_66px_aaaaaa));
        } else {
            String imageUrl = intent.getStringExtra("imageUrl");
            if (!TextUtils.isEmpty(imageUrl)) {
                titleBar.showRightButton(true);
                titleBar.setRightButtonText(getString(R.string.at_delete));
                Glide.with(this).load(imageUrl).into(imgResult);
                imgResult.setVisibility(View.VISIBLE);
                btnLogging.setText(getString(R.string.at_logging_again));
                btnLogging.setBackground(getResources().getDrawable(R.drawable.at_selector_66px_1478c8_005395));
                btnLogging.setClickable(true);
                mSurfaceView.setVisibility(View.GONE);
            }
        }
        btnLogging.setOnClickListener(v -> {
            if (getString(R.string.at_logging).equals(btnLogging.getText().toString()))
                return;
            finish();
            intent.putExtra("refresh", true);
            intent.setClass(ATMyFaceLivenessActivity.this, ATFaceLivenessExpActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (livenessBottomTips != null) {
            livenessBottomTips.setText(R.string.detect_face_in);
        }
        startPreview();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPreview();
    }

    @Override
    public void onStop() {
        if (mILivenessStrategy != null) {
            mILivenessStrategy.reset();
        }
        super.onStop();
        stopPreview();
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void volumeChanged() {
    }

    private Camera open() {
        Camera camera;
        int numCameras = Camera.getNumberOfCameras();
        if (numCameras == 0) {
            return null;
        }

        int index = 0;
        while (index < numCameras) {
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(index, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                break;
            }
            index++;
        }

        if (index < numCameras) {
            camera = Camera.open(index);
            mCameraId = index;
        } else {
            camera = Camera.open(0);
            mCameraId = 0;
        }
        return camera;
    }

    protected void startPreview() {
        if (mSurfaceView != null && mSurfaceView.getHolder() != null) {
            mSurfaceHolder = mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(this);
        }

        if (mCamera == null) {
            try {
                mCamera = open();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        if (mCameraParam == null) {
            mCameraParam = mCamera.getParameters();
        }

        mCameraParam.setPictureFormat(PixelFormat.JPEG);
        int degree = displayOrientation(this);
        mCamera.setDisplayOrientation(degree);
        // 设置后无效，camera.setDisplayOrientation方法有效
        mCameraParam.set("rotation", degree);
        mPreviewDegree = degree;

        Point point = CameraPreviewUtils.getBestPreview(mCameraParam,
                new Point(mDisplayWidth, mDisplayHeight));

        mPreviewWidth = point.x;
        mPreviewHight = point.y;
        // Preview 768,432
        if (mILivenessStrategy != null) {
            mILivenessStrategy.setPreviewDegree(degree);
        }

        mPreviewRect.set(0, 0, mPreviewHight, mPreviewWidth);
        mCameraParam.setPreviewSize(mPreviewWidth, mPreviewHight);
        mCamera.setParameters(mCameraParam);

        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.stopPreview();
            mCamera.setErrorCallback(this);
            mCamera.setPreviewCallback(this);
            mCamera.startPreview();
        } catch (RuntimeException e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mCamera);
            mCamera = null;
        } catch (Exception e) {
            e.printStackTrace();
            CameraUtils.releaseCamera(mCamera);
            mCamera = null;
        }
    }

    protected void stopPreview() {
        if (mCamera != null) {
            try {
                mCamera.setErrorCallback(null);
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CameraUtils.releaseCamera(mCamera);
                mCamera = null;
            }
        }
        if (mSurfaceHolder != null) {
            mSurfaceHolder.removeCallback(this);
        }
        if (mILivenessStrategy != null) {
            mILivenessStrategy = null;
        }
    }

    private int displayOrientation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int rotation = windowManager.getDefaultDisplay().getRotation();
        int degrees;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                degrees = 0;
                break;
        }
        int result = (0 - degrees + 360) % 360;
        if (APIUtils.hasGingerbread()) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                result = (info.orientation + degrees) % 360;
                result = (360 - result) % 360;
            } else {
                result = (info.orientation - degrees + 360) % 360;
            }
        }
        return result;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsCreateSurface = true;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format,
                               int width,
                               int height) {
        mSurfaceWidth = width;
        mSurfaceHeight = height;
        if (holder.getSurface() == null) {
            return;
        }
        startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsCreateSurface = false;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (mIsCompletion) {
            Camera.Size size = camera.getParameters().getPreviewSize();
            try {
                YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Log.e("createBitmap: ", size.width + "--1--" + size.height);
                mDouble = 1;
                if (size.width - 800 > 0) {
                    mDouble = size.width / 800;
                }
                image.compressToJpeg(new Rect(0, 0, size.width, size.height), 80, stream);

                byte[] bytes = stream.toByteArray();
                String deviceModel = Build.MODEL;
                float rotate = -90;
                if ("Nexus 6".equals(deviceModel)) {
                    rotate = 90;
                }
                stream.close();
                Bitmap bitmap2 = BitmapUtils.createBitmap(this, bytes, rotate);
//                    Bitmap bitmap1 = Bitmap.createBitmap(bitmap2, 0, (int)(bitmap2.getHeight() - bitmap2.getWidth() * 4 / 3) / 2,
//                            (int)(bitmap2.getWidth() * mDouble), (int)(bitmap2.getWidth() * 4 / 3 * mDouble));
                Bitmap bitmap1 = Bitmap.createBitmap(bitmap2, 0, (bitmap2.getHeight() - bitmap2.getWidth() * 4 / 3) / 2,
                        bitmap2.getWidth(), bitmap2.getWidth() * 4 / 3);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 80, out);
                byte[] data1 = out.toByteArray();
                Bitmap bitmap = BitmapUtils.createBitmap(this, data1, 0);
                out.close();

                Log.e("createBitmap: ", bitmap.getWidth() + "--3--" + bitmap.getHeight());

                bitmap = mirrorConvert(bitmap, 0);
                imageBase64 = BitmapUtils.bitmapToJpegBase64(bitmap, 80);
                imgResult.setImageBitmap(bitmap);
                imgResult.setVisibility(View.VISIBLE);
                livenessBottomTips.setText(getString(R.string.at_click_right_top));

                btnLogging.setClickable(true);
                btnLogging.setText(R.string.at_logging_again);
                btnLogging.setCompoundDrawables(null, null, null, null);
                btnLogging.setBackground(getResources().getDrawable(R.drawable.at_selector_66px_1478c8_005395));
            } catch (Exception ex) {
                Log.e("Sys", "Error:" + ex.getMessage());
            }
            return;
        }

        if (mILivenessStrategy == null) {
            mILivenessStrategy = FaceSDKManager.getInstance().getLivenessStrategyModule();
            mILivenessStrategy.setPreviewDegree(mPreviewDegree);

            Rect detectRect = FaceDetectRoundView.getPreviewDetectRect(
                    mDisplayWidth, mPreviewHight, mPreviewWidth);
            mILivenessStrategy.setLivenessStrategyConfig(
                    mFaceConfig.getLivenessTypeList(), mPreviewRect, detectRect, this);
        }
        mILivenessStrategy.livenessStrategy(data);
    }

    public Bitmap mirrorConvert(Bitmap srcBitmap, int flag) {
        //flag: 0 左右翻转，1 上下翻转
        Matrix matrix = new Matrix();
        if (flag == 0) //左右翻转
            matrix.setScale(-1, 1);
        if (flag == 1)
            matrix.setScale(1, -1);
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
    }

    @Override
    public void onError(int error, Camera camera) {
    }

    @Override
    public void onLivenessCompletion(FaceStatusEnum status, String message, HashMap<String, String> base64ImageMap) {
        if (mIsCompletion) {
            return;
        }
        onRefreshView(status, message);
        if (status == FaceStatusEnum.OK) {
            mIsCompletion = true;
            titleBar.showRightButton(true);
            startPreview();
            mSurfaceView.setVisibility(View.GONE);
        }
        Ast.getInstance().faceHit("liveness");
    }

    private void onRefreshView(FaceStatusEnum status, String message) {
        switch (status) {
            case OK:
            case Liveness_OK:
            case Liveness_Completion:
                onRefreshTipsView(false, message);
                livenessBottomTips.setText(getString(R.string.at_click_right_top));
                livenessFaceRound.processDrawState(false);
                onRefreshSuccessView(true);
                break;
            case Detect_DataNotReady:
            case Liveness_Eye:
            case Liveness_Mouth:
            case Liveness_HeadUp:
            case Liveness_HeadDown:
            case Liveness_HeadLeft:
            case Liveness_HeadRight:
            case Liveness_HeadLeftRight:
                onRefreshTipsView(false, message);
                livenessFaceRound.processDrawState(false);
                onRefreshSuccessView(false);
                break;
            case Detect_PitchOutOfUpMaxRange:
            case Detect_PitchOutOfDownMaxRange:
            case Detect_PitchOutOfLeftMaxRange:
            case Detect_PitchOutOfRightMaxRange:
                onRefreshTipsView(true, message);
                livenessBottomTips.setText(message);
                livenessFaceRound.processDrawState(true);
                onRefreshSuccessView(false);
                break;
            default:
                onRefreshTipsView(false, message);
                livenessFaceRound.processDrawState(true);
                onRefreshSuccessView(false);
        }
    }

    private void onRefreshTipsView(boolean isAlert, String message) {
        if (isAlert) {
            if (mTipsIcon == null) {
                mTipsIcon = getResources().getDrawable(R.mipmap.ic_warning);
                mTipsIcon.setBounds(0, 0, (int) (mTipsIcon.getMinimumWidth() * 0.7f),
                        (int) (mTipsIcon.getMinimumHeight() * 0.7f));
                livenessBottomTips.setCompoundDrawablePadding(15);
            }
            livenessBottomTips.setBackgroundResource(R.drawable.bg_tips);
            livenessBottomTips.setText(R.string.detect_standard);
            livenessBottomTips.setCompoundDrawables(mTipsIcon, null, null, null);
        } else {
            livenessBottomTips.setBackgroundResource(R.drawable.bg_tips_no);
            livenessBottomTips.setCompoundDrawables(null, null, null, null);
            if (!TextUtils.isEmpty(message)) {
                livenessBottomTips.setText(message);
            }
        }
    }

    private void onRefreshSuccessView(boolean isShow) {
        if (livenessSuccessImage.getTag() == null) {
            Rect rect = livenessFaceRound.getFaceRoundRect();
            RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) livenessSuccessImage.getLayoutParams();
            rlp.setMargins(
                    rect.centerX() - (livenessSuccessImage.getWidth() / 2),
                    rect.top - (livenessSuccessImage.getHeight() / 2),
                    0,
                    0);
            livenessSuccessImage.setLayoutParams(rlp);
            livenessSuccessImage.setTag("setlayout");
        }
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_DELETEFACE:
                        showToast(getString(R.string.at_image_delete_success));
                        finish();
                        break;
                    case ATConstants.Config.SERVER_URL_ADDFACE:
                        showToast(getString(R.string.at_image_uploaded_success));
                        finish();
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