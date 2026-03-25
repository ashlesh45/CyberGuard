package com.example.cyberguard.domain.model

data class FraudType(
    val id: Int,
    val title: String,
    val description: String,
    val preventionTips: List<String>,
    val commonTactics: List<String>
)
