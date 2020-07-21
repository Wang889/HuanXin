package com.example.lenovo.huanxin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView mTvUser;
    private RecyclerView mRlv;
    private String mCurrentUser;
    private RlvContactAdapter mAdapter;

    //1115200219168620
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCurrentUser = EMClient.getInstance().getCurrentUser();
        initView();
        getfriendList();
    }

    private void getfriendList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.addData(usernames);
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1, 0, 0, "退出登录");
        menu.add(1, 1, 0, "群聊");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                logout();
                break;
            case 1:
                go2Group();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void go2Group() {
        startActivity(new Intent(this,GroupChatActivity.class));
    }

    //退出登录
    private void logout() {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                showToast("退出登录成功");
                go2Login();
            }

            @Override
            public void onProgress(int progress, String status) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onError(int code, String message) {
                // TODO Auto-generated method stub
                showToast("退出失败");
            }
        });
    }

    private void go2Login() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initView() {
        mTvUser = (TextView) findViewById(R.id.tvUser);
        mTvUser.setText("当前用户"+mCurrentUser);
        mRlv = (RecyclerView) findViewById(R.id.rlv);

        ArrayList<String> strings = new ArrayList<>();
        mRlv.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new RlvContactAdapter(strings);
        mRlv.setAdapter(mAdapter);
        mRlv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        mAdapter.setOnItemClickListener(new RlvContactAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String friend) {
                go2Chat(friend);
            }
        });
    }

    private void go2Chat(String friend) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("friend",friend);
        startActivity(intent);
    }
}
