package com.example.citybikes.util;

public class Constants {

    private static final String CITYBIKES_URL = "http://api.citybik.es/v2/networks/bicimad";
    private static final String SORTABLE_NAME = "sortableName";
    private static final String DISTANCE = "distance";
    private static final String FREE_BIKES = "free_bikes";
    private static final String EMPTY_SLOTS = "empty_slots";
    private static final String NAME = "name";
    private static final String CAP_NAME = "Name";
    private static final String CAP_DISTANCE = "Distance";
    private static final String CAP_FREE_BIKES = "Free bikes";
    private static final String CAP_EMPTY_SLOTS = "Empty slots";
    private static final String UID = "uid";
    private static final String EXTRA = "extra";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String NETWORK = "network";
    private static final String STATIONS = "stations";

    public static String getCitybikesUrl() {
        return CITYBIKES_URL;
    }

    public static String getNAME() {
        return NAME;
    }

    public static String getSortableName() {
        return SORTABLE_NAME;
    }

    public static String getDISTANCE() {
        return DISTANCE;
    }

    public static String getFreeBikes() {
        return FREE_BIKES;
    }

    public static String getEmptySlots() {
        return EMPTY_SLOTS;
    }

    public static String getCapName() {
        return CAP_NAME;
    }

    public static String getCapDistance() {
        return CAP_DISTANCE;
    }

    public static String getCapFreeBikes() {
        return CAP_FREE_BIKES;
    }

    public static String getCapEmptySlots() {
        return CAP_EMPTY_SLOTS;
    }

    public static String getUID() {
        return UID;
    }

    public static String getEXTRA() {
        return EXTRA;
    }

    public static String getLONGITUDE() {
        return LONGITUDE;
    }

    public static String getLATITUDE() {
        return LATITUDE;
    }

    public static String getNETWORK() {
        return NETWORK;
    }

    public static String getSTATIONS() {
        return STATIONS;
    }
}
