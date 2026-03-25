package com.example.cyberguard.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.cyberguard.data.local.entity.PostEntity;

import java.util.List;

@Dao
public interface PostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PostEntity post);

    @Update
    void update(PostEntity post);

    @Delete
    void delete(PostEntity post);

    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    LiveData<List<PostEntity>> getAllPosts();
}
