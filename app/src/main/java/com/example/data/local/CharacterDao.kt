package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM saved_characters ORDER BY timestamp DESC")
    fun getAllCharacters(): Flow<List<CharacterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: CharacterEntity): Long

    @Query("DELETE FROM saved_characters WHERE id = :id")
    suspend fun deleteCharacterById(id: Long)

    @Query("SELECT * FROM saved_characters WHERE id = :id LIMIT 1")
    suspend fun getCharacterById(id: Long): CharacterEntity?
}
