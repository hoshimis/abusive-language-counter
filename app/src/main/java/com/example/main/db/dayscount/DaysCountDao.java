package com.example.main.db.dayscount;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaysCountDao {
    @Query("select * from dayscount")
    List<DaysCount> getAll();

    @Query("select * from dayscount where id in (:ids)")
    List<DaysCount> loadAllByIds(int[] ids);

    @Insert
    void insertAll(DaysCount... daysCount);

    @Insert
    void insert(DaysCount daysCount);

    @Delete
    void delete(DaysCount daysCount);
}