package com.example.citybikes.ui.favorites;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/*
Class that uses the Room library to represent the Station table in the database.
Inspired by https://github.com/bonigarcia/android-examples/blob/main/DatabaseRoomDemo/app/src/main/java/io/github/bonigarcia/android/database/Notes.java
 */

@Entity
public class Station {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "stationId")
    public String stationId;

    @ColumnInfo(name = "name")
    public String name;

    /*public Station(String name, String stationId) {
        this.name = name;
        this.stationId = stationId;
    }*/

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }
}
