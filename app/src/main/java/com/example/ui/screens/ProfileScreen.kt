package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.AbilityInfo
import com.example.data.model.AnimeCharacter
import com.example.data.model.RegenTarget
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.FuturisticButton
import com.example.ui.components.FuturisticOutlineButton
import com.example.ui.components.GlassCard
import com.example.ui.components.RadarStatsChart
import com.example.ui.components.StatProgressBar
import com.example.ui.theme.AccentGold
import com.example.ui.theme.CyberPink
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.MatrixGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.VoidBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val character = viewModel.currentCharacter.collectAsStateWithLifecycle().value
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var showRegenSheet by remember { mutableStateOf(false) }

    if (character == null) {
        Box(
            modifier = modifier.fillMaxSize().background(VoidBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("No character selected.", color = TextPrimary)
        }
        return
    }

    val tabs = listOf(
        "Overview",
        "Appearance",
        "Personality",
        "Backstory",
        "Abilities",
        "Combat & Stats",
        "Relationships",
        "Prompt"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 36.dp)
            .testTag("profile_screen")
    ) {
        // Top Bar Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FuturisticOutlineButton(
                text = "← Create Another",
                onClick = { viewModel.navigateTo(AppScreen.CREATION) },
                accentColor = ElectricCyan,
                testTag = "create_another_button"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // Save Character Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x33B388FF))
                        .border(1.dp, NeonPurple, RoundedCornerShape(12.dp))
                        .clickable { viewModel.saveCurrentCharacter() }
                        .testTag("save_character_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "Save Character",
                        tint = NeonPurple,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Share Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x3300E5FF))
                        .border(1.dp, ElectricCyan, RoundedCornerShape(12.dp))
                        .clickable { shareCharacter(context, character) }
                        .testTag("share_character_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share Character",
                        tint = ElectricCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Section 4: Character Card Hero Display
        CharacterCardHero(
            character = character,
            onAvatarChange = { newAvatar -> viewModel.updateAvatar(newAvatar) },
            onSaveClick = { viewModel.saveCurrentCharacter() }
        )

        Spacer(modifier = Modifier.height(18.dp))

        // Horizontal Segmented Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEachIndexed { index, title ->
                val isSelected = selectedTab == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isSelected) Brush.horizontalGradient(listOf(NeonPurple, ElectricCyan))
                            else Brush.horizontalGradient(listOf(Color(0xFF191438), Color(0xFF141030)))
                        )
                        .border(
                            1.dp,
                            if (isSelected) Color.Transparent else Color(0xFF2C2454),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable { selectedTab = index }
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                        .testTag("tab_${title.lowercase()}")
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) VoidBlack else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Tab Content
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            when (selectedTab) {
                0 -> OverviewSection(character, context)
                1 -> AppearanceSection(character)
                2 -> PersonalitySection(character)
                3 -> BackstorySection(character)
                4 -> AbilitiesSection(character)
                5 -> CombatAndStatsSection(character)
                6 -> RelationshipsSection(character)
                7 -> ImagePromptSection(character, context, viewModel)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons Row: Regenerate & Copy Character
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FuturisticOutlineButton(
                text = "Regenerate...",
                icon = Icons.Default.Refresh,
                onClick = { showRegenSheet = true },
                modifier = Modifier.weight(1f),
                accentColor = CyberPink,
                testTag = "regenerate_dialog_button"
            )

            FuturisticButton(
                text = "Copy Profile",
                icon = Icons.Default.ContentCopy,
                onClick = {
                    copyToClipboard(context, character.toMarkdownSummary())
                    viewModel.showToast("Copied entire character profile to clipboard!")
                },
                modifier = Modifier.weight(1f),
                testTag = "copy_character_button"
            )
        }
    }

    // Modal Sheet for Section Regeneration
    if (showRegenSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRegenSheet = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = Color(0xFF110E28)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .testTag("regen_sheet")
            ) {
                Text(
                    text = "Regenerate Section",
                    color = ElectricCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Choose which part of ${character.name} to rewrite with AI:",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                RegenTarget.values().forEach { target ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1B163B))
                            .border(1.dp, Color(0xFF2E275E), RoundedCornerShape(12.dp))
                            .clickable {
                                showRegenSheet = false
                                viewModel.regenerate(target)
                            }
                            .padding(horizontal = 16.dp, vertical = 13.dp)
                            .testTag("regen_target_${target.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = target.displayName,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = NeonPurple,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ---------------- SECTION COMPOSABLES ---------------- //

@Composable
fun CharacterCardHero(
    character: AnimeCharacter,
    onAvatarChange: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    val avatarRes = when (character.avatarType) {
        "mage" -> R.drawable.mystic_mage
        "rogue" -> R.drawable.shadow_rogue
        else -> R.drawable.cyber_samurai
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .testTag("character_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Character Portrait Area with avatar switcher
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            2.dp,
                            Brush.sweepGradient(listOf(ElectricCyan, NeonPurple, CyberPink, ElectricCyan)),
                            RoundedCornerShape(16.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = "Character Portrait",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Name, Role, Power level info
                Column(modifier = Modifier.weight(1f)) {
                    // Alignment & Role Badges
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        BadgePill(text = character.alignment, color = CyberPink)
                        BadgePill(text = character.characterRole, color = ElectricCyan)
                    }

                    Text(
                        text = character.name,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = character.powerLevel,
                        color = AccentGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "⚡ " + (character.abilities.firstOrNull()?.name ?: "Primary Ability"),
                        color = NeonPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = "🗡 " + character.weapon,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Short Description
            Text(
                text = character.shortDescription,
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Avatar Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Portrait Archetype:",
                    color = TextTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("samurai" to "Samurai", "mage" to "Mage", "rogue" to "Rogue").forEach { (type, label) ->
                        val isCurrent = character.avatarType == type
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) NeonPurple.copy(alpha = 0.3f) else Color(0xFF1F1B3E))
                                .border(1.dp, if (isCurrent) ElectricCyan else Color(0xFF322A5E), RoundedCornerShape(8.dp))
                                .clickable { onAvatarChange(type) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isCurrent) ElectricCyan else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OverviewSection(character: AnimeCharacter, context: Context) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Cinematic Intro
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CINEMATIC INTRODUCTION",
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = character.cinematicIntro,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 21.sp
                )
            }
        }

        // Signature Catchphrase
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "SIGNATURE CATCHPHRASE",
                    color = CyberPink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = character.catchphrase,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
            }
        }

        // Character Goals & Universe
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "CORE MISSION & GOAL",
                    color = NeonPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = character.goals,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "WORLD / UNIVERSE",
                    color = AccentGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = character.worldUniverse,
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun AppearanceSection(character: AnimeCharacter) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "DETAILED APPEARANCE",
                color = ElectricCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = character.appearance,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Specs grid
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SpecRow(label = "Hair Color", value = character.hairColor)
                SpecRow(label = "Eye Color", value = character.eyeColor)
                SpecRow(label = "Height", value = character.height)
                SpecRow(label = "Art Style", value = character.artStyle)
                SpecRow(label = "Signature Outfit", value = character.outfit)
            }
        }
    }
}

@Composable
fun PersonalitySection(character: AnimeCharacter) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "PSYCHOLOGICAL PROFILE & PERSONA",
                color = NeonPurple,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = character.personality,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
fun BackstorySection(character: AnimeCharacter) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "ORIGIN & LORE CHRONICLE",
                color = AccentGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = character.backstory,
                color = TextPrimary,
                fontSize = 14.sp,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun AbilitiesSection(character: AnimeCharacter) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Ultimate Ability Highlight Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColors = listOf(CyberPink, NeonPurple)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "★ ULTIMATE ABILITY",
                        color = CyberPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.5.sp
                    )
                    BadgePill(text = "Signature", color = CyberPink)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = character.ultimateAbility.name,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = character.ultimateAbility.description,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Cost & Drawback: ${character.ultimateAbility.costOrCooldown}",
                    color = AccentGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Standard Abilities
        Text(
            text = "COMBAT SKILLS & TECHNIQUES",
            color = ElectricCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
        )

        character.abilities.forEachIndexed { index, ability ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${index + 1}. ${ability.name}",
                            color = ElectricCyan,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = ability.description,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Limitation: ${ability.limitation}",
                        color = TextTertiary,
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Composable
fun CombatAndStatsSection(character: AnimeCharacter) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Power Radar Chart
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ATTRIBUTE MATRIX",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = character.stats.rankTier,
                        color = AccentGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                RadarStatsChart(
                    stats = character.stats,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                )
            }
        }

        // Animated Stat Progress Bars
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "DETAILED POWER METRICS",
                    color = NeonPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                StatProgressBar("Strength", character.stats.strength, CyberPink)
                StatProgressBar("Speed", character.stats.speed, ElectricCyan)
                StatProgressBar("Defense", character.stats.defense, MatrixGreen)
                StatProgressBar("Intelligence", character.stats.intelligence, NeonPurple)
                StatProgressBar("Magic / Resonance", character.stats.magic, AccentGold)
                StatProgressBar("Combat Prowess", character.stats.combat, CyberPink)
                StatProgressBar("Agility", character.stats.agility, ElectricCyan)
                StatProgressBar("Endurance", character.stats.endurance, MatrixGreen)
            }
        }

        // Fighting Style & Weapon
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "COMBAT PHILOSOPHY & WEAPONRY",
                    color = AccentGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = character.fightingStyle,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "WEAPON SPECIFICATION",
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = character.weapon,
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Strengths and Weaknesses
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "STRENGTHS",
                            color = MatrixGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        character.strengths.forEach { s ->
                            Text(text = "• $s", color = TextSecondary, fontSize = 12.sp)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "WEAKNESSES",
                            color = CyberPink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        character.weaknesses.forEach { w ->
                            Text(text = "• $w", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RelationshipsSection(character: AnimeCharacter) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (character.relationships.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "No relationships recorded.",
                    color = TextSecondary,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            character.relationships.forEach { rel ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = rel.targetName,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            BadgePill(text = rel.roleType, color = NeonPurple)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = rel.context,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImagePromptSection(
    character: AnimeCharacter,
    context: Context,
    viewModel: MainViewModel
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("image_prompt_card")
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "AI IMAGE GENERATION PROMPT",
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                BadgePill(text = "Ready to Copy", color = ElectricCyan)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Use this master prompt in any image generator (Midjourney, DALL-E, Stable Diffusion, Imagen):",
                color = TextTertiary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0F0B24))
                    .border(1.dp, Color(0xFF282050), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = character.imagePrompt,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    fontStyle = FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            FuturisticButton(
                text = "Copy Image Prompt",
                icon = Icons.Default.ContentCopy,
                onClick = {
                    copyToClipboard(context, character.imagePrompt)
                    viewModel.showToast("Copied image prompt to clipboard!")
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = "copy_image_prompt_button"
            )
        }
    }
}

// ---------------- HELPERS ---------------- //

@Composable
fun BadgePill(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextTertiary, fontSize = 12.sp)
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("LEO Character", text)
    clipboard.setPrimaryClip(clip)
}

fun shareCharacter(context: Context, character: AnimeCharacter) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, character.toMarkdownSummary())
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Share ${character.name}")
    context.startActivity(shareIntent)
}

fun AnimeCharacter.toMarkdownSummary(): String {
    return """
        # LEO AI - $name
        **Role:** $characterRole | **Alignment:** $alignment | **Power Level:** $powerLevel
        **Power Type:** $powerType | **Weapon:** $weapon
        
        ## Overview
        $shortDescription
        
        ## Cinematic Intro
        "$cinematicIntro"
        
        ## Catchphrase
        "$catchphrase"
        
        ## Appearance
        $appearance
        (Hair: $hairColor, Eyes: $eyeColor, Height: $height, Outfit: $outfit)
        
        ## Personality
        $personality
        
        ## Backstory
        $backstory
        
        ## Ultimate Ability: ${ultimateAbility.name}
        ${ultimateAbility.description}
        *Cost/Cooldown:* ${ultimateAbility.costOrCooldown}
        
        ## Abilities
        ${abilities.joinToString("\n") { "- **${it.name}:** ${it.description} (Limitation: ${it.limitation})" }}
        
        ## Power Stats
        STR: ${stats.strength} | SPD: ${stats.speed} | DEF: ${stats.defense} | INT: ${stats.intelligence}
        MAG: ${stats.magic} | CMB: ${stats.combat} | AGI: ${stats.agility} | END: ${stats.endurance}
        
        ## Image Prompt
        $imagePrompt
    """.trimIndent()
}
