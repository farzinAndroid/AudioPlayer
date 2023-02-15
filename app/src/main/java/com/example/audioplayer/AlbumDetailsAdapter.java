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

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder> {

    private Context ctx;
    static ArrayList<MusicFiles> albumFiles;
    private int layout;


    public AlbumDetailsAdapter(Context ctx, ArrayList<MusicFiles> albumFiles, int layout) {
        this.ctx = ctx;
        this.albumFiles = albumFiles;
        this.layout = layout;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(ctx).inflate(this.layout,parent,false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, final int position) {

        int duration = Integer.parseInt(albumFiles.get(position).getDuration()) / 1000;
        holder.total_txt.setText(formattedTime(duration));
        holder.music_file_album.setText(albumFiles.get(position).getAlbum());
        holder.album_name.setText(albumFiles.get(position).getTitle());
        byte[] image = getAlbumImage(albumFiles.get(position).getPath());

        if (image != null){

            Glide.with(ctx).asBitmap()
                    .load(image)
                    .into(holder.album_image);
        }else {

            Glide.with(ctx).asBitmap()
                    .load(R.drawable.first)
                    .into(holder.album_image);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ctx,PlayerActivity.class);
                intent.putExtra("sender","AlbumDetailsActivity");
                intent.putExtra("position",position);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ctx.startActivity(intent);
            }
        });

        holder.menu_more.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView album_name , music_file_album , total_txt;
        ImageView album_image ,menu_more;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            music_file_album = itemView.findViewById(R.id.music_file_album);
            album_image = itemView.findViewById(R.id.music_img);
            album_name = itemView.findViewById(R.id.music_file_name);
            total_txt = itemView.findViewById(R.id.total_text);
            menu_more = itemView.findViewById(R.id.menu_more);
        }
    }

    private byte[] getAlbumImage (String uri){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
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

    private void delete(int position, View view) {

        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(albumFiles.get(position).getId()));
        File file = new File(albumFiles.get(position).getPath());
        boolean deleted = file.delete(); //delete file...

        if (deleted){

            ctx.getContentResolver().delete(contentUri,null,null);
            albumFiles.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,albumFiles.size());
            Snackbar.make(view,"File Deleted", BaseTransientBottomBar.LENGTH_SHORT).show();
        }else
            Snackbar.make(view,"File Can't be Deleted",BaseTransientBottomBar.LENGTH_SHORT).show();
    }
}
