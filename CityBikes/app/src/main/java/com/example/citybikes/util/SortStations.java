package com.example.citybikes.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortStations {

    // Reference: https://discourse.processing.org/t/sorting-a-jsonarray-by-one-of-its-values/4911/5
    public static JSONArray sortStationsByField(JSONArray jsonArr, String sortBy) {
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
        // Sort list
            Collections.sort(jsonValues, new Comparator<JSONObject>() {
                @Override
                public int compare(JSONObject a, JSONObject b) {
                    if(sortBy.equals("distance")) {
                        double valA = 0;
                        double valB = 0;

                        try {
                            valA = Double.parseDouble(a.getString(sortBy));
                            valB = Double.parseDouble(b.getString(sortBy));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        double difference = valA - valB;
                        return (int) difference;
                    }
                    else if(sortBy.equals("sortableName")) {
                        String valA = "";
                        String valB = "";

                        try {
                            valA = a.getString(sortBy);
                            valB = b.getString(sortBy);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return valA.compareToIgnoreCase(valB);
                    }
                    else if(sortBy.equals("free_bikes") || sortBy.equals("empty_slots")) {
                        int valA = 0;
                        int valB = 0;

                        try {
                            valA = Integer.parseInt(a.getString(sortBy));
                            valB = Integer.parseInt(b.getString(sortBy));
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return valB - valA;
                    }
                    else return 0;
                }
            });

        // Convert back to JSONArray
        for(int i = 0; i < jsonValues.size(); i++) {
            sortedJsonArray.put(jsonValues.get(i));
        }
        return sortedJsonArray;
    }
}
