package com.example.citybikes.ui.favorites;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.citybikes.ui.list.ListFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * CLass that creates a fragment for the 'Favorites' section. It handles the
 * visualization and renders necessary elements
 */

public class FavoritesFragment extends ListFragment {


    @Override
    public void populateList() {
        requireActivity().runOnUiThread(() -> {
            for(int i=0; i < array.length(); i++) {
                stationsLinearLayout.addView(createBoxWithData(i));
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    @Override
    public void detectScrolledToBottom(ScrollView scrollView) {
        return;
    }


    @Override
    public void filterFavorites(){
        List<Station> stations = db.stationsDao().getAllStations();
        JSONArray favoriteArray = new JSONArray();
        if (stations.size() == 0) {
            styleEmptyFavoritesPage();
        }
        else {
            for (int i = 0; i < array.length(); i++) {
                for (Station station : stations) {
                    try {
                        if (station.getStationId().equals(array.getJSONObject(i).getJSONObject("extra").getString("uid"))){
                            favoriteArray.put(array.getJSONObject(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        array = favoriteArray;
    }


    public void styleEmptyFavoritesPage() {
        TextView favorites = new TextView(getActivity());
        styleText(favorites, "No favorites to show", 40, robotoBold, Color.parseColor("#CC383838"));
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
        favorites.setLayoutParams(params);
        favorites.setGravity(Gravity.CENTER);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                constraintLayout.addView(favorites);
            }
        });
    }

}