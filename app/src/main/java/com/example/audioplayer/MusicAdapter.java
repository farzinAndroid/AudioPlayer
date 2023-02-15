package com.example.audioplayer;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context ctx;
    static ArrayList<MusicFiles> mFiles;
    private int layout;

    public MusicAdapter(Context ctx, ArrayList<MusicFiles> mFiles, int layout) {
        this.ctx = ctx;
        this.mFiles = mFiles;
        this.layout = layout;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(ctx).inflate(this.layout,parent,false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        int durationtotal = Integer.parseInt(mFiles.get(position).getDuration()) / 1000;
        holder.total_text.setText(formattedTime(durationtotal));
        holder.music_name.setText(mFiles.get(position).getTitle());
        holder.musicAlbum.setText(mFiles.get(position).getAlbum());
        byte[] image = getAlbumArt(mFiles.get(position).getPath());

        if (image != null){

            Glide.with(ctx).asBitmap()
                    .load(image)
                    .into(holder.music_img);
        }else {

            Glide.with(ctx).asBitmap()
                    .load(R.drawable.first)
                    .into(holder.music_img);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx,PlayerActivity.class);
                intent.putExtra("position",position);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ctx.startActivity(intent);
            }
        });

        holder.more_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                PopupMenu popupMenu = new PopupMenu(ctx,view);
                popupMenu.getMenuInflater().inflate(R.menu.menu,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()){

                            case R.id.delete:
                                delete(position,view);
                        }

                        return false;
                    }
                });

                popupMenu.show();
            }
        });

    }


    private String formattedTime(int mCurrentPosition) {

        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;

        if (seconds.length() == 1){
            return totalNew;
        }else
            return totalOut;
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView music_name , total_text , musicAlbum;
        ImageView music_img , more_menu;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            musicAlbum = itemView.findViewById(R.id.music_file_album);
            total_text = itemView.findViewById(R.id.total_text);
            music_name = itemView.findViewById(R.id.music_file_name);
            music_img = itemView.findViewById(R.id.music_img);
            more_menu = itemView.findViewById(R.id.menu_more);
        }
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    private void delete(int position, View view) {

        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mFiles.get(position).getId()));
        File file = new File(mFiles.get(position).getPath());
        boolean deleted = file.delete(); //delete file...

        if (deleted){

            ctx.getContentResolver().delete(contentUri,null,null);
            mFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,mFiles.size());
            Snackbar.make(view,"File Deleted", BaseTransientBottomBar.LENGTH_SHORT).show();
        }else
            Snackbar.make(view,"File Can't be Deleted",BaseTransientBottomBar.LENGTH_SHORT).show();
    }

    public void updateList(ArrayList<MusicFiles> musicFilesArrayList){

        mFiles = new ArrayList<>();
        mFiles.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }


}
