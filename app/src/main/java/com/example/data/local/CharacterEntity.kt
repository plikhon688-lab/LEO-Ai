package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_characters")
data class CharacterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val characterRole: String,
    val alignment: String,
    val powerType: String,
    val powerLevel: String,
    val avatarType: String,
    val shortDescription: String,
    val rawJson: String,
    val timestamp: Long = System.currentTimeMillis()
)
