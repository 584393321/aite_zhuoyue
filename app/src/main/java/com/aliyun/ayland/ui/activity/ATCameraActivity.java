package com.aliyun.ayland.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.utils.ATBitmapUtils1;
import com.aliyun.ayland.utils.ATCameraUtil;
import com.aliyun.ayland.utils.ATSystemUtils;
import com.anthouse.wyzhuoyue.R;

import java.io.File;

public class ATCameraActivity extends ATBaseActivity implements ATMainContract.View, SurfaceHolder.Callback {
    private static final String TAG = "----CameraActivity----";
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private int mCameraId = 0;
    private Context context;
    //屏幕宽高
    private int screenWidth;
    private int screenHeight;
    private int picHeight;
    //矩形框的宽高
    private double rectangleW;
    private double rectangleH;
    private int margin = 40;
    //默认宽高比
    private double defaultProportion = 1.56;
    //向外多扩大10像素
    private double expandHeight = 10;
    private double expandWidth = expandHeight * defaultProportion;
    private final static int REUQEST_CODE_PERMISSION_CAMERA = 100;
    private final static int REQUEST_PERMISSION_SETTING = 101;
    private AlertDialog alertDialog;
    private RelativeLayout rlRect;
    private SurfaceView cameraView;
    private ImageView btnTakePicture, btnClose;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_camera;
    }

    @Override
    protected void findView() {
        btnTakePicture = findViewById(R.id.btn_take_picture);
        cameraView = findViewById(R.id.camera_view);
        btnClose = findViewById(R.id.btn_close);
        rlRect = findViewById(R.id.rl_rect);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = this;
        init();
        checkPermission();
    }

    @Override
    protected void requestWindowFeature() {
        //取消标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    protected void initPresenter() {
    }

    private void init() {
        btnTakePicture.setOnClickListener(view -> captrue());
        btnClose.setOnClickListener(view -> finish());

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        margin = ATSystemUtils.dp2px(this, 20);
        rectangleW = screenWidth - margin * 2;
        rectangleH = rectangleW / defaultProportion;

        //设置矩形框的宽高
        ViewGroup.LayoutParams layoutParams = rlRect.getLayoutParams();
        layoutParams.width = (int) rectangleW;
        layoutParams.height = (int) rectangleH;
        rlRect.setLayoutParams(layoutParams);

    }

    /**
     * 初始化预览
     */
    private void initCameraPreview() {
        mHolder = cameraView.getHolder();
        mHolder.addCallback(this);
    }

    //判断是否有权限开启相机
    public boolean isCameraUseable() {
        boolean canUse = true;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            // setParameters 是针对魅族MX5。MX5通过Camera.open()拿到的Camera对象不为null
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
        } catch (Exception e) {
            canUse = false;
        }

        if (mCamera != null) {
            mCamera.release();
        }
        return canUse;

    }

    private void checkPermission() {
        //6.0以下的系统检测权限
        if (this.isCameraUseable()) {
            //有权限
            initCameraPreview();
        } else {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                //拒绝授权
                alertDialog = new AlertDialog.Builder(this)
                        .setTitle("申请权限")
                        .setCancelable(false)
                        .setMessage("请到设置界面允许拍照,否则将不能正常使用当前功能")
                        .setPositiveButton("跳转设置", (dialogInterface, i) -> {
                            //申请权限
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, REQUEST_PERMISSION_SETTING);

                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("关闭", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            //拒绝授权
                            ATCameraActivity.this.finish();
                        }).show();
            } else {
                //只被拒绝过一次该权限的申请
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REUQEST_CODE_PERMISSION_CAMERA);
            }
        }
    }

    @Override
    public void requestResult(String result, String url) {
    }

    /**
     * 获取Camera实例
     *
     * @return
     */
    private Camera getCamera(int id) {
        Camera camera = null;
        try {
            camera = Camera.open(id);
        } catch (Exception e) {

        }
        return camera;
    }

    /**
     * 预览相机
     */
    private void startPreview(Camera camera, SurfaceHolder holder) {
        if (camera == null) {
            return;
        }
        try {
            setupCamera(camera);
            camera.setPreviewDisplay(holder);
            //亲测的一个方法 基本覆盖所有手机 将预览矫正
            ATCameraUtil.getInstance().setCameraDisplayOrientation(this, mCameraId, camera);
//            camera.setDisplayOrientation(90);
            camera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void captrue() {
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //将data 转换为位图 或者你也可以直接保存为文件使用 FileOutputStream
                //这里我相信大部分都有其他用处把 比如加个水印 后续再讲解
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                Bitmap saveBitmap = ATCameraUtil.getInstance().setTakePicktrueOrientation(mCameraId, bitmap);

                saveBitmap = Bitmap.createScaledBitmap(saveBitmap, screenWidth, picHeight, true);

                //这里打印宽高 就能看到 CameraUtil.getInstance().getPropPictureSize(parameters.getSupportedPictureSizes(), 200);
                // 这设置的最小宽度影响返回图片的大小 所以这里一般这是1000左右把我觉得

                //获取自定义宽高
                double imageH = rectangleH + expandHeight * 2;
                double imageW = rectangleW + expandWidth * 2;
                double imageX = (screenWidth - imageW) / 2;
                double imageY = (screenHeight - imageH) / 2;

                saveBitmap = Bitmap.createBitmap(saveBitmap, (int) imageX, (int) imageY, (int) imageW, (int) imageH);

                String img_path = getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath() +
                        File.separator + System.currentTimeMillis() + ".jpeg";
                ATBitmapUtils1.saveJPGE_After(context, saveBitmap, img_path, 100);

                //返回
                Intent intent = new Intent();
                intent.putExtra(ATConstants.IntentKeyFilePath, img_path);
                setResult(RESULT_OK, intent);
                finish();

            }
        });
    }

    /**
     * 设置
     */
    private void setupCamera(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();

        if (parameters.getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        //这里第三个参数为最小尺寸 getPropPreviewSize方法会对从最小尺寸开始升序排列 取出所有支持尺寸的最小尺寸
        Camera.Size previewSize = ATCameraUtil.getInstance().getPropSizeForHeight(parameters.getSupportedPreviewSizes(), 800);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        Camera.Size pictrueSize = ATCameraUtil.getInstance().getPropSizeForHeight(parameters.getSupportedPictureSizes(), 800);
        parameters.setPictureSize(pictrueSize.width, pictrueSize.height);

        camera.setParameters(parameters);

        /**
         * 设置surfaceView的尺寸 因为camera默认是横屏，所以取得支持尺寸也都是横屏的尺寸
         * 我们在startPreview方法里面把它矫正了过来，但是这里我们设置设置surfaceView的尺寸的时候要注意 previewSize.height<previewSize.width
         * previewSize.width才是surfaceView的高度
         * 一般相机都是屏幕的宽度 这里设置为屏幕宽度 高度自适应 你也可以设置自己想要的大小
         *
         */

        picHeight = (screenWidth * pictrueSize.width) / pictrueSize.height;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(screenWidth, (screenWidth * pictrueSize.width) / pictrueSize.height);

//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(screenWidth, screenHeight);
        //这里当然可以设置拍照位置 比如居中 我这里就置顶了
        //params.gravity = Gravity.CENTER;
        cameraView.setLayoutParams(params);
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();
        startPreview(mCamera, holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();
        if (mCamera == null) {
            mCamera = getCamera(mCameraId);
            if (mHolder != null) {
                if (mCamera != null) {
                    startPreview(mCamera, mHolder);
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();

        }
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            checkPermission();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REUQEST_CODE_PERMISSION_CAMERA == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initCameraPreview();
            } else {
                Toast.makeText(context, "拍照权限被拒绝,请允许拍照", Toast.LENGTH_SHORT).show();
                this.finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
