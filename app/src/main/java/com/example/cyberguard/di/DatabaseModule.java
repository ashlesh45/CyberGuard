package com.example.cyberguard.di;

import android.content.Context;

import com.example.cyberguard.data.local.AppDatabase;
import com.example.cyberguard.data.local.dao.AdvisoryDao;
import com.example.cyberguard.data.local.dao.FraudDao;
import com.example.cyberguard.data.local.dao.PostDao;
import com.example.cyberguard.data.local.dao.UserDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return AppDatabase.getDatabase(context);
    }

    @Provides
    public FraudDao provideFraudDao(AppDatabase database) {
        return database.fraudDao();
    }

    @Provides
    public AdvisoryDao provideAdvisoryDao(AppDatabase database) {
        return database.advisoryDao();
    }

    @Provides
    public UserDao provideUserDao(AppDatabase database) {
        return database.userDao();
    }

    @Provides
    public PostDao providePostDao(AppDatabase database) {
        return database.postDao();
    }
}
