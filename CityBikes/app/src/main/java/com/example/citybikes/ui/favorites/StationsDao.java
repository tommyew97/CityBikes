package com.example.citybikes.ui.favorites;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

/*
Class that uses DAO (Data Access Objects) that provides methods that the app can use
to query, update, insert and delete data in the database.
Inspired by https://github.com/bonigarcia/android-examples/blob/main/DatabaseRoomDemo/app/src/main/java/io/github/bonigarcia/android/database/NotesDao.java
 */

@Dao
public interface StationsDao {


    @Query("SELECT * FROM station")
    List<Station> getAllStations();

    @Query("SELECT * FROM station WHERE id IN (:stationsIds)")
    List<Station> loadAllByIds(long[] stationsIds);

    @Query("SELECT * FROM station WHERE name LIKE :name")
    Station findByName(String name);

    @Query("SELECT * FROM station WHERE id=:id")
    Station findById(long id);

    @Insert
    long insert(Station station);

    @Insert
    long[] insertAll(Station... stations);

    @Update
    public void update(Station... stations);

    @Delete
    void delete(Station station);

}
