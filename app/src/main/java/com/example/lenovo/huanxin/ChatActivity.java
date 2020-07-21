package com.example.lenovo.huanxin;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mTvFriend;
    private RecyclerView mRlv;
    private EditText mEtContent;
    /**
     * 发送
     */
    private Button mBtnSend;
    /**
     * 开始录音
     */
    private Button mBtnRecord;
    /**
     * 发送语音
     */
    private Button mBtnSendVoice;
    /**
     * 语音聊天
     */
    private Button mBtnVoiceChat;
    /**
     * 视频聊天
     */
    private Button mBtnVideoChat;
    private String mFriend;
    private RlvChatAdapter mAdapter;
    private String mPath;
    private long mDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mFriend = getIntent().getStringExtra("friend");
        initView();

        //注册接受消息的监听
        initListener();
    }

    private void initListener() {
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(final List<EMMessage> messages) {
            //收到消息
            Log.d("TAG", "onMessageReceived: "+messages.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.addData(messages);
                }
            });
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {
            //收到已读回执
        }

        @Override
        public void onMessageDelivered(List<EMMessage> message) {
            //收到已送达回执
        }
        @Override
        public void onMessageRecalled(List<EMMessage> messages) {
            //消息被撤回
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };

    private void initView() {
        mTvFriend = (TextView) findViewById(R.id.tv_friend);
        mTvFriend.setText("当前正在和"+mFriend+"聊天");
        mRlv = (RecyclerView) findViewById(R.id.rlv);
        mEtContent = (EditText) findViewById(R.id.et_content);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnSend.setOnClickListener(this);
        mBtnRecord = (Button) findViewById(R.id.btn_record);
        mBtnRecord.setOnClickListener(this);
        mBtnSendVoice = (Button) findViewById(R.id.btn_send_voice);
        mBtnSendVoice.setOnClickListener(this);
        mBtnVoiceChat = (Button) findViewById(R.id.btn_voice_chat);
        mBtnVoiceChat.setOnClickListener(this);
        mBtnVideoChat = (Button) findViewById(R.id.btn_video_chat);
        mBtnVideoChat.setOnClickListener(this);

        ArrayList<EMMessage> list = new ArrayList<>();
        mAdapter = new RlvChatAdapter(list);
        mRlv.setLayoutManager(new LinearLayoutManager(this));
        mRlv.setAdapter(mAdapter);
        mRlv.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        mAdapter.setOnItemClickListener(new RlvChatAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String localUrl) {
                playAudio(localUrl);
            }
        });
    }

    private void playAudio(String localUrl) {
        if (!TextUtils.isEmpty(localUrl)){
            MediaPlayer mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(localUrl);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.btn_send:
                sentTextMsg();
                break;
            case R.id.btn_record:
                if (AudioUtil.isRecording){
                    //正在录音,停止
                    AudioUtil.stopRecord();
                    mBtnRecord.setText("开始录音");
                }else {
                    //没在录音,开始录音
                    mBtnRecord.setText("停止录音");
                    AudioUtil.startRecord(new AudioUtil.ResultCallBack() {
                        @Override
                        public void onFail(String msg) {
                            showToast("录音失败");
                        }

                        @Override
                        public void onSuccess(String absolutePath, long duration) {
                            //absolutePath,音频文件保存的位置
                            //duration ,音频时长
                            mPath = absolutePath;
                            mDuration = duration;
                        }
                    });
                }
                break;
            case R.id.btn_send_voice:
                sendVoiceMsg();
                break;
            case R.id.btn_voice_chat:
                break;
            case R.id.btn_video_chat:
                break;
        }
    }

    //发送语音消息
    private void sendVoiceMsg() {
        if (TextUtils.isEmpty(mPath)){
            showToast("请先录音");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                //filePath为语音文件路径，length为录音时间(秒)
                EMMessage message = EMMessage.createVoiceSendMessage(mPath, (int) mDuration, mFriend);
                //如果是群聊，设置chattype，默认是单聊
                /*if (chatType == CHATTYPE_GROUP)
                    message.setChatType(ChatType.GroupChat);*/
                EMClient.getInstance().chatManager().sendMessage(message);

                //语音路径置为""
                mPath = "";
                addSingleMsg(message);
            }
        }).start();
    }

    private void sentTextMsg() {
        final String content = mEtContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)){
            showToast("发送内容不能为空");
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者
                // 群聊的id，后文皆是如此
                final EMMessage message = EMMessage.createTxtSendMessage(content, mFriend);
//如果是群聊，设置chattype，默认是单聊
                /*if (chatType == CHATTYPE_GROUP)
                    message.setChatType(EMMessage.ChatType.GroupChat);*/
//发送消息
                EMClient.getInstance().chatManager().sendMessage(message);

                addSingleMsg(message);
            }
        }).start();

    }

    private void addSingleMsg(final EMMessage message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.addSingleData(message);
            }
        });
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChatActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
