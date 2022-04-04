package com.example.citybikes.ui.favorites;

import android.graphics.Color;
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
    public void getData() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(citybikesURL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {
                if(response.isSuccessful()) {
                    String reply = Objects.requireNonNull(response.body()).string();
                    try {
                        mainObject = new JSONObject(reply);
                        network = mainObject.getJSONObject("network");
                        array = (JSONArray)network.get("stations");
                        filterStations();
                        if(locationAllowed) sortByDistance();
                        refreshContainer.setRefreshing(false);
                        populateList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public void filterStations(){
        List<Station> stations = db.stationsDao().getAllStations();
        JSONArray favoriteArray = new JSONArray();
        if (stations.size() == 0) {
            addEmptyFavoritesText();
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


    public void addEmptyFavoritesText() {
        TextView favorites = new TextView(getActivity());
        styleText(favorites, "Add favorites", 20, robotoBold, Color.BLACK);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stationsLinearLayout.addView(favorites);
            }
        });
    }

}