package com.example.audioplayer;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.example.audioplayer.MainActivity.albums;
import static com.example.audioplayer.MainActivity.musicFiles;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlbumFragment extends Fragment {


    RecyclerView rv;
    AlbumAdapter albumAdapter;
    GridLayoutManager  gridLayoutManager;
    public AlbumFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_songs, container, false);

        rv = v.findViewById(R.id.rv);
        rv.setHasFixedSize(true);

        if (!(albums.size() < 1)){

            albumAdapter = new AlbumAdapter(getContext(),albums,R.layout.album_item);
            gridLayoutManager = new GridLayoutManager(getContext(),2,RecyclerView.VERTICAL,false);
            rv.setLayoutManager(gridLayoutManager);
            rv.setAdapter(albumAdapter);
        }

        return v;
    }

}
