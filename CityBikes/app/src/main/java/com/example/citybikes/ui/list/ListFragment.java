package com.example.citybikes.ui.list;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.citybikes.MainActivity;
import com.example.citybikes.R;
import com.example.citybikes.ui.favorites.AppDatabase;
import com.example.citybikes.ui.favorites.Station;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.citybikes.util.CalculateDistance;

/**
 * CLass that creates a fragment for the 'List' section. It handles the
 * visualization and renders necessary elements
 */
public class ListFragment extends Fragment {

    private ListViewModel mViewModel;
    protected static final String citybikesURL = "http://api.citybik.es/v2/networks/bicimad";
    protected LinearLayout stationsLinearLayout;
    private ProgressBar progressBar;
    protected JSONObject mainObject;
    protected JSONObject network;
    protected JSONArray array;
    private RelativeLayout.LayoutParams boxParams;
    private RelativeLayout.LayoutParams lp;
    private RelativeLayout.LayoutParams lp2;
    private RelativeLayout.LayoutParams lp3;
    protected Typeface robotoBold;
    private Typeface robotoNormal;
    protected AppDatabase db;
    private RelativeLayout.LayoutParams lp4;
    private Double userLat;
    private Double userLong;
<<<<<<< HEAD
    protected ConstraintLayout constraintLayout;
    protected RelativeLayout.LayoutParams params;
=======
>>>>>>> 2c83fe93b12dc29e92380f61180eadb84b5783b0
    protected boolean locationAllowed;
    protected SwipeRefreshLayout refreshContainer;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        stationsLinearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
<<<<<<< HEAD
        constraintLayout = (ConstraintLayout) view.findViewById(R.id.constraintLayout);
=======
>>>>>>> 2c83fe93b12dc29e92380f61180eadb84b5783b0
        refreshContainer = (SwipeRefreshLayout) view.findViewById(R.id.refreshContainer);
        refreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStationsList();
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        db = AppDatabase.getInstance(getActivity().getApplicationContext());
        setUp();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getData();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ListViewModel.class);
        // TODO: Use the ViewModel
    }

    // One time setup of UI elements such as layout parameters and fonts
    public void setUp() {
        robotoBold = Typeface.create("sans-serif", Typeface.BOLD);
        robotoNormal = Typeface.create("sans-serif", Typeface.NORMAL);
        layoutParameterSetUp();
        locationAllowed = ((MainActivity) getActivity()).getLocationAllowed();
        updateUserPosition();
    }

    public void updateUserPosition() {
        if(locationAllowed) {
            userLong = ((MainActivity) getActivity()).getUserLong();
            userLat = ((MainActivity) getActivity()).getUserLat();
        }
    }

    public void layoutParameterSetUp() {
        boxParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                225);
        lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp3 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp3.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp4 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp4.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp4.addRule(RelativeLayout.CENTER_HORIZONTAL);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
    }

    public void refreshStationsList() {
        updateUserPosition();
        stationsLinearLayout.removeAllViews();
        getData();
    }

    public void styleText(TextView view, String text, int fontSize, Typeface font, int color) {
        view.setId(View.generateViewId());
        view.setText(text);
        view.setTextSize(fontSize);
        view.setTypeface(font);
        view.setTextColor(color);
    }

    public void configureFavoritesButton(ImageButton btn, String name, String id) {
        List<Station> allStations = db.stationsDao().getAllStations();
        btn.setBackgroundColor(getResources().getColor(R.color.transparent));
        boolean isFavorited = false;
        for (Station station: allStations) {
            if (id.equals(station.getStationId())) {
                isFavorited = true;
            }
        }
        if (isFavorited) {
            btn.setImageResource(R.drawable.star_filled);
        }
        else {
            btn.setImageResource(R.drawable.star_unfilled);
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStation(btn, name, id);
            }
        });
    }

    public void checkStation(ImageButton btn, String name, String id) {
        List<Station> allStations = db.stationsDao().getAllStations();
        boolean isFavorited = false;
        for (Station station: allStations) {
            if (id.equals(station.getStationId())) {
                isFavorited = true;
                db.stationsDao().delete(station);
                btn.setImageResource(R.drawable.star_unfilled);
            }
        }
        if(!isFavorited) {
            Station station = new Station(name, id);
            db.stationsDao().insert(station);
            btn.setImageResource(R.drawable.star_filled);
        }
    }

    // Creates one box view with API data
    public RelativeLayout createBoxWithData(int index) {
        RelativeLayout box = new RelativeLayout(getActivity());
        styleBox(box);
        TextView name = new TextView(getActivity());
        TextView freeBikes = new TextView(getActivity());
        TextView emptySlots = new TextView(getActivity());
        ImageButton favoritesButton = new ImageButton(getActivity());
        TextView distance = new TextView(getActivity());
        String stationDistance = "";
        try {
            styleText(name, array.getJSONObject(index).getString("name"), 18,
                    robotoBold, Color.BLACK);
            styleText(freeBikes, "Free bikes: " +
                    array.getJSONObject(index).getString("free_bikes"), 16,
                    robotoNormal, Color.BLACK);
            styleText(emptySlots, "Empty slots: " +
                    array.getJSONObject(index).getString("empty_slots"), 16,
                    robotoNormal, Color.BLACK);
            JSONObject extra = array.getJSONObject(index).getJSONObject("extra");
            configureFavoritesButton(favoritesButton,array.getJSONObject(index).getString("name"),
                    extra.getString("uid"));
            stationDistance = CalculateDistance.numberToString(Double.parseDouble(array.getJSONObject(index).getString("distance")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        emptySlots.setLayoutParams(lp);
        freeBikes.setLayoutParams(lp2);
        favoritesButton.setLayoutParams(lp3);
        box.addView(name);
        box.addView(freeBikes);
        box.addView(emptySlots);
        box.addView(favoritesButton);
        if(locationAllowed) {
            styleText(distance, stationDistance, 16, robotoNormal, Color.BLACK);
            distance.setLayoutParams(lp4);
            box.addView(distance);
        }
        return box;
    }

    public void styleBox(RelativeLayout box) {
        box.setLayoutParams(boxParams);
        box.setBackgroundResource(R.drawable.bottomborder);
        box.setPadding(20, 15, 30, 20);
    }

    // Loop to create all the boxes and add them to the Linear Layout
    // Also disables loading spinner when done
    public void populateList() {
        requireActivity().runOnUiThread(() -> {
            for(int i=0; i < array.length(); i++) {
                stationsLinearLayout.addView(createBoxWithData(i));
            }
            progressBar.setVisibility(View.GONE);
        });
    }

    public void sortByDistance() {
        double distance;
        Double stationLat;
        Double stationLong;
        JSONObject station;
        // Adding distance to all station objects
        for(int i = 0; i < array.length(); i++) {
            try {
                station = array.getJSONObject(i);
                stationLat = Double.parseDouble(station.getString("latitude"));
                stationLong = Double.parseDouble(station.getString("longitude"));
                distance = CalculateDistance.distance(userLat, userLong, stationLat, stationLong);
                array.getJSONObject(i).put("distance", distance);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        array = sortJSONByField(array, "distance");
    }

    // Reference: https://discourse.processing.org/t/sorting-a-jsonarray-by-one-of-its-values/4911/5
    public JSONArray sortJSONByField(JSONArray jsonArr, String sortBy) {
        JSONArray sortedJsonArray = new JSONArray();
        List<JSONObject> jsonValues = new ArrayList<JSONObject>();
        // Convert JSONArray to List
        for (int i = 0; i < jsonArr.length(); i++) {
            try {
                jsonValues.add(jsonArr.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final String KEY_NAME = sortBy;
        // Sort list
        Collections.sort( jsonValues, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject a, JSONObject b) {
                double valA = 0;
                double valB = 0;

                try {
                    valA = Double.parseDouble(a.getString(KEY_NAME));
                    valB = Double.parseDouble(b.getString(KEY_NAME));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                double difference = valA - valB;
                return (int) difference;
            }
        });
        // Convert back to JSONArray
        for(int i = 0; i < jsonValues.size(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }
        return sortedJsonArray;
    }

    // Inspired by this source: https://www.youtube.com/watch?v=oGWJ8xD2W6k
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
}