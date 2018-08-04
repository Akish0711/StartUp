package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ashish.startup.Models.Message;
import com.example.ashish.startup.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Message> messageList;
    public Context context;

    public MessageAdapter(List<Message> messageList){
        this.messageList = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_message, parent, false);
                return new ViewHolder1(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_message, parent, false);
                return new ViewHolder2(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getType();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()){
            case 1:
                ViewHolder1 viewHolder1 = (ViewHolder1)holder;
                viewHolder1.Text.setText(messageList.get(position).getMessage());
                break;

            case 2:
                ViewHolder2 viewHolder2 = (ViewHolder2)holder;
                Glide.with(viewHolder2.Image.getContext())
                        .load(messageList.get(position).getMessage())
                        .placeholder(R.drawable.ic_image_send_24dp)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder2.Image);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {

        View mView;
        public TextView Text;

        public ViewHolder1(View itemView) {
            super(itemView);
            mView = itemView;
            Text = mView.findViewById(R.id.textView_message_text);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {
        public ImageView Image;
        View mView;

        public ViewHolder2(View itemView){
            super(itemView);
            mView = itemView;
            Image = mView.findViewById(R.id.imageView_message_image);
        }
    }
}
