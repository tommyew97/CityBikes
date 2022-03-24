package com.example.citybikes.ui.list;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.citybikes.MainActivity;
import com.example.citybikes.R;
import com.example.citybikes.ui.favorites.AppDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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
    private static final String citybikesURL = "http://api.citybik.es/v2/networks/bicimad";
    private LinearLayout stationsLinearLayout;
    private ProgressBar progressBar;
    private JSONObject mainObject;
    private JSONObject network;
    private JSONArray array;
    private RelativeLayout.LayoutParams boxParams;
    private RelativeLayout.LayoutParams lp;
    private RelativeLayout.LayoutParams lp2;
    private RelativeLayout.LayoutParams lp3;
    private Typeface robotoBold;
    private Typeface robotoNormal;
    private AppDatabase db;
    private RelativeLayout.LayoutParams lp4;
    private Double userLat;
    private Double userLong;
    private Double stationLat;
    private Double stationLong;
    private boolean locationAllowed;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        stationsLinearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
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
        boxParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                225);
        robotoBold = Typeface.create("sans-serif", Typeface.BOLD);
        robotoNormal = Typeface.create("sans-serif", Typeface.NORMAL);
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
        locationAllowed = ((MainActivity) getActivity()).getLocationAllowed();
        if(locationAllowed) {
            userLong = ((MainActivity) getActivity()).getUserLong();
            userLat = ((MainActivity) getActivity()).getUserLat();
        }
    }

    public void styleText(TextView view, String text, int fontSize, Typeface font, int color) {
        view.setId(View.generateViewId());
        view.setText(text);
        view.setTextSize(fontSize);
        view.setTypeface(font);
        view.setTextColor(color);
    }



    public void configureFavoritesButton(ImageButton btn) {
        btn.setImageResource(R.drawable.ic_baseline_star_border_24);
        btn.setBackgroundColor(getResources().getColor(R.color.transparent));
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn.setImageResource(R.drawable.ic_baseline_star_24);
            }
        });
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
        try {
            styleText(name, array.getJSONObject(index).getString("name"), 18,
                    robotoBold, Color.BLACK);
            styleText(freeBikes, "Free bikes: " +
                    array.getJSONObject(index).getString("free_bikes"), 16,
                    robotoNormal, Color.BLACK);
            styleText(emptySlots, "Empty slots: " +
                    array.getJSONObject(index).getString("empty_slots"), 16,
                    robotoNormal, Color.BLACK);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        configureFavoritesButton(favoritesButton);
        emptySlots.setLayoutParams(lp);
        freeBikes.setLayoutParams(lp2);
        favoritesButton.setLayoutParams(lp3);
        box.addView(name);
        box.addView(freeBikes);
        box.addView(emptySlots);
        box.addView(favoritesButton);
        if(locationAllowed) {
            styleText(distance, CalculateDistance.distance(userLat, userLong, stationLat, stationLong), 16, robotoNormal, Color.BLACK);
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
                        populateList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}