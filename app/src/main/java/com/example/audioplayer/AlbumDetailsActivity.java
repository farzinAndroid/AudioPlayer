package com.example.audioplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import static com.example.audioplayer.MainActivity.musicFiles;

public class AlbumDetailsActivity extends AppCompatActivity {

    ImageView albumPhoto, back_btn;
    RecyclerView rv;
    ArrayList<MusicFiles> albumSongs;
    String albumName;
    AlbumDetailsAdapter albumDetailsAdapter;
    LinearLayoutManager linearLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        initViews();

        albumName = getIntent().getStringExtra("albumName");
        int j = 0;

        for (int i = 0; i < musicFiles.size(); i++){

            if (albumName.equals(musicFiles.get(i).getAlbum())){

                albumSongs.add(j,musicFiles.get(i));
                j ++;
            }
        }

        byte[] image = getAlbumArt(albumSongs.get(0).getPath());

        if (image != null){

            Glide.with(this).asBitmap()
                    .load(image)
                    .into(albumPhoto);
        }else {

            Glide.with(this).asBitmap()
                    .load(R.drawable.first)
                    .into(albumPhoto);
        }

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();


        if (!(albumSongs.size() < 1)){

            albumDetailsAdapter = new AlbumDetailsAdapter(this,albumSongs,R.layout.music_item);
            rv.setAdapter(albumDetailsAdapter);
            linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
            rv.setLayoutManager(linearLayoutManager);
        }
    }

    private byte[] getAlbumArt (String uri){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }


    private void initViews(){

        albumPhoto = findViewById(R.id.album_photo);
        rv = findViewById(R.id.rv);
        albumSongs = new ArrayList<>();
        back_btn = findViewById(R.id.back_btn);

    }
}