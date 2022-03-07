package com.example.citybikes.util;

public class CalculateDistance {

    //Inspired from https://www.geodatasource.com/developers/java

    public static String distance(double lat1, double lon1, double lat2, double lon2){
        String result;
        Long roundedDist;
        if ((lat1 == lat2) && (lon1 == lon2)){
            return "0";
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
            dist = dist * 60 * 1.1515 * 1.609344 * 1000; //Get the distance in meters
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

    }

    private static double toDegrees(double rad) {
        return (rad*180.0/Math.PI);
    }

}
