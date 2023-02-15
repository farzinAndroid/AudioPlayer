package com.example.audioplayer;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.example.audioplayer.MainActivity.musicFiles;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment {

    RecyclerView rv;
    static MusicAdapter musicAdapter;
    LinearLayoutManager linearLayoutManager;

    public SongsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_songs, container, false);

        rv = v.findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        if (!(musicFiles.size() < 1)){

            musicAdapter = new MusicAdapter(getContext(),musicFiles,R.layout.music_item);
            linearLayoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false);
            rv.setLayoutManager(linearLayoutManager);
            rv.setAdapter(musicAdapter);
        }

        return v;
    }

}
