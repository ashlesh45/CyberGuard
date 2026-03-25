package com.example.cyberguard.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cyberguard.data.local.entity.AdvisoryEntity;

import java.util.List;

@Dao
public interface AdvisoryDao {
    @Query("SELECT * FROM advisories ORDER BY timestamp DESC")
    LiveData<List<AdvisoryEntity>> getAllAdvisories();

    @Query("SELECT * FROM advisories ORDER BY timestamp DESC")
    List<AdvisoryEntity> getAllAdvisoriesSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<AdvisoryEntity> advisories);

    @Query("DELETE FROM advisories")
    void deleteAll();
}
