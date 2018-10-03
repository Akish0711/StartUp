package com.example.ashish.startup.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.ashish.startup.Activities.FullScreenImage;
import com.example.ashish.startup.R;

import java.util.List;

public class MakeAnnouncementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<String> fileNameList;
    private List<String> listpos;
    private List<Uri> listUri;
    private List<Uri> listUriPdf;
    private List<String> listStringUri;
    private List<String> listStringUriPdf;
    Context context;

    public MakeAnnouncementAdapter(List<String> fileNameList, List<Uri> listUri, List<Uri> listUriPdf, List<String> listpos,List<String> listStringUri,List<String> listStringUriPdf, Context context){
        this.fileNameList = fileNameList;
        this.listUri = listUri;
        this.context = context;
        this.listpos = listpos;
        this.listUriPdf = listUriPdf;
        this.listStringUri = listStringUri;
        this.listStringUriPdf = listStringUriPdf;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_message_edit, parent, false);
                return new ViewHolder1(view);
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pdf_message_edit, parent, false);
                return new ViewHolder2(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {

        if (listpos.get(position).equals("image")){
            return 1;
        }
        else if(listpos.get(position).equals("pdf")){
            return 2;
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        switch (holder.getItemViewType()) {
            case 1:
                ViewHolder1 viewHolder1 = (ViewHolder1)holder;
                viewHolder1.fileNameView.setText(fileNameList.get(position));
                Glide.with(context)
                        .load(listUri.get(position))
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder1.image_holder);

                viewHolder1.clear_button.setOnClickListener(v -> {
                    if (listStringUri.size()<=position+1 && listStringUri.size()!=0) {
                        listStringUri.remove(position);
                        listStringUriPdf.remove(position);
                        fileNameList.remove(position);
                        listUri.remove(position);
                        listpos.remove(position);
                        listUriPdf.remove(position);
                        notifyDataSetChanged();
                    }else {
                        fileNameList.remove(position);
                        listUri.remove(position);
                        listpos.remove(position);
                        listUriPdf.remove(position);
                        notifyDataSetChanged();
                    }
                });

                viewHolder1.mView.setOnClickListener(v -> {
                    Intent intent = new Intent(context, FullScreenImage.class);
                    intent.putExtra("image_url", listUri.get(position).toString());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                });
                break;

            case 2:
                ViewHolder2 viewHolder2 = (ViewHolder2)holder;
                viewHolder2.fileNameView.setText(fileNameList.get(position));

                viewHolder2.clear_button.setOnClickListener(v -> {
                    if (listStringUriPdf.size()<=position+1 && listStringUriPdf.size()!=0) {
                        listStringUri.remove(position);
                        listStringUriPdf.remove(position);
                        fileNameList.remove(position);
                        listUri.remove(position);
                        listpos.remove(position);
                        listUriPdf.remove(position);
                        notifyDataSetChanged();
                    }else{
                        fileNameList.remove(position);
                        listUri.remove(position);
                        listpos.remove(position);
                        listUriPdf.remove(position);
                        notifyDataSetChanged();
                    }
                });
        }
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder1 extends RecyclerView.ViewHolder {
        View mView;
        TextView fileNameView;
        ImageView image_holder;
        ImageButton clear_button;

        ViewHolder1(View itemView) {
            super(itemView);
            mView = itemView;
            fileNameView = mView.findViewById(R.id.upload_filename);
            image_holder = mView.findViewById(R.id.image_holder);
            clear_button = mView.findViewById(R.id.clear_button);
        }
    }

    public class ViewHolder2 extends RecyclerView.ViewHolder {
        TextView fileNameView;
        ImageButton clear_button;
        View mView;

        ViewHolder2(View itemView){
            super(itemView);
            mView = itemView;
            fileNameView = mView.findViewById(R.id.upload_filename);
            clear_button = mView.findViewById(R.id.clear_button);
        }
    }
}
