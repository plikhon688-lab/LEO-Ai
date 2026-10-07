package com.example.data.local

import com.example.data.model.AnimeCharacter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CharacterRepository(private val characterDao: CharacterDao) {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val adapter = moshi.adapter(AnimeCharacter::class.java)

    val savedCharacters: Flow<List<AnimeCharacter>> = characterDao.getAllCharacters().map { entities ->
        entities.mapNotNull { entity ->
            try {
                val char = adapter.fromJson(entity.rawJson)
                char?.copy(id = entity.id)
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun saveCharacter(character: AnimeCharacter): Long {
        val json = adapter.toJson(character)
        val entity = CharacterEntity(
            id = if (character.id > 1000000000000L) 0 else character.id, // reset auto-gen if needed
            name = character.name,
            characterRole = character.characterRole,
            alignment = character.alignment,
            powerType = character.powerType,
            powerLevel = character.powerLevel,
            avatarType = character.avatarType,
            shortDescription = character.shortDescription,
            rawJson = json,
            timestamp = System.currentTimeMillis()
        )
        return characterDao.insertCharacter(entity)
    }

    suspend fun deleteCharacter(id: Long) {
        characterDao.deleteCharacterById(id)
    }
}
