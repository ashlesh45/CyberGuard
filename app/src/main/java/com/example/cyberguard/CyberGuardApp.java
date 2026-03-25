package com.example.cyberguard;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatDelegate;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class CyberGuardApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Apply saved theme on app start
        SharedPreferences prefs = getSharedPreferences("cyberguard_prefs", Context.MODE_PRIVATE);
        int savedTheme = prefs.getInt("app_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(savedTheme);
    }
}
