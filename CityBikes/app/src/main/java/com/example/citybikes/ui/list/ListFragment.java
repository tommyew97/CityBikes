package com.example.citybikes.ui.list;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.MenuItem;
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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.citybikes.util.SortStations;
import com.example.citybikes.util.CalculateDistance;
import com.example.citybikes.util.Constants;

/**
 * CLass that creates a fragment for the 'List' section. It handles the
 * visualization and renders necessary elements
 */
public class ListFragment extends Fragment {

    private ListViewModel mViewModel;
    protected LinearLayout stationsLinearLayout;
    protected ProgressBar progressBar;
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
    protected ConstraintLayout constraintLayout;
    protected RelativeLayout.LayoutParams params;
    protected boolean locationAllowed;
    protected SwipeRefreshLayout refreshContainer;
    protected String currentSortKey;
    protected ImageButton sortButton;
    protected ImageButton filterButton;
    private LinearLayout sortAndFilterLayout;
    private boolean emptySlotsChecked;
    private boolean freeBikesChecked;
    private int numberOfLoadedStations;
    private int totalNumberOfStations;
    private static final int STATION_BULK_LOAD_SIZE = 50;

    public static ListFragment newInstance() {
        return new ListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        emptySlotsChecked = false;
        freeBikesChecked = false;
        ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        detectScrolledToBottom(scrollView);
        stationsLinearLayout = (LinearLayout) view.findViewById(R.id.linearLayout);
        constraintLayout = (ConstraintLayout) view.findViewById(R.id.constraintLayout);
        refreshContainer = (SwipeRefreshLayout) view.findViewById(R.id.refreshContainer);
        sortAndFilterLayout = (LinearLayout) view.findViewById(R.id.sort_and_filter_layout);
        refreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshStationsList(currentSortKey);
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        db = AppDatabase.getInstance(getActivity().getApplicationContext());
        HashMap<String, String> sortKeys = new HashMap<>();
        sortKeys.put(Constants.getCapName(), Constants.getSortableName());
        sortKeys.put(Constants.getCapDistance(), Constants.getDISTANCE());
        sortKeys.put(Constants.getCapFreeBikes(), Constants.getFreeBikes());
        sortKeys.put(Constants.getCapEmptySlots(), Constants.getEmptySlots());
        sortButton = (ImageButton) view.findViewById(R.id.sortButton);
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateList();
                PopupMenu popupMenu = new PopupMenu(getActivity(), sortButton);
                int menuID;
                if(locationAllowed) menuID = R.menu.sorting_menu;
                else menuID = R.menu.sorting_menu_no_location;
                popupMenu.getMenuInflater().inflate(menuID, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        String menuTitle = String.valueOf(menuItem.getTitle());
                        String sortKey = sortKeys.get(menuTitle);
                        currentSortKey = sortKey;
                        if(emptySlotsChecked && !freeBikesChecked) {
                            refresh(sortKey, Constants.getEmptySlots(), null);
                        }
                        else if(!emptySlotsChecked && freeBikesChecked) {
                            refresh(sortKey, Constants.getFreeBikes(), null);
                        }
                        else if(emptySlotsChecked && freeBikesChecked) {
                            refresh(sortKey, Constants.getEmptySlots(), Constants.getFreeBikes());
                        }
                        else {
                            refreshStationsList(sortKey);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        filterButton = (ImageButton) view.findViewById(R.id.filterButton);
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), filterButton);
                popupMenu.getMenuInflater().inflate(R.menu.filter_menu, popupMenu.getMenu());
                if(emptySlotsChecked) {
                    popupMenu.getMenu().findItem(R.id.filter_empty_slots).setChecked(true);
                }
                if(freeBikesChecked) {
                    popupMenu.getMenu().findItem(R.id.filter_free_bikes).setChecked(true);
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        filterItems(menuItem);
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
        setUp();
        return view;
    }

    public void filterOnKey(String key) {
        JSONArray filterArray = new JSONArray();
        for (int i = 0; i < array.length(); i++) {
            try {
                if (!array.getJSONObject(i).getString(key).equals("0")) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    filterArray.put(jsonObject);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        array = filterArray;
        totalNumberOfStations = array.length();
        requireActivity().runOnUiThread(() -> {
            stationsLinearLayout.removeAllViews();
        });
    }

    public void filterItems(MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case R.id.filter_free_bikes:
                if(item.isChecked()) {
                    item.setChecked(false);
                    freeBikesChecked = false;
                    if(emptySlotsChecked) refresh(currentSortKey, Constants.getEmptySlots(), null);
                    else refresh(currentSortKey, null, null);

                }
                else {
                    item.setChecked(true);
                    freeBikesChecked = true;
                    filterOnKey("free_bikes");
                    populateList();
                }
                break;
            case R.id.filter_empty_slots:
                if(item.isChecked()) {
                    item.setChecked(false);
                    emptySlotsChecked = false;
                    if(freeBikesChecked) refresh(currentSortKey, Constants.getFreeBikes(), null);
                    else refresh(currentSortKey, null, null);
                }
                else {
                    item.setChecked(true);
                    emptySlotsChecked = true;
                    filterOnKey("empty_slots");
                    populateList();
                }
                break;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getData(currentSortKey);
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
        if(locationAllowed) currentSortKey = Constants.getDISTANCE();
        else currentSortKey = Constants.getSortableName();
        updateUserPosition();
        numberOfLoadedStations = 0;
    }

    // Reference: https://stackoverflow.com/questions/10316743/detect-end-of-scrollview
    // Used to enable infinite scroll, loads more stations when scrolled to the bottom
    public void detectScrolledToBottom(ScrollView scrollView) {
        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        if (!scrollView.canScrollVertically(1)) {
                            populateList();
                        }
                    }
                });
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

    public void refresh(String sortKey, String filterKey1, String filterKey2) {
        sortAndFilterLayout.setVisibility(View.INVISIBLE);
        numberOfLoadedStations = 0;
        updateUserPosition();
        stationsLinearLayout.removeAllViews();
        progressBar.setVisibility(View.VISIBLE);
        getData(sortKey, filterKey1, filterKey2);
    }

    public void refreshStationsList(String sortKey) {
        emptySlotsChecked = false;
        freeBikesChecked = false;
        refresh(sortKey, null, null);
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
        for(Station station: allStations) {
            if(id.equals(station.getStationId())) {
                isFavorited = true;
                break;
            }
        }
        if(isFavorited) {
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
        for(Station station: allStations) {
            if(id.equals(station.getStationId())) {
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
            styleText(name, array.getJSONObject(index).getString(Constants.getSortableName()), 18,
                    robotoBold, Color.BLACK);
            styleText(freeBikes, "Free bikes: " +
                    array.getJSONObject(index).getString(Constants.getFreeBikes()), 16,
                    robotoNormal, Color.BLACK);
            styleText(emptySlots, "Empty slots: " +
                    array.getJSONObject(index).getString(Constants.getEmptySlots()), 16,
                    robotoNormal, Color.BLACK);
            JSONObject extra = array.getJSONObject(index).getJSONObject(Constants.getEXTRA());
            configureFavoritesButton(favoritesButton,array.getJSONObject(index).
                    getString(Constants.getNAME()), extra.getString(Constants.getUID()));
            if(locationAllowed) {
                stationDistance = CalculateDistance.numberToString(Double.parseDouble(array.
                        getJSONObject(index).getString(Constants.getDISTANCE())));
            }
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

    /* Loop to create boxes and add them to the Linear Layout
    Also disables loading spinner when done
    Adds a bulk of station each time it is called, instead of all at once to improve loading
    loading speed */
    public void populateList() {
        int end;
        if(numberOfLoadedStations + STATION_BULK_LOAD_SIZE > totalNumberOfStations) end = totalNumberOfStations;
        else end = numberOfLoadedStations + STATION_BULK_LOAD_SIZE;
        requireActivity().runOnUiThread(() -> {
            for(int i = numberOfLoadedStations; i < end; i++) {
                stationsLinearLayout.addView(createBoxWithData(i));
                numberOfLoadedStations++;
            }
        });
    }


    public void sortStationsByField(String sortKey) {
        array = SortStations.sortStationsByField(array, sortKey);
    }

    public void filterFavorites() {
        return;
    }

    public void hideAndShowElements() {
        requireActivity().runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            sortAndFilterLayout.setVisibility(View.VISIBLE);
        });
    }

    public void getData(String sortKey) {
        getData(sortKey, null, null);
    }

    // Inspired by this source: https://www.youtube.com/watch?v=oGWJ8xD2W6k
    public void getData(String sortKey, String filterKey1, String filterKey2) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(Constants.getCitybikesUrl())
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
                        JSONObject mainObject = new JSONObject(reply);
                        JSONObject network = mainObject.getJSONObject(Constants.getNETWORK());
                        array = (JSONArray)network.get(Constants.getSTATIONS());
                        totalNumberOfStations = array.length();
                        filterFavorites();
                        array = CalculateDistance.addFieldsToStations(array, locationAllowed, userLat, userLong);
                        sortStationsByField(sortKey);
                        if(filterKey1 != null) filterOnKey(filterKey1);
                        if(filterKey2 != null) filterOnKey(filterKey2);
                        refreshContainer.setRefreshing(false);
                        populateList();
                        hideAndShowElements();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}