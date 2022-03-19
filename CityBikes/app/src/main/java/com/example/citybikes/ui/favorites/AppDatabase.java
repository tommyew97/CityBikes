package com.example.citybikes.ui.favorites;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/*
The class that serves as the main access point with the database.
Inspired by https://github.com/bonigarcia/android-examples/blob/main/DatabaseRoomDemo/app/src/main/java/io/github/bonigarcia/android/database/AppDatabase.java
 */

@Database(entities = {Station.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract StationsDao stationsDao();
    private static final String DB_NAME = "Stations-database.db";
    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = create(context);
        }
        return instance;
    }

    private static AppDatabase create(Context context) {
        return Room.databaseBuilder(context,AppDatabase.class, DB_NAME)
                .allowMainThreadQueries().build();
    }
}
