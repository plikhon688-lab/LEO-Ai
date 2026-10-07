package com.example.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AbilityInfo(
    val name: String = "",
    val description: String = "",
    val limitation: String = ""
)

@JsonClass(generateAdapter = true)
data class UltimateAbilityInfo(
    val name: String = "",
    val description: String = "",
    val costOrCooldown: String = ""
)

@JsonClass(generateAdapter = true)
data class RelationshipInfo(
    val targetName: String = "",
    val roleType: String = "", // Ally, Rival, Mentor, Nemesis
    val context: String = ""
)

@JsonClass(generateAdapter = true)
data class CharacterStats(
    val strength: Int = 75,
    val speed: Int = 80,
    val defense: Int = 70,
    val intelligence: Int = 85,
    val magic: Int = 90,
    val combat: Int = 82,
    val agility: Int = 78,
    val endurance: Int = 76
) {
    val overallRating: Int
        get() = (strength + speed + defense + intelligence + magic + combat + agility + endurance) / 8

    val rankTier: String
        get() = when {
            overallRating >= 90 -> "EX-Rank (Transcendent)"
            overallRating >= 80 -> "S-Rank (Apex Vanguard)"
            overallRating >= 70 -> "A-Rank (Elite Master)"
            overallRating >= 60 -> "B-Rank (Veteran Operative)"
            else -> "C-Rank (Initiate)"
        }
}

@JsonClass(generateAdapter = true)
data class AnimeCharacter(
    val id: Long = System.currentTimeMillis(),
    val name: String = "",
    val shortDescription: String = "",
    val gender: String = "",
    val age: String = "",
    val characterRole: String = "",
    val alignment: String = "",
    val powerType: String = "",
    val powerLevel: String = "",
    val appearance: String = "",
    val hairColor: String = "",
    val eyeColor: String = "",
    val outfit: String = "",
    val height: String = "",
    val artStyle: String = "",
    val personality: String = "",
    val backstory: String = "",
    val fightingStyle: String = "",
    val weapon: String = "",
    val abilities: List<AbilityInfo> = emptyList(),
    val ultimateAbility: UltimateAbilityInfo = UltimateAbilityInfo(),
    val strengths: List<String> = emptyList(),
    val weaknesses: List<String> = emptyList(),
    val goals: String = "",
    val relationships: List<RelationshipInfo> = emptyList(),
    val catchphrase: String = "",
    val cinematicIntro: String = "",
    val imagePrompt: String = "",
    val stats: CharacterStats = CharacterStats(),
    val worldUniverse: String = "",
    val additionalDetails: String = "",
    val avatarType: String = "samurai", // samurai, mage, rogue
    val createdAt: Long = System.currentTimeMillis()
)

data class CharacterFormInput(
    val name: String = "",
    val gender: String = "Female",
    val age: String = "19",
    val personality: String = "Stoic, Sharp-witted, Fiercely Protective",
    val artStyle: String = "Cyberpunk Neo-Tokyo Shonen",
    val hairColor: String = "Silver with neon violet streaks",
    val eyeColor: String = "Electric Cyan",
    val outfit: String = "High-collared cyber trenchcoat with holographic trim & tactical greaves",
    val height: String = "172 cm (5'8\")",
    val specialAbilities: String = "Chrono-phase displacement, Aether blade projection",
    val weapon: String = "Twin Tachyon Kodachi blades",
    val powerType: String = "Psionic / Space-Time Manipulation",
    val characterRole: String = "Rogue Vanguard / Protagonist",
    val alignment: String = "Anti-Hero",
    val worldUniverse: String = "Neo-Akira 2189 / Astral Megacity",
    val additionalDetails: String = "Carries a mysterious digital pendant from a collapsed celestial dimension."
)

enum class RegenTarget(val displayName: String) {
    ENTIRE_CHARACTER("Entire Character"),
    BACKSTORY("Backstory & Lore"),
    ABILITIES("Abilities & Powers"),
    APPEARANCE("Appearance & Style"),
    PERSONALITY("Personality & Traits"),
    ULTIMATE_ABILITY("Ultimate Ability")
}
