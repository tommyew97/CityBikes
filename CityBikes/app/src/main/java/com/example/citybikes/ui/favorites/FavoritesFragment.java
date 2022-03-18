package com.example.citybikes.ui.favorites;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.citybikes.R;

import java.util.ArrayList;
import java.util.List;

/**
 * CLass that creates a fragment for the 'Favorites' section. It handles the
 * visualization and renders necessary elements
 */

public class FavoritesFragment extends Fragment {

    private FavoritesViewModel mViewModel;

    private AppDatabase db;



    public static FavoritesFragment newInstance() {
        return new FavoritesFragment();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites_fragment, container, false);
        db = AppDatabase.getInstance(getActivity().getApplicationContext());
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
        // TODO: Use the ViewModel
    }

    //Function for saving data to database through a button
    //Inspired by https://github.com/bonigarcia/android-examples/blob/main/DatabaseRoomDemo/app/src/main/java/io/github/bonigarcia/android/database/EditActivity.java


    public void favoriteStation(Station station) {
        db.stationsDao().insert(station);
        List<Station> stations = db.stationsDao().getAllStations();
        for (Station element: stations) {
            Log.d("station", element.getName());
        }
    }

}