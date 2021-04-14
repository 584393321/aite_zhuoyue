package com.aliyun.ayland.ui.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;

public class ATLinkageSendAppMessageActivity extends ATBaseActivity {
    private String message;
    private JSONObject params;
    private ATMyTitleBar titleBar;
    private EditText edittext;
    private TextView tvCount;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_linkage_send_app_message;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        edittext = findViewById(R.id.edittext);
        tvCount = findViewById(R.id.tv_count);
        init();
    }

    @Override
    protected void initPresenter() {

    }

    private void init() {
        titleBar.setRightButtonText(getString(R.string.at_done));
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                tvCount.setText(String.format(getString(R.string.at_100), edittext.getText().toString().length()));
            }
        });
        params = JSONObject.parseObject(getIntent().getStringExtra("params"));
        if (params != null) {
            message = params.getJSONObject("customData").getString("message");
            edittext.setText(message);
            edittext.setSelection(message.length());
        }

        titleBar.setRightClickListener(() -> {
            params = new JSONObject();
            String content = edittext.getText().toString();
            if (TextUtils.isEmpty(content)) {
                showToast(getString(R.string.at_input_send_app_message));
                return;
            }
            JSONObject customData = new JSONObject();
            customData.put("message", content);
            params.put("customData", customData);
            params.put("msgTag", "IlopBusiness_CustomMsg");
            setResult(RESULT_OK, getIntent()
                    .putExtra("content", content)
                    .putExtra("uri", "action/mq/send")
                    .putExtra("name", getString(R.string.at_send_app_message))
                    .putExtra("params", params.toJSONString()));
            finish();
        });
    }
}