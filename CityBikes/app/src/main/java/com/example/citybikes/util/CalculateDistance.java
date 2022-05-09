package com.example.citybikes.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CalculateDistance {

    //Inspired from https://www.geodatasource.com/developers/java

    public static double distance(double lat1, double lon1, double lat2, double lon2){
        if ((lat1 == lat2) && (lon1 == lon2)){
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1))
                    * Math.sin(Math.toRadians(lat2))
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2))
                    * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = toDegrees(dist);
            return dist * 60 * 1.1515 * 1.609344 * 1000; //Get the distance in meters

        }

    }

    private static double toDegrees(double rad) {
        return (rad*180.0/Math.PI);
    }

    public static String numberToString(double dist) {
        String result;
        Long roundedDist;
        if (dist > 1000) {
            dist = dist/1000;
            dist = Math.round(dist * 10)/10.0;
            if (dist % 1 == 0){
                roundedDist = Math.round(dist);
                result = roundedDist + " km";
            }
            else {
                result = dist + " km";
            }

        }
        else {
            roundedDist = Math.round(dist);
            result = roundedDist + " m";
        }
        return result;
    }

    public static JSONArray addFieldsToStations(JSONArray array, boolean locationAllowed, Double userLat, Double userLong) {
        double distance;
        Double stationLat;
        Double stationLong;
        JSONObject station;
        String rawName;
        String sortableName;
        // Adding distance and sortable name to all station objects
        for(int i = 0; i < array.length(); i++) {
            try {
                station = array.getJSONObject(i);
                if(locationAllowed) {
                    stationLat = Double.parseDouble(station.getString(Constants.getLATITUDE()));
                    stationLong = Double.parseDouble(station.getString(Constants.getLONGITUDE()));
                    distance = CalculateDistance.distance(userLat, userLong, stationLat, stationLong);
                    array.getJSONObject(i).put(Constants.getDISTANCE(), distance);
                }
                rawName = station.getString(Constants.getNAME());
                sortableName = rawName.split(" ", 3)[2];
                array.getJSONObject(i).put(Constants.getSortableName(), sortableName);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

}
