package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drafts")
data class Draft(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val niche: String,
    val topic: String,
    val tone: String,
    val duration: String,
    val hook: String,
    val body: String,
    val callToAction: String,
    val caption: String = "",
    val hashtags: String = "",
    val thumbnailText: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
