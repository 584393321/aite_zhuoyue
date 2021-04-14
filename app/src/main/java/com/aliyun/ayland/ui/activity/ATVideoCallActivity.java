package com.aliyun.ayland.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATEventClazz;
import com.aliyun.ayland.data.ATLoginBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.aliyun.iotx.linkvisual.media.audio.AudioParams;
import com.aliyun.iotx.linkvisual.media.audio.LiveIntercom;
import com.aliyun.iotx.linkvisual.media.audio.audiotrack.SimpleStreamAudioTrack;
import com.aliyun.iotx.linkvisual.media.audio.listener.OnAudioBufferReceiveListener;
import com.aliyun.iotx.linkvisual.media.audio.listener.OnTalkReadyListener;
import com.aliyun.iotx.linkvisual.media.audio.record.AudioRecordListener;
import com.aliyun.iotx.linkvisual.media.audio.record.SimpleAudioRecord;
import com.aliyun.iotx.linkvisual.media.video.PlayerException;
import com.aliyun.iotx.linkvisual.media.video.listener.OnErrorListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPlayerStateChangedListener;
import com.aliyun.iotx.linkvisual.media.video.listener.OnPreparedListener;
import com.aliyun.iotx.linkvisual.media.video.player.LivePlayer;
import com.anthouse.wyzhuoyue.R;
import com.google.android.exoplayer2.Player;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ATVideoCallActivity extends ATBaseActivity implements ATMainContract.View {
    private static final String TAG = "VideoCallActivity";
    private static final int MSG_CALLING = 1001;
    private static final int MSG_ACCEPT = 1002;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 0x1001;
    private static final int PERMISSION_RECORD_AUDIO = 0x1002;
    private ATMainPresenter mPresenter;
    private LivePlayer mPlayer;
    private SimpleAudioRecord mAudioRecord;
    private BlockingQueue<byte[]> audioTrackQueue = new LinkedBlockingQueue();
    private SimpleStreamAudioTrack simpleStreamAudioTrack;
    private String mIotId, recordId;
    private Dialog dialog;
    private TextView tvTime, tvOpen, tvAnswer, tvHangUp;
    private GLSurfaceView gLSurfaceView;
    private RelativeLayout rlContent;
    private int timeCount = 0, accept = 0, beat = 5000;
    private Date date = new Date();
    private LiveIntercom mLiveIntercom;
    @SuppressLint("SimpleDateFormat")
    private SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_CALLING:
                    // 正在拨打
                    timeCount++;
                    tvTime.setText(getTime(timeCount));
                    mHandler.sendEmptyMessageDelayed(MSG_CALLING, 1000);
                    break;
                case MSG_ACCEPT:
                    // 正在拨打
                    talkbackHeartBeat();
                    mHandler.sendEmptyMessageDelayed(MSG_ACCEPT, beat);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_video_call;
    }

    @Override
    protected void findView() {
        tvTime = findViewById(R.id.tv_time);
        rlContent = findViewById(R.id.rl_content);
        tvOpen = findViewById(R.id.tv_open);
        tvAnswer = findViewById(R.id.tv_answer);
        tvHangUp = findViewById(R.id.tv_hang_up);
        gLSurfaceView = findViewById(R.id.gLSurfaceView);
        init();
        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ATEventClazz eventClazz) {
        if ("VideoCallActivity".equals(eventClazz.getClazz())) {
            mAudioRecord.stop();
            mLiveIntercom.stop();
            if (simpleStreamAudioTrack != null) {
                simpleStreamAudioTrack.stop();
            }
            showToast(getString(R.string.at_The_other_is_hang_up));
            finish();
        }
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void talkbackHeartBeat() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", recordId);
        jsonObject.put("accept", accept);
        mPresenter.request(ATConstants.Config.SERVER_URL_TALKBACKHEARTBEAT, jsonObject);
    }

    private void init() {
        mIotId = getIntent().getStringExtra("iotId");
        recordId = getIntent().getStringExtra("recordId");
        Log.e("MainModel: ", recordId + "---");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_RECORD_AUDIO);
        }


        ATLoginBean loginBean = ATGlobalApplication.getATLoginBean();
        initDialog();
        mPlayer = new LivePlayer();
        mPlayer.setSurfaceView(gLSurfaceView);
        mPlayer.setOnPlayerStateChangedListener(new OnPlayerStateChangedListener() {
            @Override
            public void onPlayerStateChange(int playerState) {
                switch (playerState) {
                    case Player.STATE_BUFFERING:
                        //播放器正在缓冲, 当前的位置还不可以播放
                        break;
                    case Player.STATE_IDLE:
                        //播放器没有任何内容播放时的状态
                        break;
                    case Player.STATE_READY:
                        break;
                    case Player.STATE_ENDED:
                        break;
                    default:
                        break;
                }
            }
        });
        // 设置错误监听
        mPlayer.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(PlayerException exception) {
                Log.e("onError: ", exception.getCode() + exception.getMessage());
                showToast(exception.getMessage());
                finish();
            }
        });
        // 设置iotId.
        mPlayer.setIPCLiveDataSource(mIotId, 0, true, 0, true);
        // 设置rtmp地址.
//        mPlayer.setDataSource("rtmp://live.hkstv.hk.lxdns.com/live/hks2");
        // 设置数据源就绪监听器
        mPlayer.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                // 数据源就绪后开始播放
                mPlayer.start();
                new Thread(mAudioRecord::start).start();
                accept = 1;
                mHandler.sendEmptyMessage(MSG_ACCEPT);
            }
        });

        mLiveIntercom = new LiveIntercom();
        mLiveIntercom.setOnErrorListener(error -> {
            error.printStackTrace();
            mAudioRecord.stop();
            mLiveIntercom.stop();
            if (simpleStreamAudioTrack != null) {
                simpleStreamAudioTrack.stop();
            }
            Log.e("onError: ", error.getCode() + "----" + error.getMessage() + "----" + error.getLocalizedMessage());
            switch (error.getCode()) {
                case 2:
                    showToast(getString(R.string.at_The_other_is_on_the_phone_please));
                    break;
                default:
                    showToast(getString(R.string.at_The_other_is_hang_up));
                    break;
            }
            finish();
        });
        mLiveIntercom.setOnTalkReadyListener(new OnTalkReadyListener() {
            @Override
            public void onTalkReady() {

            }
        });
        mLiveIntercom.setOnAudioParamsChangeListener(audioParams -> {
            // 收到对端发送的音频参数，用于初始化AuudioTrack
            if (simpleStreamAudioTrack != null) {
                simpleStreamAudioTrack.release();
                audioTrackQueue.clear();
            }
            simpleStreamAudioTrack = new SimpleStreamAudioTrack(audioParams, AudioManager.STREAM_MUSIC,
                    audioTrackQueue);
            simpleStreamAudioTrack.start();
        });
        mLiveIntercom.setOnAudioBufferReceiveListener(new OnAudioBufferReceiveListener() {
            @Override
            public void onAudioBufferRecevie(byte[] bytes, int i) {
                //收到对端发过来的音频数据, 送入播放器播放
                audioTrackQueue.add(bytes);
            }
        });

        mAudioRecord = new SimpleAudioRecord(MediaRecorder.AudioSource.VOICE_COMMUNICATION, AudioParams.AUDIOPARAM_MONO_8K_PCM);
        mAudioRecord.setAudioRecordListener(new AudioRecordListener() {
            @Override
            public void onRecordStart() {
                Log.d(TAG, "onRecordStart");
                ThreadPool.MainThreadHandler.getInstance().post(() -> {
                    tvOpen.setVisibility(View.VISIBLE);
                    gLSurfaceView.setVisibility(View.VISIBLE);
                    rlContent.setVisibility(View.GONE);
                    tvAnswer.setVisibility(View.GONE);
                    mHandler.sendEmptyMessage(MSG_CALLING);
                });
                // 录音开始后，再启动云对讲，指定SDK将音频数据转成G711A格式后再发送
                mLiveIntercom.start(mIotId, AudioParams.AUDIOPARAM_MONO_8K_G711A);
            }

            @Override
            public void onRecordEnd() {
                Log.d(TAG, "onRecordEnd");
                // 录音结束时, 关闭云对讲
                mPlayer.stop();
                mPlayer.release();
                mLiveIntercom.stop();
            }

            @Override
            public void onBufferReceived(byte[] buffer, int offset, int size) {
                Log.d(TAG, "onBufferReceived:" + size);
                // 收到录音机PCM数据，调用云对讲接口发送给对端
                mLiveIntercom.sendAudioBuffer(buffer, offset, size);
            }

            @Override
            public void onError(int error, String message) {
                Log.e(TAG, "onError:" + error + message);
                // 处理录音机错误，如停止云对讲、重置UI元素等
            }
        });

        tvAnswer.setOnClickListener(view -> {
            mPlayer.prepare();
        });
        tvOpen.setOnClickListener(view -> dialog.show());
        tvHangUp.setOnClickListener(view -> {
            mHandler.removeMessages(MSG_ACCEPT);
            if (accept == 1)
                accept = 2;
            talkbackHeartBeat();
            finish();
        });
    }

    private void control() {
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONArray commands = new JSONArray();
        JSONObject command = new JSONObject();
        JSONObject data = new JSONObject();
        data.put("args", new JSONObject());
        data.put("method", "RemoteOpen");
        command.put("data", data);
        command.put("type", "INVOKE_SERVICE");
        commands.add(command);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("targetId", mIotId);
        jsonObject.put("operator", operator);
        jsonObject.put("commands", commands);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_CONTROL, jsonObject);
    }

    @SuppressLint("InflateParams")
    private void initDialog() {
        dialog = new Dialog(this, R.style.nameDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.at_dialog_clean, null, false);
        TextView tvNumber = view.findViewById(R.id.tv_title);
        TextView tvLeft = view.findViewById(R.id.tv_cancel);
        TextView tvRight = view.findViewById(R.id.tv_sure);
        tvNumber.setText(getString(R.string.at_sure_to_open_door));
        tvLeft.setText(getString(R.string.at_back));
        tvRight.setText(getString(R.string.at_sure));
        tvLeft.setOnClickListener(v -> dialog.dismiss());
        tvRight.setOnClickListener(v -> control());
        dialog.setContentView(view);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAudioRecord.stop();
        mHandler.removeMessages(MSG_CALLING);
        mHandler.removeMessages(MSG_ACCEPT);
        dialog.dismiss();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        gLSurfaceView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        gLSurfaceView.onPause();
    }

    @Override
    public void finish() {
        setResult(RESULT_OK);
        super.finish();
    }

    private String getTime(int timeCount) {
        long time = 1000 * timeCount;
        date.setTime(time);
        return sdf.format(date);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_TALKBACKHEARTBEAT:
                        if (accept == 1) {
                            recordId = jsonResult.getString("id");
                            mHandler.removeMessages(MSG_ACCEPT);
                            accept = 2;
                            beat = 10000;
                            mHandler.sendEmptyMessageDelayed(MSG_ACCEPT, beat);
                        }
                        break;
                    case ATConstants.Config.SERVER_URL_CONTROL:
                        if (result.contains("success")) {
                            showToast(getString(R.string.at_operate_success));
                            dialog.dismiss();
                        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_WRITE_EXTERNAL_STORAGE || requestCode == PERMISSION_RECORD_AUDIO) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showToast("请开启权限");
            }
        }
    }
}