package com.example.lenovo.huanxin;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;

import java.util.ArrayList;
import java.util.List;

public class RlvChatAdapter extends RecyclerView.Adapter {
    private ArrayList<EMMessage> mList;
    private OnItemClickListener mListener;

    public RlvChatAdapter(ArrayList<EMMessage> list) {

        mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, null);
        return new VH(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        VH vh = (VH) holder;
        EMMessage emMessage = mList.get(position);
        //消息来自于谁
        String from = emMessage.getFrom();
        //消息发送的时间
        long msgTime = emMessage.getMsgTime();
        //消息题
        final EMMessageBody body = emMessage.getBody();
        vh.mTv.setText("消息来自于:"+from+",发送时间:"+msgTime+",消息内容:"+body.toString());


        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener != null){
                    if (body instanceof EMVoiceMessageBody){
                        //语音消息
                        EMVoiceMessageBody voiceBody = (EMVoiceMessageBody) body;
                        //音频本地路径
                        String localUrl = voiceBody.getLocalUrl();
                        mListener.onItemClick(v,position,localUrl);
                    }
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addData(List<EMMessage> messages) {
        mList.addAll(messages);
        notifyDataSetChanged();
    }

    public void addSingleData(EMMessage message) {
        mList.add(message);
        notifyDataSetChanged();
    }

    class VH extends RecyclerView.ViewHolder{

        private final TextView mTv;

        public VH(View itemView) {
            super(itemView);
            mTv = itemView.findViewById(R.id.tv_friend);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position, String audioUrl);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

}
