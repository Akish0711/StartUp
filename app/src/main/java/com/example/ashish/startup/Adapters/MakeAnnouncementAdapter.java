package com.example.ashish.startup.Adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ashish.startup.R;

import java.util.List;

public class MakeAnnouncementAdapter extends RecyclerView.Adapter<MakeAnnouncementAdapter.ViewHolder>{

    public List<String> fileNameList;
    public List<String> fileDoneList;

    public MakeAnnouncementAdapter(List<String> fileNameList, List<String> fileDoneList){
        this.fileDoneList = fileDoneList;
        this.fileNameList = fileNameList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String fileName = fileNameList.get(position);
        holder.fileNameView.setText(fileName);
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;
        public TextView fileNameView;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            fileNameView = mView.findViewById(R.id.upload_filename);
        }
    }
}
