package com.example.cyberguard.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.cyberguard.data.local.entity.FraudEntity;

import java.util.List;

@Dao
public interface FraudDao {
    @Query("SELECT * FROM fraud_types")
    LiveData<List<FraudEntity>> getAllFraudTypes();

    @Query("SELECT * FROM fraud_types")
    List<FraudEntity> getAllFraudTypesSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FraudEntity> fraudTypes);

    @Query("DELETE FROM fraud_types")
    void deleteAll();
}
