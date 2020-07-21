package com.example.lenovo.huanxin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 请输入用户名
     */
    private EditText mEtName;
    /**
     * 请输入密码
     */
    private EditText mEtPsd;
    /**
     * 登录
     */
    private Button mBtnLogin;
    /**
     * 注册
     */
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (EMClient.getInstance().isLoggedInBefore()){
            go2MainAictivity();
        }
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPsd = (EditText) findViewById(R.id.et_psd);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(this);
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_register:
                register();
                break;
        }
    }

    private void register() {
        startActivity(new Intent(this,RegisterActivity.class));
    }

    private void login() {
        final String name=mEtName.getText().toString().trim();
        final String pad = mEtPsd.getText().toString().trim();
        if (TextUtils.isEmpty(name)||TextUtils.isEmpty(pad)){
            Toast("账号密码不能为空");
            return;
        }

        EMClient.getInstance().login(name,pad,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                Log.d("main", "登录聊天服务器成功！");
                go2MainAictivity();
            }



            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                Log.d("main", "登录聊天服务器失败！");
            }
        });
    }
    private void go2MainAictivity() {
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }

    public void Toast(final String mag){
//切换主线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, mag, Toast.LENGTH_SHORT).show();

            }
        });
    }
}
