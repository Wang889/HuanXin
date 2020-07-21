package com.example.lenovo.huanxin;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RlvContactAdapter extends RecyclerView.Adapter {
    private ArrayList<String> mList;
    private OnItemClickListener mListener;

    public RlvContactAdapter(ArrayList<String> list) {

        mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_contact, null);
        return new VH(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        VH vh = (VH) viewHolder;
        vh.mTvFriend.setText(mList.get(i));
        vh.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onItemClick(v,i,mList.get(i));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addData(List<String> usernames) {
        if (usernames != null && usernames.size()>0){
            mList.addAll(usernames);
            notifyDataSetChanged();
        }
    }

    class VH extends RecyclerView.ViewHolder{

        private final TextView mTvFriend;

        public VH(@NonNull View itemView) {
            super(itemView);
            mTvFriend = itemView.findViewById(R.id.tv_friend);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position, String friend);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
}
