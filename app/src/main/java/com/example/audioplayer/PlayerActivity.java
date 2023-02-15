package com.example.audioplayer;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

import static com.example.audioplayer.AlbumDetailsAdapter.albumFiles;
import static com.example.audioplayer.MainActivity.musicFiles;
import static com.example.audioplayer.MainActivity.repeatBoolean;
import static com.example.audioplayer.MainActivity.shuffleBoolean;
import static com.example.audioplayer.MusicAdapter.mFiles;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, ActionPlaying , ServiceConnection {

    TextView song_name , artist_name , duration_played , duration_total;
    ImageView cover_art , next_btn , prev_btn , back_btn , shuffle_btn , repeat_btn;
    FloatingActionButton play_pause_btn;
    SeekBar seekBar;
    int position = -1;
    static ArrayList<MusicFiles> listofSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    Handler handler = new Handler();
    private Thread playThread, prevThread , nextThread;
    MusicService musicService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        initViews();
        getIntentMethod();
        song_name.setText(listofSongs.get(position).getTitle());
        artist_name.setText(listofSongs.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (mediaPlayer != null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,500);
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        shuffle_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (shuffleBoolean){

                    shuffleBoolean = false;
                    shuffle_btn.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                    Toast.makeText(PlayerActivity.this, "Shuffle Off", Toast.LENGTH_SHORT).show();
                }else {

                    shuffleBoolean = true;
                    shuffle_btn.setImageTintList(ColorStateList.valueOf(Color.rgb(136,8,28)));
                    Toast.makeText(PlayerActivity.this, "Shuffle On", Toast.LENGTH_SHORT).show();

                }
            }
        });

        repeat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (repeatBoolean){

                    repeatBoolean = false;
                    repeat_btn.setImageTintList(ColorStateList.valueOf(Color.WHITE));
                    Toast.makeText(PlayerActivity.this, "Repeat Off", Toast.LENGTH_SHORT).show();
                }else {

                    repeatBoolean = true;
                    repeat_btn.setImageTintList(ColorStateList.valueOf(Color.rgb(136,8,28)));
                    Toast.makeText(PlayerActivity.this, "Repeat On", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        Intent intent = new Intent(this,MusicService.class);
        bindService(intent,this,BIND_AUTO_CREATE);
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        unbindService(this);
    }

    //for Prev Button////////////###########
    private void prevThreadBtn() {

        prevThread = new Thread(){

            @Override
            public void run() {
                super.run();

                prev_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prev_btnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    public void prev_btnClicked() {

        if (mediaPlayer.isPlaying()){

            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean){

                position = getRandomNumber(listofSongs.size() - 1);
            }else if (!shuffleBoolean && !repeatBoolean){

                position = ((position - 1) < 0 ? (listofSongs.size() - 1) : (position - 1));
            }

            uri = Uri.parse(listofSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listofSongs.get(position).getTitle());
            artist_name.setText(listofSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){

                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,500);
                }
            });

            mediaPlayer.setOnCompletionListener(this);
            play_pause_btn.setBackgroundResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();
        }else {
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean){

                position = getRandomNumber(listofSongs.size() - 1);
            }else if (!shuffleBoolean && !repeatBoolean){

                position = ((position - 1) < 0 ? (listofSongs.size() - 1) : (position - 1));
            }


            uri = Uri.parse(listofSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listofSongs.get(position).getTitle());
            artist_name.setText(listofSongs.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){

                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,500);
                }
            });

            mediaPlayer.setOnCompletionListener(this);
            play_pause_btn.setBackgroundResource(R.drawable.ic_baseline_play);
        }
    }
    //for Prev Button////////////###########


    //for Next Button////////////###########
    private void nextThreadBtn() {

        nextThread = new Thread(){

            @Override
            public void run() {
                super.run();

                next_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        next_btnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    public void next_btnClicked() {

        if (mediaPlayer.isPlaying()){

            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean){

                position = getRandomNumber(listofSongs.size() - 1);
            }else if (!shuffleBoolean && !repeatBoolean){

                position = ((position + 1) % listofSongs.size());
            }

            //else...
            uri = Uri.parse(listofSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listofSongs.get(position).getTitle());
            artist_name.setText(listofSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,500);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play_pause_btn.setBackgroundResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();

        }else {

            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBoolean && !repeatBoolean){

                position = getRandomNumber(listofSongs.size() - 1);
            }else if (!shuffleBoolean && !repeatBoolean){

                position = ((position + 1) % listofSongs.size());
            }


            uri = Uri.parse(listofSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            metaData(uri);
            song_name.setText(listofSongs.get(position).getTitle());
            artist_name.setText(listofSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,500);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            play_pause_btn.setBackgroundResource(R.drawable.ic_baseline_play);
        }
    }
    //for Next Button////////////###########


    //for Play Button////////////###########
    private void playThreadBtn() {

        playThread = new Thread(){
            @Override
            public void run() {
                super.run();
                play_pause_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       play_pause_btnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    public void play_pause_btnClicked() {

        if (mediaPlayer.isPlaying()){

            play_pause_btn.setImageResource(R.drawable.ic_baseline_play);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,500);
                }
            });
        }else {

            play_pause_btn.setImageResource(R.drawable.ic_baseline_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this,500);
                }
            });
        }
    }
    //for Play Button////////////###########




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

    private void getIntentMethod() {

        String sender = getIntent().getStringExtra("sender");
        position = getIntent().getIntExtra("position",-1);

        if (sender != null && sender.contains("AlbumDetailsActivity")){

            listofSongs = albumFiles;
        }else {

            listofSongs = mFiles;

        }

        if (listofSongs != null){
            play_pause_btn.setImageResource(R.drawable.ic_baseline_pause);
            uri = Uri.parse(listofSongs.get(position).getPath());
        }

        if (mediaPlayer != null){

            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        metaData(uri);
    }

    private void initViews() {

        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.durationplayed);
        duration_total = findViewById(R.id.durationtotal);
        cover_art = findViewById(R.id.cover_art);
        next_btn = findViewById(R.id.next);
        prev_btn = findViewById(R.id.prev);
        back_btn = findViewById(R.id.back_btn);
        shuffle_btn = findViewById(R.id.shuffle);
        repeat_btn = findViewById(R.id.repeat);
        play_pause_btn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekbar);
    }

    private void metaData(Uri uri){

        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationtotal = Integer.parseInt(listofSongs.get(position).getDuration()) / 1000;
        duration_total.setText(formattedTime(durationtotal));
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap;
        if (art != null){

            /*Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(art)
                    .into(cover_art);*/

            bitmap = BitmapFactory.decodeByteArray(art,0,art.length);
            imageAnimation(PlayerActivity.this, cover_art,bitmap);
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    Palette.Swatch swatch = palette.getDominantSwatch();

                    if (swatch != null){

                        ImageView gradient = findViewById(R.id.imageviewgradient);
                        RelativeLayout mContainer = findViewById(R.id.mcontainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[] {swatch.getRgb(), 0x00000000});
                        gradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawable1Bg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[] {swatch.getRgb(), swatch.getRgb()});
                        mContainer.setBackground(gradientDrawable1Bg);

                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());
                    }else {

                        ImageView gradient = findViewById(R.id.imageviewgradient);
                        RelativeLayout mContainer = findViewById(R.id.mcontainer);
                        gradient.setBackgroundResource(R.drawable.gradient_bg);
                        mContainer.setBackgroundResource(R.drawable.main_bg);

                        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[] {0xff000000, 0x00000000});
                        gradient.setBackground(gradientDrawable);

                        GradientDrawable gradientDrawable1Bg = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[] {0xff000000, 0xff000000});
                        mContainer.setBackground(gradientDrawable1Bg);

                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.GRAY);
                    }
                }
            });
        }else {

            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load(R.drawable.first)
                    .into(cover_art);

            ImageView gradient = findViewById(R.id.imageviewgradient);
            RelativeLayout mContainer = findViewById(R.id.mcontainer);
            gradient.setBackgroundResource(R.drawable.gradient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);

            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.GRAY);
        }
    }

    public void imageAnimation(final Context context , final ImageView imageView , final Bitmap bitmap){

        Animation animOut = AnimationUtils.loadAnimation(PlayerActivity.this,android.R.anim.fade_out);
        final Animation animIn = AnimationUtils.loadAnimation(PlayerActivity.this, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                imageView.startAnimation(animIn);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        imageView.startAnimation(animOut);
    }

    private int getRandomNumber(int i){

        Random random = new Random();
        return random.nextInt(i + 1);
    }


    @Override
    public void onCompletion(MediaPlayer mp) {

        next_btnClicked();

        if (mediaPlayer != null){

            mediaPlayer = MediaPlayer.create(PlayerActivity.this,uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

        MusicService.MyBinder myBinder = (MusicService.MyBinder) service;
        musicService = myBinder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

        musicService = null;
    }
}