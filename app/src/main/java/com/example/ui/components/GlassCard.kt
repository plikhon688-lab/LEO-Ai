package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BorderCyan
import com.example.ui.theme.BorderGlow
import com.example.ui.theme.CardSurfaceGlass

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(18.dp),
    borderColors: List<Color> = listOf(BorderGlow.copy(alpha = 0.8f), BorderCyan.copy(alpha = 0.4f)),
    borderWidth: Dp = 1.dp,
    backgroundColor: Color = CardSurfaceGlass,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(
                borderWidth,
                Brush.linearGradient(borderColors),
                shape
            )
    ) {
        content()
    }
}
