package com.example.data.remote

import com.example.BuildConfig
import com.example.data.model.AbilityInfo
import com.example.data.model.AnimeCharacter
import com.example.data.model.CharacterFormInput
import com.example.data.model.CharacterStats
import com.example.data.model.RegenTarget
import com.example.data.model.RelationshipInfo
import com.example.data.model.UltimateAbilityInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import kotlin.random.Random

class GeminiCharacterGenerator(
    private val apiService: GeminiApiService = GeminiApiService.create()
) {

    suspend fun generateCharacter(input: CharacterFormInput): AnimeCharacter = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Provide high-quality procedural generation if API key is not yet set
            return@withContext generateProceduralCharacter(input)
        }

        val prompt = buildFullPrompt(input)
        val systemPrompt = """
            You are LEO AI, an elite anime worldbuilder and character creator.
            Produce an entirely ORIGINAL, highly detailed fictional anime character.
            STRICT SAFETY & COPYRIGHT MANDATE:
            Do NOT copy any copyrighted characters (e.g. Dragon Ball, Naruto, Jujutsu Kaisen, Bleach, One Piece, Attack on Titan).
            Make all abilities, backstories, and names distinct, innovative, and internally consistent with their power type.
            Always return valid JSON ONLY with no surrounding markdown or code blocks.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt)))
            ),
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = systemPrompt))
            ),
            generationConfig = GeminiGenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.85f
            )
        )

        try {
            val response = apiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty response from Gemini")
            parseCharacterFromJson(jsonText, input)
        } catch (e: Exception) {
            // If network or quota error, fallback gracefully while preserving input choices
            generateProceduralCharacter(input, errorNotice = e.message)
        }
    }

    suspend fun regenerateSection(
        character: AnimeCharacter,
        target: RegenTarget
    ): AnimeCharacter = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext regenerateSectionProcedural(character, target)
        }

        val prompt = """
            You are revising an existing anime character named "${character.name}".
            Existing Context:
            - Role: ${character.characterRole}
            - Power Type: ${character.powerType}
            - Current Personality: ${character.personality}
            - Current Backstory: ${character.backstory}
            
            Task: Regenerate ONLY the "${target.displayName}" section.
            Make it thrilling, innovative, and cohesive with their power level.
            Return a JSON object containing the updated fields for this section:
            ${
                when (target) {
                    RegenTarget.BACKSTORY -> """{ "backstory": "new detailed origin story...", "goals": "updated primary goal..." }"""
                    RegenTarget.ABILITIES -> """{ "abilities": [ { "name": "...", "description": "...", "limitation": "..." } ], "fightingStyle": "..." }"""
                    RegenTarget.APPEARANCE -> """{ "appearance": "new visual description...", "imagePrompt": "new anime image generation prompt..." }"""
                    RegenTarget.PERSONALITY -> """{ "personality": "new personality...", "catchphrase": "new catchphrase..." }"""
                    RegenTarget.ULTIMATE_ABILITY -> """{ "ultimateAbility": { "name": "...", "description": "...", "costOrCooldown": "..." } }"""
                    RegenTarget.ENTIRE_CHARACTER -> "{}"
                }
            }
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt)))
            ),
            generationConfig = GeminiGenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.9f
            )
        )

        try {
            val response = apiService.generateContent(apiKey, request)
            val jsonText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: return@withContext regenerateSectionProcedural(character, target)
            val json = JSONObject(cleanJson(jsonText))

            when (target) {
                RegenTarget.BACKSTORY -> character.copy(
                    backstory = json.optString("backstory", character.backstory),
                    goals = json.optString("goals", character.goals)
                )
                RegenTarget.ABILITIES -> {
                    val abilitiesArray = json.optJSONArray("abilities")
                    val updatedAbilities = if (abilitiesArray != null) parseAbilities(abilitiesArray) else character.abilities
                    character.copy(
                        abilities = updatedAbilities,
                        fightingStyle = json.optString("fightingStyle", character.fightingStyle)
                    )
                }
                RegenTarget.APPEARANCE -> character.copy(
                    appearance = json.optString("appearance", character.appearance),
                    imagePrompt = json.optString("imagePrompt", character.imagePrompt)
                )
                RegenTarget.PERSONALITY -> character.copy(
                    personality = json.optString("personality", character.personality),
                    catchphrase = json.optString("catchphrase", character.catchphrase)
                )
                RegenTarget.ULTIMATE_ABILITY -> {
                    val ultJson = json.optJSONObject("ultimateAbility")
                    val updatedUlt = if (ultJson != null) {
                        UltimateAbilityInfo(
                            name = ultJson.optString("name", character.ultimateAbility.name),
                            description = ultJson.optString("description", character.ultimateAbility.description),
                            costOrCooldown = ultJson.optString("costOrCooldown", character.ultimateAbility.costOrCooldown)
                        )
                    } else character.ultimateAbility
                    character.copy(ultimateAbility = updatedUlt)
                }
                RegenTarget.ENTIRE_CHARACTER -> character
            }
        } catch (e: Exception) {
            regenerateSectionProcedural(character, target)
        }
    }

    private fun buildFullPrompt(input: CharacterFormInput): String {
        return """
            Create an original anime character based on these user specifications:
            - Name: ${input.name.ifBlank { "Generate a legendary original anime name" }}
            - Gender: ${input.gender}
            - Age: ${input.age}
            - Personality: ${input.personality}
            - Art Style: ${input.artStyle}
            - Hair Color: ${input.hairColor}
            - Eye Color: ${input.eyeColor}
            - Outfit: ${input.outfit}
            - Height: ${input.height}
            - Special Abilities: ${input.specialAbilities}
            - Weapon: ${input.weapon}
            - Power Type: ${input.powerType}
            - Character Role: ${input.characterRole}
            - Alignment: ${input.alignment}
            - World / Universe: ${input.worldUniverse}
            - Additional Details: ${input.additionalDetails}

            Generate the following JSON structure:
            {
              "name": "Original Name",
              "shortDescription": "One-line captivating description",
              "appearance": "In-depth visual breakdown (face, eyes, hair, clothes, physique, accessory)",
              "personality": "Nuanced behavioral traits, moral core, flaws, battle temperament",
              "backstory": "Compelling multi-paragraph origin story and defining trauma or catalyst",
              "abilities": [
                {
                  "name": "Ability Name",
                  "description": "How the power manifests and affects reality",
                  "limitation": "Explicit mechanical weakness, drain, or constraint"
                }
              ],
              "ultimateAbility": {
                "name": "Ultimate Name",
                "description": "Devastating signature technique and visual spectacle",
                "costOrCooldown": "Stamina drain, spiritual toll, or recharge condition"
              },
              "strengths": ["Strategic adaptability", "Hyper-accelerated reflexes"],
              "weaknesses": ["Vulnerable to anti-matter disruption", "Reluctance to sacrifice allies"],
              "fightingStyle": "Tactical breakdown of their hand-to-hand and power synergy",
              "weapon": "Weapon name, material, and unique mechanic",
              "goals": "Core motivation and life ambition",
              "relationships": [
                {
                  "targetName": "Character Name",
                  "roleType": "Rival / Ally / Mentor / Nemesis",
                  "context": "Dynamic history between them"
                }
              ],
              "catchphrase": "Signature iconic line spoken in battle or dialogue",
              "powerLevel": "e.g. S-Rank (Apex Vanguard) • 9,480 / 10,000",
              "cinematicIntro": "Cinematic visual narrative scene introducing the character in a movie-quality opening shot",
              "imagePrompt": "Masterpiece anime illustration prompt describing subject, dynamic pose, outfit details, intricate weapons, luminous energy aura, dramatic cinematic angle, volumetric god-rays, 8k resolution anime key art",
              "stats": {
                "strength": 82,
                "speed": 88,
                "defense": 74,
                "intelligence": 90,
                "magic": 95,
                "combat": 86,
                "agility": 89,
                "endurance": 78
              }
            }
        """.trimIndent()
    }

    private fun parseCharacterFromJson(
        rawText: String,
        input: CharacterFormInput
    ): AnimeCharacter {
        val clean = cleanJson(rawText)
        val json = JSONObject(clean)

        val name = json.optString("name", input.name.ifBlank { "Kaelen Vane" })
        val shortDesc = json.optString("shortDescription", "A legendary wanderer unbound by the laws of destiny.")
        val appearance = json.optString("appearance", "Possesses luminous silver hair and calculating cyan eyes, dressed in cybernetic battle gear.")
        val personality = json.optString("personality", input.personality)
        val backstory = json.optString("backstory", "Forged in the crucible of an astral collapse, seeking redemption across dimensional ruins.")
        val fightingStyle = json.optString("fightingStyle", "Fluid spatial-shift combat paired with precision counterattacks.")
        val weapon = json.optString("weapon", input.weapon)
        val goals = json.optString("goals", "Unravel the secret of the celestial rift and protect their sworn comrades.")
        val catchphrase = json.optString("catchphrase", "\"Reality bends where my blade falls.\"")
        val powerLevel = json.optString("powerLevel", "S-Rank (Apex) • 9,200 / 10,000")
        val cinematicIntro = json.optString("cinematicIntro", "Rain pelts against the neon rooftops as an azure flare cuts through the storm.")
        val imagePrompt = json.optString("imagePrompt", "1boy, master anime illustration, futuristic cyber armor, glowing cyan blade, neon city background, cinematic lighting, 8k")

        // Parse abilities
        val abilitiesList = mutableListOf<AbilityInfo>()
        val abilitiesJson = json.optJSONArray("abilities")
        if (abilitiesJson != null) {
            abilitiesList.addAll(parseAbilities(abilitiesJson))
        }

        // Parse ultimate
        val ultJson = json.optJSONObject("ultimateAbility")
        val ultimate = if (ultJson != null) {
            UltimateAbilityInfo(
                name = ultJson.optString("name", "Celestial Singularity"),
                description = ultJson.optString("description", "Unleashes a gravitational rift compressing all enemy projectiles into starlight."),
                costOrCooldown = ultJson.optString("costOrCooldown", "Consumes 80% mana; 3-minute cooldown.")
            )
        } else {
            UltimateAbilityInfo("Apex Resonance", "Temporarily overclocks all sensory pathways by 300%.", "Strains neurological receptors.")
        }

        // Parse strengths & weaknesses
        val strengths = parseStringList(json.optJSONArray("strengths"))
        val weaknesses = parseStringList(json.optJSONArray("weaknesses"))

        // Parse relationships
        val relationships = mutableListOf<RelationshipInfo>()
        val relArray = json.optJSONArray("relationships")
        if (relArray != null) {
            for (i in 0 until relArray.length()) {
                val rObj = relArray.optJSONObject(i) ?: continue
                relationships.add(
                    RelationshipInfo(
                        targetName = rObj.optString("targetName", "Unknown"),
                        roleType = rObj.optString("roleType", "Ally"),
                        context = rObj.optString("context", "Allied during the Great Incursion.")
                    )
                )
            }
        }

        // Parse stats
        val statsObj = json.optJSONObject("stats")
        val stats = if (statsObj != null) {
            CharacterStats(
                strength = statsObj.optInt("strength", 75).coerceIn(20, 100),
                speed = statsObj.optInt("speed", 82).coerceIn(20, 100),
                defense = statsObj.optInt("defense", 70).coerceIn(20, 100),
                intelligence = statsObj.optInt("intelligence", 88).coerceIn(20, 100),
                magic = statsObj.optInt("magic", 90).coerceIn(20, 100),
                combat = statsObj.optInt("combat", 84).coerceIn(20, 100),
                agility = statsObj.optInt("agility", 85).coerceIn(20, 100),
                endurance = statsObj.optInt("endurance", 76).coerceIn(20, 100)
            )
        } else CharacterStats()

        // Choose avatar type based on role / power
        val avatar = pickAvatar(input.characterRole, input.powerType)

        return AnimeCharacter(
            name = name,
            shortDescription = shortDesc,
            gender = input.gender,
            age = input.age,
            characterRole = input.characterRole,
            alignment = input.alignment,
            powerType = input.powerType,
            powerLevel = powerLevel,
            appearance = appearance,
            hairColor = input.hairColor,
            eyeColor = input.eyeColor,
            outfit = input.outfit,
            height = input.height,
            artStyle = input.artStyle,
            personality = personality,
            backstory = backstory,
            fightingStyle = fightingStyle,
            weapon = weapon,
            abilities = if (abilitiesList.isEmpty()) defaultAbilities() else abilitiesList,
            ultimateAbility = ultimate,
            strengths = if (strengths.isEmpty()) listOf("Exceptional tactical foresight", "Master of aerial displacement") else strengths,
            weaknesses = if (weaknesses.isEmpty()) listOf("Over-relies on high-speed strikes", "Stamina drops in prolonged siege") else weaknesses,
            goals = goals,
            relationships = if (relationships.isEmpty()) defaultRelationships() else relationships,
            catchphrase = catchphrase,
            cinematicIntro = cinematicIntro,
            imagePrompt = imagePrompt,
            stats = stats,
            worldUniverse = input.worldUniverse,
            additionalDetails = input.additionalDetails,
            avatarType = avatar
        )
    }

    private fun parseAbilities(array: JSONArray): List<AbilityInfo> {
        val list = mutableListOf<AbilityInfo>()
        for (i in 0 until array.length()) {
            val obj = array.optJSONObject(i) ?: continue
            list.add(
                AbilityInfo(
                    name = obj.optString("name", "Ability #${i + 1}"),
                    description = obj.optString("description", ""),
                    limitation = obj.optString("limitation", "Requires active concentration")
                )
            )
        }
        return list
    }

    private fun parseStringList(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            val item = array.optString(i)
            if (item.isNotBlank()) list.add(item)
        }
        return list
    }

    private fun cleanJson(text: String): String {
        var str = text.trim()
        if (str.startsWith("```json")) {
            str = str.removePrefix("```json")
        } else if (str.startsWith("```")) {
            str = str.removePrefix("```")
        }
        if (str.endsWith("```")) {
            str = str.removeSuffix("```")
        }
        val firstBrace = str.indexOf('{')
        val lastBrace = str.lastIndexOf('}')
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            str = str.substring(firstBrace, lastBrace + 1)
        }
        return str.trim()
    }

    private fun pickAvatar(role: String, powerType: String): String {
        val text = "$role $powerType".lowercase()
        return when {
            text.contains("mage") || text.contains("magic") || text.contains("astral") || text.contains("celestial") -> "mage"
            text.contains("rogue") || text.contains("stealth") || text.contains("shadow") || text.contains("assassin") -> "rogue"
            else -> "samurai"
        }
    }

    // Procedural generation fallback ensuring 100% reliable original creation
    fun generateProceduralCharacter(
        input: CharacterFormInput,
        errorNotice: String? = null
    ): AnimeCharacter {
        val archetypes = listOf(
            Triple("Aoi 'Zero' Kisaragi", "The Chrono-Phantasm of Sector 9", "samurai"),
            Triple("Lyra Starweaver", "Archon of the Stellar Veil", "mage"),
            Triple("Ren 'Nightfall' Kurogane", "Phantom Operative of the Black Syndicate", "rogue"),
            Triple("Sylas Vane", "Aetheric Vanguard of the Radiant Dominion", "samurai"),
            Triple("Elysia Dawn", "Cyber-Valkyrie of the Celestial Grid", "mage")
        )
        val archetype = archetypes.random()
        val charName = input.name.ifBlank { archetype.first }
        val avatar = pickAvatar(input.characterRole, input.powerType)

        val stats = CharacterStats(
            strength = Random.nextInt(68, 96),
            speed = Random.nextInt(72, 98),
            defense = Random.nextInt(65, 92),
            intelligence = Random.nextInt(75, 99),
            magic = Random.nextInt(70, 97),
            combat = Random.nextInt(74, 96),
            agility = Random.nextInt(78, 98),
            endurance = Random.nextInt(68, 93)
        )

        val prompt = "Cinematic anime portrait of $charName, ${input.gender}, ${input.age} years old, ${input.hairColor} hair, piercing ${input.eyeColor} eyes, wearing ${input.outfit}, wielding ${input.weapon}, glowing neon particles of ${input.powerType}, dramatic dynamic angle, atmospheric lighting, high fidelity key visual, anime concept art masterpiece, 8k resolution"

        return AnimeCharacter(
            name = charName,
            shortDescription = input.additionalDetails.ifBlank { archetype.second },
            gender = input.gender,
            age = input.age,
            characterRole = input.characterRole,
            alignment = input.alignment,
            powerType = input.powerType,
            powerLevel = "S-Rank [Apex Prodigy] • ${stats.overallRating * 115} / 10,000",
            appearance = "Stands at ${input.height} with a commanding presence. Hair flows with radiant ${input.hairColor}, framing acute facial contours with ${input.eyeColor} eyes that shimmer whenever power channels through the soul-matrix. Adorned in ${input.outfit}, accented by subtle energy conductors.",
            hairColor = input.hairColor,
            eyeColor = input.eyeColor,
            outfit = input.outfit,
            height = input.height,
            artStyle = input.artStyle,
            personality = "${input.personality}. Analytical under lethal pressure, yet driven by an unyielding ethical code that defies bureaucratic decree.",
            backstory = "Orphaned during the catastrophic collapse of the Great Chrono-Spire in ${input.worldUniverse}, $charName unlocked an awakening of pure ${input.powerType}. Hunted by corporate hegemony and cosmic syndicates alike, they carved an independent sanctuary, swearing to prevent another timeline fracture.",
            fightingStyle = "A hybrid of high-speed kinematic displacement and tactical close-quarters martial precision, utilizing ${input.weapon} to manipulate local spatial vectors.",
            weapon = input.weapon,
            abilities = listOf(
                AbilityInfo(
                    name = "Tachyon Phasing",
                    description = "Temporarily slips between quantum probability streams to phase through ballistic and energy attacks.",
                    limitation = "Causes micro-fractures in biological tissue if sustained for longer than 4.5 continuous seconds."
                ),
                AbilityInfo(
                    name = "Aether Surge Blade",
                    description = "Condenses high-frequency resonance directly along the weapon's edge, slicing through reinforced alloys.",
                    limitation = "Consumes high internal thermal energy; requires 12 seconds of heat dissipation between strikes."
                ),
                AbilityInfo(
                    name = "Graviton Tether",
                    description = "Fires an invisible kinetic anchor allowing instantaneous directional redirection mid-air.",
                    limitation = "Anchor line breaks if anchored surface density is lower than hardened carbon."
                )
            ),
            ultimateAbility = UltimateAbilityInfo(
                name = "Singularity Event Horizon",
                description = "Channels maximum resonance to collapse regional space into an inescapable luminous distortion vortex, detonating with celestial brilliance.",
                costOrCooldown = "Completely exhausts power reserves; leaves user in defensive stasis for 20 seconds post-cast."
            ),
            strengths = listOf(
                "Superhuman reaction time under extreme stress",
                "Exceptional mastery over spatial topography",
                "Unshakeable battlefield mental resilience"
            ),
            weaknesses = listOf(
                "Internal circuit strain when over-clocking power",
                "Protective instinct for civilians limits area-of-effect lethality",
                "High mana consumption during prolonged attrition warfare"
            ),
            goals = "Locate the lost Stellar Shard, dismantle the shadow cabal threatening ${input.worldUniverse}, and restore balance.",
            relationships = listOf(
                RelationshipInfo("Kairi Valen", "Trusted Ally", "Co-founder of the underground rebellion; manages intel."),
                RelationshipInfo("Lord Obsidian", "Arch-Nemesis", "Commander of the Vanguard Fleet responsible for the Chrono-Spire catastrophe."),
                RelationshipInfo("Master Gendo", "Old Mentor", "Taught the foundational martial philosophy before disappearing into the Void.")
            ),
            catchphrase = "\"Cross this boundary, and not even destiny can piece you back together.\"",
            cinematicIntro = "Neon rain washes over the skyscraper edge as sirens wail below. In the gloom, two ${input.eyeColor} eyes ignite with celestial fire. A hand closes around ${input.weapon}, and with a thunderous sonic crack, $charName descends.",
            imagePrompt = prompt,
            stats = stats,
            worldUniverse = input.worldUniverse,
            additionalDetails = if (errorNotice != null) "Note: Generated via LEO procedural engine ($errorNotice)" else input.additionalDetails,
            avatarType = avatar
        )
    }

    private fun regenerateSectionProcedural(
        character: AnimeCharacter,
        target: RegenTarget
    ): AnimeCharacter {
        return when (target) {
            RegenTarget.BACKSTORY -> character.copy(
                backstory = "Born on the forbidden perimeter of the Astral Outpost, ${character.name} survived a synthetic supernova by merging their consciousness with a dormant singularity core. Ever since, they have walked the boundary between worlds, seeking the architect behind the cosmic catastrophe.",
                goals = "Uncover the identity of the Architect and purge the synthetic plague threatening free colonies."
            )
            RegenTarget.ABILITIES -> character.copy(
                abilities = listOf(
                    AbilityInfo("Phase Drift", "Instantly teleports 15 meters along line of sight with a sonic boom.", "Requires visible trajectory; unusable in pitch void."),
                    AbilityInfo("Kinetic Overdrive", "Multiplies physical strike force by converting incoming momentum.", "Strains muscle fibers exponentially."),
                    AbilityInfo("Prismatic Barrier", "Projects an iridescent hexagonal shield deflecting elemental blasts.", "Can only withstand 3 consecutive heavy impacts.")
                ),
                fightingStyle = "Aggressive pressure-heavy rushdown combat alternating between point-blank teleports and crushing kinetic bursts."
            )
            RegenTarget.APPEARANCE -> character.copy(
                appearance = "Features an asymmetrical hairstyle with glowing neon highlights, donning an aerodynamic armored coat with carbon-fiber weave and glowing power glyphs.",
                imagePrompt = "Masterpiece anime character art of ${character.name}, dynamic pose, glowing energy glyphs, stylish cyber jacket, cinematic lighting, 8k anime key visual"
            )
            RegenTarget.PERSONALITY -> character.copy(
                personality = "Charismatic, quietly intense, and fiercely principled. Conceals deep compassion beneath a sharp cynical wit.",
                catchphrase = "\"Don't blink. You might miss your only chance to surrender.\""
            )
            RegenTarget.ULTIMATE_ABILITY -> character.copy(
                ultimateAbility = UltimateAbilityInfo(
                    name = "Cosmic Cataclysm: Supernova Burst",
                    description = "Ignites the entire surrounding atmosphere into a radiant plasma tempest that incinerates hostile matter.",
                    costOrCooldown = "Requires 100% burst gauge; inflicts 15 seconds of sensory blindness."
                )
            )
            RegenTarget.ENTIRE_CHARACTER -> character
        }
    }

    private fun defaultAbilities(): List<AbilityInfo> = listOf(
        AbilityInfo("Tachyon Strike", "High velocity attack bypassing physical armor.", "5s cooldown"),
        AbilityInfo("Aether Shield", "Defensive barrier absorbing elemental damage.", "Consumes mana")
    )

    private fun defaultRelationships(): List<RelationshipInfo> = listOf(
        RelationshipInfo("Kairi", "Ally", "Rebellion strategist."),
        RelationshipInfo("Obsidian", "Nemesis", "Imperial warlord.")
    )
}
