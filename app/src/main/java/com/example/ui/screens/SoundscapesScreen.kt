package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Nature
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Waves
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalmSkyBlue
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SageGreenPrimary
import com.example.ui.theme.WarmSand
import com.example.ui.theme.ZenGold
import com.example.viewmodel.SerenityViewModel
import kotlin.math.sin

data class SoundscapePreset(
    val name: String,
    val rain: Float,
    val ocean: Float,
    val om: Float,
    val crickets: Float
)

val soundPresets = listOf(
    SoundscapePreset("Deep Sleep Rain", rain = 0.85f, ocean = 0.15f, om = 0.25f, crickets = 0.1f),
    SoundscapePreset("Zen Temple 432Hz", rain = 0.2f, ocean = 0.1f, om = 0.8f, crickets = 0.3f),
    SoundscapePreset("Pacific Tide", rain = 0.1f, ocean = 0.85f, om = 0.2f, crickets = 0.15f),
    SoundscapePreset("Forest Twilight", rain = 0.3f, ocean = 0.1f, om = 0.2f, crickets = 0.8f)
)

@Composable
fun SoundscapesScreen(
    viewModel: SerenityViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.soundscapeState.collectAsState()
    val scrollState = rememberScrollState()

    val infiniteWave = rememberInfiniteTransition(label = "audioWave")
    val wavePhase by infiniteWave.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Presets Chips
        Text(
            text = "Calming Soundscape Presets",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(soundPresets) { preset ->
                FilterChip(
                    selected = false,
                    onClick = {
                        viewModel.haptic.gentlePulse()
                        viewModel.updateSoundscapeVolumes(
                            rain = preset.rain,
                            ocean = preset.ocean,
                            om = preset.om,
                            crickets = preset.crickets
                        )
                        if (!state.isPlaying) {
                            viewModel.toggleSoundscape()
                        }
                    },
                    label = { Text(preset.name, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.testTag("preset_${preset.name.lowercase().replace(' ', '_')}")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Solfeggio Healing Frequencies
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Meditation Tone Frequency",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = state.droneLabel,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        val solfeggioList = listOf(
            396.0 to "396Hz Grounding",
            432.0 to "432Hz Nature Peace",
            528.0 to "528Hz Miracle Calm",
            639.0 to "639Hz Heart Harmony",
            852.0 to "852Hz Deep Clarity"
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(solfeggioList) { (hz, label) ->
                val isSelected = state.droneFrequencyHz == hz
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        viewModel.setDroneFrequency(hz, label)
                    },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    modifier = Modifier.testTag("solfeggio_${hz.toInt()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Audio Wave visualizer card
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            val outlineColor = MaterialTheme.colorScheme.outlineVariant
            Box(modifier = Modifier.fillMaxSize()) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val midY = h / 2f

                    if (state.isPlaying) {
                        for (i in 0 until 40) {
                            val x = (w / 40f) * i + 8f
                            val animAmp = if (state.isPlaying) (sin(wavePhase + i * 0.35f) * 0.5f + 0.5f) else 0.05f
                            val barH = (35.dp.toPx() * animAmp * state.masterVolume).coerceAtLeast(4.dp.toPx())

                            drawLine(
                                brush = Brush.verticalGradient(
                                    colors = listOf(CalmSkyBlue, SageGreenPrimary)
                                ),
                                start = Offset(x, midY - barH / 2f),
                                end = Offset(x, midY + barH / 2f),
                                strokeWidth = 5.dp.toPx()
                            )
                        }
                    } else {
                        // Flat line when stopped
                        drawLine(
                            color = outlineColor,
                            start = Offset(20f, midY),
                            end = Offset(w - 20f, midY),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                }

                // Playing status text
                Text(
                    text = if (state.isPlaying) "Ambient soundscape active" else "Tap Play to begin ambient calm",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Play / Pause Button
        Button(
            onClick = { viewModel.toggleSoundscape() },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("soundscape_play_toggle_button"),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (state.isPlaying) MaterialTheme.colorScheme.secondary else SageGreenPrimary
            )
        ) {
            Icon(
                imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (state.isPlaying) "Pause" else "Play"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (state.isPlaying) "Pause Soundscape" else "Play Nature Ambiance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Timer Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Timer",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (state.timerRemainingSeconds > 0) {
                        val m = state.timerRemainingSeconds / 60
                        val s = state.timerRemainingSeconds % 60
                        "Sleep Timer: %02d:%02d".format(m, s)
                    } else {
                        "Sleep Timer"
                    },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(0 to "Off", 15 to "15m", 30 to "30m", 60 to "60m").forEach { (mins, label) ->
                    FilterChip(
                        selected = (mins == 0 && state.timerRemainingSeconds == 0) || (mins > 0 && state.timerRemainingSeconds > (mins - 1) * 60 && state.timerRemainingSeconds <= mins * 60),
                        onClick = { viewModel.setSoundscapeTimer(mins) },
                        label = { Text(label, fontSize = 11.sp) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Individual Channels Mixer
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Soundscape Mixer Channels",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                SoundSliderRow(
                    title = "Gentle Rainfall",
                    icon = Icons.Default.WaterDrop,
                    value = state.rainVolume,
                    onValueChange = { viewModel.updateSoundscapeVolumes(rain = it) },
                    accentColor = CalmSkyBlue
                )

                SoundSliderRow(
                    title = "Ocean Wave Swell",
                    icon = Icons.Default.Waves,
                    value = state.oceanVolume,
                    onValueChange = { viewModel.updateSoundscapeVolumes(ocean = it) },
                    accentColor = SageGreenLight
                )

                SoundSliderRow(
                    title = "432 Hz Tibetan Drone",
                    icon = Icons.Default.SelfImprovement,
                    value = state.omDroneVolume,
                    onValueChange = { viewModel.updateSoundscapeVolumes(om = it) },
                    accentColor = ZenGold
                )

                SoundSliderRow(
                    title = "Night Crickets & Breeze",
                    icon = Icons.Default.Nature,
                    value = state.cricketsVolume,
                    onValueChange = { viewModel.updateSoundscapeVolumes(crickets = it) },
                    accentColor = WarmSand
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Master Volume
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Master Volume",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${(state.masterVolume * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Slider(
                    value = state.masterVolume,
                    onValueChange = { viewModel.updateSoundscapeVolumes(master = it) },
                    colors = SliderDefaults.colors(
                        thumbColor = SageGreenPrimary,
                        activeTrackColor = SageGreenPrimary
                    )
                )
            }
        }
    }
}

@Composable
fun SoundSliderRow(
    title: String,
    icon: ImageVector,
    value: Float,
    onValueChange: (Float) -> Unit,
    accentColor: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(accentColor.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "${(value * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
