package com.google.vision.Adapters;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.vision.activities.FullScreenImage;
import com.google.vision.Models.SingleMessage;
import com.google.vision.R;

import java.util.List;

public class ViewSingleAnnouncementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SingleMessage> messageList;
    private Context context;

    public ViewSingleAnnouncementAdapter(Context context, List<SingleMessage> messageList){
        this.messageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_message, parent, false);
                return new ViewHolder1(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf_message, parent, false);
                return new ViewHolder2(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return messageList.get(position).getType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 1:
                ViewHolder1 viewHolder1 = (ViewHolder1)holder;
                viewHolder1.fileNameView.setText(messageList.get(position).getName());
                Glide.with(context)
                        .load(messageList.get(position).getURL())
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder1.image_holder);

                viewHolder1.mView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, FullScreenImage.class);
                    intent.putExtra("image_url", messageList.get(position).getURL());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                });
                break;
            case 2:
                ViewHolder2 viewHolder2 = (ViewHolder2)holder;
                viewHolder2.fileNameView.setText(messageList.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {

        View mView;
        TextView fileNameView;
        ImageView image_holder;
        public ViewHolder1(View itemView) {
            super(itemView);

            mView = itemView;
            fileNameView = mView.findViewById(R.id.upload_filename);
            image_holder = mView.findViewById(R.id.image_holder);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {
        public TextView fileNameView;
        View mView;

        public ViewHolder2(View itemView){
            super(itemView);
            mView = itemView;
            fileNameView = mView.findViewById(R.id.upload_filename);
        }
    }
}
