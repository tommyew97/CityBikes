package com.example.citybikes.ui.favorites;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.citybikes.R;

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

    public FavoritesFragment() {

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

}