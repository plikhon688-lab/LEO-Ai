package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CharacterFormInput
import com.example.ui.AppScreen
import com.example.ui.MainViewModel
import com.example.ui.components.FuturisticButton
import com.example.ui.components.FuturisticOutlineButton
import com.example.ui.components.GlassCard
import com.example.ui.theme.CyberPink
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.VoidBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreationScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val formInput by viewModel.formInput.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    val presets = listOf(
        "Cyber Samurai" to Triple("Neo-Tokyo Cyberpunk", "Tachyon Kodachi", "Psionic / Space-Time"),
        "Astral Archon" to Triple("Cosmic Fantasy", "Celestial Orbs", "Astral Light / Void"),
        "Shadow Operative" to Triple("Dark Seinen", "Monofilament Daggers", "Umbral Phasing"),
        "Mecha Ace" to Triple("90s Sci-Fi Mecha", "Beam Saber & Railgun", "Neural Hyper-Sync"),
        "Eldritch Alchemist" to Triple("Gothic Horror", "Forbidden Grimoire", "Blood Transmutation")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VoidBlack)
            .verticalScroll(scrollState)
            .padding(bottom = 32.dp)
            .testTag("creation_screen")
    ) {
        // Hero Header with Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.nexa_banner),
                contentDescription = "LEO AI Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                VoidBlack.copy(alpha = 0.6f),
                                VoidBlack
                            )
                        )
                    )
            )

            // Header Content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.25f))
                                .border(1.dp, NeonPurple, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "LEO AI",
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Create your own legendary character.",
                        color = ElectricCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // History shortcut button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x66181438))
                        .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .clickable { viewModel.navigateTo(AppScreen.HISTORY) }
                        .testTag("history_nav_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "View History",
                        tint = ElectricCyan,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // Quick Preset Chips
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "QUICK ARCHETYPES",
                color = TextTertiary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { (presetName, presetData) ->
                    val isSelected = formInput.artStyle.contains(presetData.first.take(5))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) NeonPurple.copy(alpha = 0.25f) else Color(0xFF1B1638))
                            .border(
                                1.dp,
                                if (isSelected) ElectricCyan else Color(0xFF332A5C),
                                RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                viewModel.updateFormInput {
                                    it.copy(
                                        artStyle = presetData.first,
                                        weapon = presetData.second,
                                        powerType = presetData.third
                                    )
                                }
                            }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = presetName,
                            color = if (isSelected) ElectricCyan else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Top Action Bar: Generate Character + Random Character
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FuturisticButton(
                text = "Generate Character",
                icon = Icons.Default.FlashOn,
                onClick = { viewModel.generateCharacter() },
                isLoading = isGenerating,
                modifier = Modifier.weight(1.3f),
                testTag = "generate_character_button"
            )

            FuturisticOutlineButton(
                text = "Random",
                icon = Icons.Default.Casino,
                onClick = { viewModel.randomizeForm() },
                modifier = Modifier.weight(0.9f),
                testTag = "random_character_button"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Form Section 1: Identity
        FormSectionCard(
            title = "1. Identity & Persona",
            icon = Icons.Default.Person,
            accentColor = NeonPurple
        ) {
            CustomTextField(
                label = "Character Name",
                value = formInput.name,
                placeholder = "e.g. Ren Kurogane (leave blank for AI generated)",
                onValueChange = { value -> viewModel.updateFormInput { it.copy(name = value) } },
                testTag = "input_character_name"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CustomTextField(
                    label = "Gender",
                    value = formInput.gender,
                    placeholder = "Female / Male / Android",
                    onValueChange = { value -> viewModel.updateFormInput { it.copy(gender = value) } },
                    modifier = Modifier.weight(1f),
                    testTag = "input_gender"
                )
                CustomTextField(
                    label = "Age",
                    value = formInput.age,
                    placeholder = "e.g. 19 / Immortal",
                    onValueChange = { value -> viewModel.updateFormInput { it.copy(age = value) } },
                    modifier = Modifier.weight(1f),
                    testTag = "input_age"
                )
            }

            CustomTextField(
                label = "Height",
                value = formInput.height,
                placeholder = "e.g. 172 cm (5'8\")",
                onValueChange = { value -> viewModel.updateFormInput { it.copy(height = value) } },
                testTag = "input_height"
            )

            CustomTextField(
                label = "Personality",
                value = formInput.personality,
                placeholder = "e.g. Stoic, Sharp-witted, Fiercely Protective",
                onValueChange = { value -> viewModel.updateFormInput { it.copy(personality = value) } },
                singleLine = false,
                testTag = "input_personality"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Section 2: Style & Appearance
        FormSectionCard(
            title = "2. Visual Style & Appearance",
            icon = Icons.Default.ColorLens,
            accentColor = ElectricCyan
        ) {
            CustomTextField(
                label = "Anime / Art Style",
                value = formInput.artStyle,
                placeholder = "e.g. Cyberpunk Shonen, Dark Fantasy Seinen",
                onValueChange = { value -> viewModel.updateFormInput { it.copy(artStyle = value) } },
                testTag = "input_art_style"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CustomTextField(
                    label = "Hair Color",
                    value = formInput.hairColor,
                    placeholder = "Silver with neon violet",
                    onValueChange = { value -> viewModel.updateFormInput { it.copy(hairColor = value) } },
                    modifier = Modifier.weight(1f),
                    testTag = "input_hair_color"
                )
                CustomTextField(
                    label = "Eye Color",
                    value = formInput.eyeColor,
                    placeholder = "Electric Cyan",
                    onValueChange = { value -> viewModel.updateFormInput { it.copy(eyeColor = value) } },
                    modifier = Modifier.weight(1f),
                    testTag = "input_eye_color"
                )
            }

            CustomTextField(
                label = "Outfit & Gear",
                value = formInput.outfit,
                placeholder = "e.g. Cyber trenchcoat with holographic trim & tactical greaves",
                onValueChange = { value -> viewModel.updateFormInput { it.copy(outfit = value) } },
                singleLine = false,
                testTag = "input_outfit"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Section 3: Power & Combat
        FormSectionCard(
            title = "3. Power System & Combat",
            icon = Icons.Default.Shield,
            accentColor = CyberPink
        ) {
            CustomTextField(
                label = "Power Type",
                value = formInput.powerType,
                placeholder = "e.g. Psionic / Space-Time, Elemental, Cybernetic",
                onValueChange = { value -> viewModel.updateFormInput { it.copy(powerType = value) } },
                testTag = "input_power_type"
            )

            CustomTextField(
                label = "Special Abilities",
                value = formInput.specialAbilities,
                placeholder = "e.g. Chrono-phase displacement, Aether blade projection",
                onValueChange = { value -> viewModel.updateFormInput { it.copy(specialAbilities = value) } },
                singleLine = false,
                testTag = "input_special_abilities"
            )

            CustomTextField(
                label = "Weapon",
                value = formInput.weapon,
                placeholder = "e.g. Twin Tachyon Kodachi blades",
                onValueChange = { value -> viewModel.updateFormInput { it.copy(weapon = value) } },
                testTag = "input_weapon"
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                CustomTextField(
                    label = "Character Role",
                    value = formInput.characterRole,
                    placeholder = "Protagonist, Rival, Mentor",
                    onValueChange = { value -> viewModel.updateFormInput { it.copy(characterRole = value) } },
                    modifier = Modifier.weight(1f),
                    testTag = "input_character_role"
                )
                CustomTextField(
                    label = "Alignment",
                    value = formInput.alignment,
                    placeholder = "Hero / Anti-Hero / Villain",
                    onValueChange = { value -> viewModel.updateFormInput { it.copy(alignment = value) } },
                    modifier = Modifier.weight(1f),
                    testTag = "input_alignment"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Form Section 4: Universe & Lore
        FormSectionCard(
            title = "4. Universe & Additional Lore",
            icon = Icons.Default.Public,
            accentColor = NeonPurple
        ) {
            CustomTextField(
                label = "World / Universe",
                value = formInput.worldUniverse,
                placeholder = "e.g. Neo-Akira 2189 / Astral Megacity",
                onValueChange = { value -> viewModel.updateFormInput { it.copy(worldUniverse = value) } },
                testTag = "input_world_universe"
            )

            CustomTextField(
                label = "Additional Details / Lore Notes",
                value = formInput.additionalDetails,
                placeholder = "Secret relics, tragic backstory hints, or unique traits...",
                onValueChange = { value -> viewModel.updateFormInput { it.copy(additionalDetails = value) } },
                singleLine = false,
                testTag = "input_additional_details"
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Bottom CTA Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FuturisticButton(
                text = "⚡ Synthesize Legendary Character",
                icon = Icons.Default.AutoAwesome,
                onClick = { viewModel.generateCharacter() },
                isLoading = isGenerating,
                modifier = Modifier.fillMaxWidth(),
                testTag = "bottom_generate_button"
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Original fictional creations • Gemini 3.5 Flash Engine",
                color = TextTertiary,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun FormSectionCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            content()
        }
    }
}

@Composable
fun CustomTextField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    testTag: String = ""
) {
    Column(modifier = modifier.padding(bottom = 10.dp)) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = TextTertiary, fontSize = 13.sp) },
            singleLine = singleLine,
            maxLines = if (singleLine) 1 else 3,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0x33100C27),
                unfocusedContainerColor = Color(0x22100C27),
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = Color(0xFF2C2454),
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            )
        )
    }
}
