package com.example.audioplayer;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyHolder> {

    private Context ctx;
    private ArrayList<MusicFiles> albumFiles;
    private int layout;

    public AlbumAdapter(Context ctx, ArrayList<MusicFiles> albumFiles, int layout) {
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

        holder.album_name.setText(albumFiles.get(position).getAlbum());
        holder.album_artist.setText(albumFiles.get(position).getArtist());
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

                Intent intent = new Intent(ctx,AlbumDetailsActivity.class);
                intent.putExtra("albumName",albumFiles.get(position).getAlbum());
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ctx.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        TextView album_name , album_artist;
        ImageView album_image;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            album_artist = itemView.findViewById(R.id.album_artist);
            album_image = itemView.findViewById(R.id.album_image);
            album_name = itemView.findViewById(R.id.album_name);
        }
    }

    private byte[] getAlbumImage (String uri){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }
}
