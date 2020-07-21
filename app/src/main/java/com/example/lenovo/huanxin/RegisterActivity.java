package com.example.lenovo.huanxin;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 请输入用户名
     */
    private EditText mEtName;
    /**
     * 请输入密码
     */
    private EditText mEtPsd;
    /**
     * 注册
     */
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
    }

    private void initView() {
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPsd = (EditText) findViewById(R.id.et_psd);
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_register:
                register();
                break;
        }
    }

    private void register() {
        final String name=mEtName.getText().toString().trim();
        final String pad = mEtPsd.getText().toString().trim();
        if (TextUtils.isEmpty(name)||TextUtils.isEmpty(pad)){
            Toast("账号密码不能为空");
         return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(name, pad);//同步方法
                    Toast("注册成功");
                    RegisterActivity.this.finish();//注册成功关闭页面返回到登录页面
                } catch (HyphenateException e) {
                    Toast("注册失败");
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void Toast(final String mag){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivity.this, mag, Toast.LENGTH_SHORT).show();

            }
        });
    }
}
