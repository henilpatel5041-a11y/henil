package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalmSkyBlue
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SageGreenPrimary
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight
import com.example.ui.theme.ZenGold
import com.example.viewmodel.SerenityViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class BreathPhase(
    val name: String,
    val durationSeconds: Float,
    val instruction: String,
    val targetScale: Float,
    val isExhale: Boolean = false
)

data class BreathTechnique(
    val id: String,
    val title: String,
    val subtitle: String,
    val phases: List<BreathPhase>
)

val breathTechniques = listOf(
    BreathTechnique(
        id = "478",
        title = "4-7-8 Relax",
        subtitle = "Deep calm & sleep ease",
        phases = listOf(
            BreathPhase("Inhale", 4.0f, "Breathe in softly through nose", 1.0f),
            BreathPhase("Hold", 7.0f, "Hold breath gently with ease", 1.0f),
            BreathPhase("Exhale", 8.0f, "Release completely through lips", 0.45f, isExhale = true)
        )
    ),
    BreathTechnique(
        id = "box",
        title = "Box 4-4-4-4",
        subtitle = "Navy SEAL focus & stability",
        phases = listOf(
            BreathPhase("Inhale", 4.0f, "Deep smooth inhale", 1.0f),
            BreathPhase("Hold", 4.0f, "Sustain calm fullness", 1.0f),
            BreathPhase("Exhale", 4.0f, "Controlled steady release", 0.45f, isExhale = true),
            BreathPhase("Hold Empty", 4.0f, "Rest in still awareness", 0.45f)
        )
    ),
    BreathTechnique(
        id = "coherent",
        title = "Coherent HRV",
        subtitle = "5.5s heart balance rhythm",
        phases = listOf(
            BreathPhase("Inhale", 5.5f, "Fluid, gentle expansion", 1.0f),
            BreathPhase("Exhale", 5.5f, "Slow, warm unburdening", 0.45f, isExhale = true)
        )
    ),
    BreathTechnique(
        id = "sigh",
        title = "Physiological Sigh",
        subtitle = "Fastest neuro panic relief",
        phases = listOf(
            BreathPhase("First Inhale", 2.0f, "Deep breath in through nose", 0.8f),
            BreathPhase("Top-up Inhale", 1.5f, "Sniff in second sharp breath", 1.0f),
            BreathPhase("Long Exhale", 6.0f, "Long slow sigh through mouth", 0.45f, isExhale = true)
        )
    ),
    BreathTechnique(
        id = "pmr",
        title = "Muscle Release",
        subtitle = "PMR tension scan & melt",
        phases = listOf(
            BreathPhase("Tense Brow", 5.0f, "Gently scrunch forehead and brow", 0.95f),
            BreathPhase("Smooth Brow", 8.0f, "Release and feel forehead soften completely", 0.45f, isExhale = true),
            BreathPhase("Tense Shoulders", 5.0f, "Shrug shoulders tightly up towards ears", 0.95f),
            BreathPhase("Drop Shoulders", 8.0f, "Drop shoulders down and release heavy burdens", 0.45f, isExhale = true),
            BreathPhase("Clench Hands", 5.0f, "Gently clench both hands into fists", 0.95f),
            BreathPhase("Open Palms", 8.0f, "Open palms softly and let tension drain away", 0.45f, isExhale = true)
        )
    )
)

@Composable
fun BreathingScreen(
    viewModel: SerenityViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTechnique by remember { mutableStateOf(breathTechniques.first()) }
    var isRunning by remember { mutableStateOf(false) }
    var currentPhaseIndex by remember { mutableIntStateOf(0) }
    var phaseSecondsRemaining by remember { mutableIntStateOf(selectedTechnique.phases.first().durationSeconds.toInt()) }
    var cyclesCompleted by remember { mutableIntStateOf(0) }
    var totalSecondsElapsed by remember { mutableIntStateOf(0) }

    var soundChimesEnabled by remember { mutableStateOf(true) }
    var hapticsEnabled by remember { mutableStateOf(true) }

    val orbScale = remember { Animatable(0.45f) }
    val currentPhase = selectedTechnique.phases[currentPhaseIndex]

    // Reset when switching technique
    fun switchTechnique(newTechnique: BreathTechnique) {
        viewModel.haptic.gentlePulse()
        isRunning = false
        selectedTechnique = newTechnique
        currentPhaseIndex = 0
        phaseSecondsRemaining = newTechnique.phases.first().durationSeconds.toInt()
        cyclesCompleted = 0
    }

    // Breathing Engine loop
    LaunchedEffect(isRunning, selectedTechnique) {
        if (!isRunning) {
            orbScale.animateTo(0.45f, tween(600))
            return@LaunchedEffect
        }

        while (isActive && isRunning) {
            val phase = selectedTechnique.phases[currentPhaseIndex]
            val durationMs = (phase.durationSeconds * 1000).toInt()

            // Play transition cue
            if (soundChimesEnabled) {
                viewModel.onBreathPhaseChanged(phase.isExhale)
            } else if (hapticsEnabled) {
                viewModel.haptic.gentlePulse()
            }

            // Animate orb expansion or contraction smoothly for the exact phase duration
            launch {
                orbScale.animateTo(
                    targetValue = phase.targetScale,
                    animationSpec = tween(
                        durationMillis = durationMs,
                        easing = if (phase.name.contains("Hold")) LinearEasing else FastOutSlowInEasing
                    )
                )
            }

            // Count seconds down in this phase
            var remaining = phase.durationSeconds.toInt()
            while (remaining > 0 && isRunning) {
                phaseSecondsRemaining = remaining
                delay(1000L)
                remaining--
                totalSecondsElapsed++
            }

            if (!isRunning) break

            // Advance to next phase
            val nextIndex = (currentPhaseIndex + 1) % selectedTechnique.phases.size
            if (nextIndex == 0) {
                cyclesCompleted++
                // Log mindful session into database every completed cycle
                viewModel.recordSession(
                    type = "Breathing (${selectedTechnique.title})",
                    durationSeconds = selectedTechnique.phases.sumOf { it.durationSeconds.toInt() },
                    cycles = 1
                )
            }
            currentPhaseIndex = nextIndex
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Technique selector chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(breathTechniques) { technique ->
                val isSelected = selectedTechnique.id == technique.id
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .clickable { switchTechnique(technique) }
                        .testTag("breath_technique_${technique.id}")
                ) {
                    Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
                        Text(
                            text = technique.title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = technique.subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Center Visual Breath Orb
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            // Background breathing rings on Canvas
            Canvas(modifier = Modifier.size(280.dp)) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val baseR = 100f
                val currentR = baseR * orbScale.value * 2.1f

                // Outer ambient glow ring
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            CalmSkyBlue.copy(alpha = 0.35f),
                            SageGreenLight.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = currentR * 1.3f
                    ),
                    radius = currentR * 1.3f,
                    center = Offset(cx, cy)
                )

                // Middle pulsing ring
                drawCircle(
                    color = SageGreenPrimary.copy(alpha = 0.25f),
                    radius = currentR,
                    center = Offset(cx, cy)
                )

                // Concentric mandala petals
                val petals = 12
                for (i in 0 until petals) {
                    val angle = (i * (2 * PI / petals)).toFloat()
                    val petalR = currentR * 0.75f
                    val px = cx + cos(angle) * petalR
                    val py = cy + sin(angle) * petalR
                    drawCircle(
                        color = CalmSkyBlue.copy(alpha = 0.3f),
                        radius = currentR * 0.2f,
                        center = Offset(px, py)
                    )
                }

                // Core luminous breathing orb
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            CalmSkyBlue,
                            SageGreenPrimary
                        ),
                        center = Offset(cx, cy),
                        radius = currentR * 0.6f
                    ),
                    radius = currentR * 0.6f,
                    center = Offset(cx, cy)
                )
            }

            // Text over orb
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isRunning) currentPhase.name else "Tap Start",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )

                if (isRunning) {
                    Text(
                        text = "${phaseSecondsRemaining}s",
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isRunning) currentPhase.instruction else "Take a mindful pause",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )
            }
        }

        // Stats card (Cycles completed & time)
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Cycles",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$cyclesCompleted",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Time In Calm",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val mins = totalSecondsElapsed / 60
                    val secs = totalSecondsElapsed % 60
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Chime & Haptic toggle buttons
                Row {
                    IconButton(
                        onClick = {
                            viewModel.haptic.gentlePulse()
                            soundChimesEnabled = !soundChimesEnabled
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Toggle Chimes",
                            tint = if (soundChimesEnabled) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f)
                        )
                    }

                    IconButton(
                        onClick = {
                            viewModel.haptic.gentlePulse()
                            hapticsEnabled = !hapticsEnabled
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Toggle Haptics",
                            tint = if (hapticsEnabled) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Big Primary Start/Pause Button
        Button(
            onClick = {
                viewModel.haptic.gentlePulse()
                isRunning = !isRunning
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("breath_start_pause_button"),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRunning) MaterialTheme.colorScheme.secondary else SageGreenPrimary
            )
        ) {
            Icon(
                imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isRunning) "Pause" else "Start"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isRunning) "Pause Breathing" else "Begin ${selectedTechnique.title}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
