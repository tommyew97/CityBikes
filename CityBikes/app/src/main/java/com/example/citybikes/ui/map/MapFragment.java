package com.example.citybikes.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.citybikes.MainActivity;
import com.example.citybikes.R;
import com.example.citybikes.ui.list.ListFragment;
import com.example.citybikes.util.CalculateDistance;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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


/**
 * CLass that creates a fragment for the 'Map' section. It handles the
 * visualization and renders necessary elements
 */
public class MapFragment extends Fragment implements  OnMapReadyCallback  {

    private MapViewModel mViewModel;
    protected boolean locationAllowed;
    protected JSONArray array;
    protected static final String citybikesURL = "http://api.citybik.es/v2/networks/bicimad";
    protected JSONObject mainObject;
    protected JSONObject network;
    GoogleMap googleMap2;
    MarkerOptions [] markers;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.map_fragment, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

//        ------------------------------------

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //mViewModel = new ViewModelProvider(this).get(MapViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //enables the option to show current location on the Map
        googleMap.setMyLocationEnabled(true);

        //API call rquest, along with generating the markers on map
        getData(googleMap);

    }

    /**
     * Returns a MarkerOptions object with necessary information from a station
     *
     * @param lat
     * @param lon
     * @param stationName
     * @param emptySlots
     * @param freeBikes
     * @param distance
     * @return
     */
    private MarkerOptions addMarkerStation(double lat, double lon, String stationName, String emptySlots, String freeBikes, String distance){
        MarkerOptions station = new MarkerOptions()
                .position(new LatLng(lat, lon))
                .title(stationName)
                .snippet(emptySlots + " " + "| " + freeBikes + " " + distance);

        return station;

    }

    /**
     * Function that adds a marker to the Map
     * @param googleMap
     * @param stations Array with MarkerOptions Objects (stations ready to be added to the map)
     */
    public void addMark(GoogleMap googleMap, MarkerOptions[] stations){
        requireActivity().runOnUiThread(() -> {
            for(MarkerOptions station : stations){
                googleMap.addMarker(station);
            }
        });
    }


    /**
     * Generates an Array of MarkerOptions where each MarkerOption object contains information from
     * a station
     * @param googleMap
     * @param array JSONArray with information of stations
     * @return
     */
    private MarkerOptions[] addStationsMarkers(GoogleMap googleMap, JSONArray array){
        String name = "";
        String freeBikes = "";
        String emptySlots = "";
        double stationLat = 0, stationLong = 0;
        String stationDistance = "";
        String distance = "";
        markers = new MarkerOptions[array.length()];
        if (array != null) {
            for (int index = 0; index < array.length(); index++) {

                try {
                    name = array.getJSONObject(index).getString("name");
                    freeBikes = "Free bikes: " + array.getJSONObject(index).getString("free_bikes");
                    emptySlots = "Empty slots: " + array.getJSONObject(index).getString("empty_slots");
                    stationLat = Double.parseDouble(array.getJSONObject(index).getString("latitude"));           //Double.parseDouble(station.getString("latitude"));
                    stationLong = Double.parseDouble(array.getJSONObject(index).getString("longitude"));
                    stationDistance = CalculateDistance.numberToString(Double.parseDouble(array.getJSONObject(index).getString("distance")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (locationAllowed) {
                    distance = stationDistance;
                }
                markers[index] = addMarkerStation(stationLat, stationLong, name, emptySlots, freeBikes, distance);
            }

        }
        return markers;
    }

    /**
     * Function that makes the API call and adds the markers to the map
     * @param googleMap
     */
    public void getData(GoogleMap googleMap) {
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
                if (response.isSuccessful()) {
                    String reply = Objects.requireNonNull(response.body()).string();
                    try {
                        mainObject = new JSONObject(reply);
                        network = mainObject.getJSONObject("network");
                        array = (JSONArray) network.get("stations");

                        MarkerOptions[] stationMarkers = addStationsMarkers(googleMap2, array);
                        addMark(googleMap, stationMarkers);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

}