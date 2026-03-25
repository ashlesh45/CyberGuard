package com.example.cyberguard.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fraud_types")
data class FraudEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val preventionTips: String, // Stored as comma-separated or JSON string for simplicity, or use TypeConverters
    val commonTactics: String
)
