package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.CharacterRepository
import com.example.data.model.AnimeCharacter
import com.example.data.model.CharacterFormInput
import com.example.data.model.RegenTarget
import com.example.data.remote.GeminiCharacterGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    CREATION,
    PROFILE,
    HISTORY
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = CharacterRepository(database.characterDao())
    private val generator = GeminiCharacterGenerator()

    private val _currentScreen = MutableStateFlow(AppScreen.CREATION)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _formInput = MutableStateFlow(CharacterFormInput())
    val formInput: StateFlow<CharacterFormInput> = _formInput.asStateFlow()

    private val _currentCharacter = MutableStateFlow<AnimeCharacter?>(null)
    val currentCharacter: StateFlow<AnimeCharacter?> = _currentCharacter.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val savedCharacters: StateFlow<List<AnimeCharacter>> = repository.savedCharacters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateFormInput(update: (CharacterFormInput) -> CharacterFormInput) {
        _formInput.value = update(_formInput.value)
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun clearSnackBar() {
        _snackBarMessage.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun showToast(message: String) {
        _snackBarMessage.value = message
    }

    fun randomizeForm() {
        val names = listOf(
            "Ren Kurogane", "Aoi Kisaragi", "Valen Solari", "Elysia Chronos",
            "Sora Tachibana", "Kaede Murasame", "Zane Cross", "Seraphina Vex",
            "Ryuto Hayabusa", "Noctis Drake", "Iris Ravenwood", "Dante Ignis"
        )
        val genders = listOf("Male", "Female", "Non-Binary", "Android / Synthetic", "Astral Being")
        val ages = listOf("17", "19", "22", "26", "120 (Immortal)", "500+", "Ageless")
        val personalities = listOf(
            "Stoic, highly analytical, secretly compassionate",
            "Chaotic rebel with a sharp wit and playful bravado",
            "Mysterious celestial scholar, quiet and observant",
            "Fierce protector, hot-headed yet honor-bound",
            "Cold calculating assassin pursuing cosmic redemption",
            "Cheerful prodigy concealing world-shattering grief"
        )
        val styles = listOf(
            "Cyberpunk Neo-Tokyo Shonen",
            "Dark Fantasy Eldritch Seinen",
            "Supernatural Astral Isekai",
            "90s Retro Mecha / Space Opera",
            "Urban Arcane Fantasy",
            "Celestial Cosmic Mythology"
        )
        val hairs = listOf(
            "Silver with neon violet tips",
            "Midnight black with cyan highlights",
            "Crimson red spiky ponytail",
            "Golden blonde twin braids with glowing runes",
            "Pure snow white flowing locks",
            "Electric azure buzzcut with cyber tattoo"
        )
        val eyes = listOf(
            "Electric Cyan",
            "Crimson Red with gear iris",
            "Amethyst Purple",
            "Golden Amber",
            "Heterochromia (Cyan / Crimson)",
            "Cosmic Starlight Violet"
        )
        val outfits = listOf(
            "High-collared cyber trenchcoat with holographic trim & tactical greaves",
            "Traditional shrine warrior haori with reinforced carbon-fiber plates",
            "Sleek stealth nano-flightsuit with energy conduits",
            "Battle-damaged royal mantle over ornate gilded armor",
            "Modern streetwear hoodie with glowing arcane sigils",
            "Ceremonial celestial archon robes with levitating pauldrons"
        )
        val heights = listOf(
            "168 cm (5'6\")", "175 cm (5'9\")", "182 cm (6'0\")", "190 cm (6'3\")", "162 cm (5'4\")"
        )
        val specialPowers = listOf(
            "Chrono-phase displacement, Aether blade projection",
            "Gravitational vortex generation, Singularity crush",
            "Nanite swarm transmutation, Cyber-hacking physical reality",
            "Celestial solar flare control, Light-speed dash",
            "Umbral shadow weaving, Dimensional portal tears",
            "Acoustic vibration resonance, Soundwave shockwaves"
        )
        val weapons = listOf(
            "Twin Tachyon Kodachi blades",
            "Heavy Anti-Materiel Rail-Scythe",
            "Floating Celestial Spell-Gems (6 Orbs)",
            "Dual Particle Ion Pistols with bayonets",
            "Ancestral Murasame Odachi infused with dark lightning",
            "Graviton Gauntlets with kinetic shock-bursts"
        )
        val powerTypes = listOf(
            "Psionic / Space-Time",
            "Cybernetic / Tech-Nanites",
            "Arcane Elemental (Fire/Lightning)",
            "Cosmic Void / Dark Matter",
            "Celestial Light / Divine Order",
            "Biological Transmutation / Beast Core"
        )
        val roles = listOf(
            "Protagonist / Lone Vanguard",
            "Fierce Rival / Ace Commander",
            "Exiled Demon Lord / Anti-Hero",
            "Enigmatic Mentor / Ancient Master",
            "Underground Syndicate Leader",
            "Sanctuary Guardian / Paladin"
        )
        val alignments = listOf(
            "Hero", "Anti-Hero", "Rebel Neutral", "Villain", "Lawful Arbiter"
        )
        val worlds = listOf(
            "Neo-Akira 2189 / Megacity Zero",
            "The Shattered Astral Isles",
            "Arcane Imperial Metropolis of Valderia",
            "Deep Orbit Dyson Sphere Station 'Chronos'",
            "Eldritch Ruins of the Lost Eclipse",
            "Shin-Kyoto Underworld"
        )
        val details = listOf(
            "Carries an encrypted memory drive belonging to their fallen twin.",
            "Their cybernetic arm secretly houses a forbidden divine engine.",
            "Can hear the latent frequencies of living machines.",
            "Sworn blood oath to never surrender their homeland's flame.",
            "Has a mysterious holographic companion AI nicknamed 'Pixel'."
        )

        _formInput.value = CharacterFormInput(
            name = names.random(),
            gender = genders.random(),
            age = ages.random(),
            personality = personalities.random(),
            artStyle = styles.random(),
            hairColor = hairs.random(),
            eyeColor = eyes.random(),
            outfit = outfits.random(),
            height = heights.random(),
            specialAbilities = specialPowers.random(),
            weapon = weapons.random(),
            powerType = powerTypes.random(),
            characterRole = roles.random(),
            alignment = alignments.random(),
            worldUniverse = worlds.random(),
            additionalDetails = details.random()
        )
        showToast("Randomized character parameters!")
    }

    fun generateCharacter() {
        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null
            try {
                val character = generator.generateCharacter(_formInput.value)
                _currentCharacter.value = character
                _currentScreen.value = AppScreen.PROFILE
                showToast("Character generated successfully!")
            } catch (e: Exception) {
                _errorMessage.value = "Failed to generate character: ${e.message}"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun generateRandomDirectly() {
        randomizeForm()
        generateCharacter()
    }

    fun regenerate(target: RegenTarget) {
        val current = _currentCharacter.value ?: return
        if (target == RegenTarget.ENTIRE_CHARACTER) {
            generateCharacter()
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            try {
                val updated = generator.regenerateSection(current, target)
                _currentCharacter.value = updated
                showToast("Regenerated ${target.displayName}!")
            } catch (e: Exception) {
                _errorMessage.value = "Regeneration failed: ${e.message}"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun saveCurrentCharacter() {
        val current = _currentCharacter.value ?: return
        viewModelScope.launch {
            try {
                val newId = repository.saveCharacter(current)
                _currentCharacter.value = current.copy(id = newId)
                showToast("Saved '${current.name}' to history!")
            } catch (e: Exception) {
                _errorMessage.value = "Failed to save: ${e.message}"
            }
        }
    }

    fun deleteCharacter(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteCharacter(id)
                showToast("Character removed from history.")
            } catch (e: Exception) {
                _errorMessage.value = "Could not delete character: ${e.message}"
            }
        }
    }

    fun selectSavedCharacter(character: AnimeCharacter) {
        _currentCharacter.value = character
        _currentScreen.value = AppScreen.PROFILE
    }

    fun updateAvatar(avatarType: String) {
        _currentCharacter.value = _currentCharacter.value?.copy(avatarType = avatarType)
    }
}
