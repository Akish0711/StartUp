package com.example.ashish.startup.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ashish.startup.Activities.FullScreenImage;
import com.example.ashish.startup.Models.Message;
import com.example.ashish.startup.R;

import java.io.File;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Message> messageList;
    private Context context;
    public MessageAdapter(Context context, List<Message> messageList){
        this.messageList = messageList;
        this.context = context;
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
            case 3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf_message, parent, false);
                return new ViewHolder3(view);
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
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder2.Image);

                viewHolder2.Image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent= new Intent(context,FullScreenImage.class);
                        intent.putExtra("image_url", messageList.get(position).getMessage());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });
                break;
            case 3:
                ViewHolder3 viewHolder3 = (ViewHolder3)holder;
                viewHolder3.file_name.setText(messageList.get(position).getName());
                viewHolder3.file_name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Uri webpage = Uri.parse(messageList.get(position).getMessage());
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        Intent newIntent = Intent.createChooser(intent, "Open File");
                        try {
                            newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(newIntent);
                        } catch (ActivityNotFoundException e) {
                            // Instruct the user to install a PDF reader here, or something
                        }
                    }
                });
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

    public class ViewHolder3 extends RecyclerView.ViewHolder {
        public TextView file_name;
        View mView;

        public ViewHolder3(View itemView){
            super(itemView);
            mView = itemView;
            file_name = mView.findViewById(R.id.pdf_file_name);
        }
    }
}