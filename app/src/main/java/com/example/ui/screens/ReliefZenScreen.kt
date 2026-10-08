package com.example.ui.screens

import android.view.MotionEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Water
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CalmSkyBlue
import com.example.ui.theme.DarkSurfaceZen
import com.example.ui.theme.LavenderCalm
import com.example.ui.theme.SageGreenDark
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SageGreenPrimary
import com.example.ui.theme.WarmSand
import com.example.ui.theme.WarmSandLight
import com.example.ui.theme.ZenGold
import com.example.ui.theme.ZenLotusPink
import com.example.viewmodel.SerenityViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class ZenToyType(val title: String, val subtitle: String) {
    BUBBLE_POP("Bubble Wrap", "Tactile popping satisfying stress relief"),
    SQUISHY_BALL("Squishy Ball", "Elastic jelly stress ball to squeeze & stretch"),
    WATER_RIPPLES("Water Pond", "Calming ripples & floating lotus petals"),
    SINGING_BOWL("Singing Bowl", "Tibetan 432Hz resonant chime & vibrations"),
    STONE_CAIRN("Zen Stones", "Mindful river stone balancing & stacking"),
    SAND_DRAWING("Zen Sand Garden", "Smooth finger rake mandala trails")
}

@Composable
fun ReliefZenScreen(
    viewModel: SerenityViewModel,
    modifier: Modifier = Modifier
) {
    var selectedToy by remember { mutableStateOf(ZenToyType.BUBBLE_POP) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Tab selector
        ScrollableTabRow(
            selectedTabIndex = selectedToy.ordinal,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedToy.ordinal]),
                    color = MaterialTheme.colorScheme.primary,
                    height = 3.dp
                )
            },
            divider = {}
        ) {
            ZenToyType.values().forEach { toy ->
                Tab(
                    selected = selectedToy == toy,
                    onClick = {
                        viewModel.haptic.gentlePulse()
                        selectedToy = toy
                    },
                    text = {
                        Text(
                            text = toy.title,
                            fontWeight = if (selectedToy == toy) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.testTag("zen_tab_${toy.name.lowercase()}")
                )
            }
        }

        // Toy Subtitle banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = selectedToy.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Active Interactive Toy View
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedToy) {
                ZenToyType.BUBBLE_POP -> BubblePopToy(viewModel)
                ZenToyType.SQUISHY_BALL -> SquishyBallToy(viewModel)
                ZenToyType.WATER_RIPPLES -> WaterRipplesToy(viewModel)
                ZenToyType.SINGING_BOWL -> SingingBowlToy(viewModel)
                ZenToyType.STONE_CAIRN -> StoneCairnToy(viewModel)
                ZenToyType.SAND_DRAWING -> SandDrawingToy(viewModel)
            }
        }
    }
}

// -------------------------------------------------------------
// 1. Bubble Wrap Toy
// -------------------------------------------------------------
@Composable
fun BubblePopToy(viewModel: SerenityViewModel) {
    val totalBubbles = 30
    val poppedState = remember { mutableStateListOf(*Array(totalBubbles) { false }) }
    val poppedCount = poppedState.count { it }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Popped: $poppedCount / $totalBubbles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            OutlinedButton(
                onClick = {
                    viewModel.haptic.gentlePulse()
                    for (i in 0 until totalBubbles) {
                        poppedState[i] = false
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.testTag("bubble_reset_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset sheet",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("New Sheet", fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(4.dp)
            ) {
                itemsIndexed(poppedState) { index, isPopped ->
                    BubbleItem(
                        isPopped = isPopped,
                        onPop = {
                            if (!isPopped) {
                                poppedState[index] = true
                                // Varied pitch between 0.85 and 1.25 for acoustic realism
                                val pitch = 0.85f + (index % 6) * 0.08f
                                viewModel.onBubblePopped(pitch)
                            }
                        },
                        tagIndex = index
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tap bubbles to release tension with crisp tactile pops",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun BubbleItem(
    isPopped: Boolean,
    onPop: () -> Unit,
    tagIndex: Int
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .shadow(
                elevation = if (isPopped) 0.dp else 4.dp,
                shape = CircleShape
            )
            .clip(CircleShape)
            .background(
                brush = if (isPopped) {
                    Brush.radialGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                        )
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(
                            CalmSkyBlue.copy(alpha = 0.35f),
                            SageGreenPrimary.copy(alpha = 0.25f),
                            SageGreenPrimary.copy(alpha = 0.5f)
                        )
                    )
                }
            )
            .border(
                width = if (isPopped) 1.dp else 2.dp,
                color = if (isPopped) {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                } else {
                    CalmSkyBlue.copy(alpha = 0.8f)
                },
                shape = CircleShape
            )
            .clickable { onPop() }
            .testTag("bubble_$tagIndex"),
        contentAlignment = Alignment.Center
    ) {
        if (isPopped) {
            // Popped indented dimple
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        shape = CircleShape
                    )
            )
        } else {
            // Shiny highlight glint
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .offset(x = (-8).dp, y = (-8).dp)
                    .background(Color.White.copy(alpha = 0.65f), CircleShape)
            )
        }
    }
}

// -------------------------------------------------------------
// 2. Zen Water Ripples Toy
// -------------------------------------------------------------
data class WaterRipple(
    val id: Long,
    val center: Offset,
    var radius: Float,
    var alpha: Float,
    val maxRadius: Float
)

@Composable
fun WaterRipplesToy(viewModel: SerenityViewModel) {
    val ripples = remember { mutableStateListOf<WaterRipple>() }
    var nextRippleId by remember { mutableStateOf(0L) }

    val infiniteTransition = rememberInfiniteTransition(label = "lotusDrift")
    val lotusDriftX by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lotusX"
    )
    val lotusDriftY by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lotusY"
    )

    // Ripple expansion engine
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(16L) // ~60 FPS
            if (ripples.isNotEmpty()) {
                val iterator = ripples.iterator()
                while (iterator.hasNext()) {
                    val r = iterator.next()
                    r.radius += 5.5f
                    r.alpha = (1f - (r.radius / r.maxRadius)).coerceIn(0f, 1f)
                    if (r.radius >= r.maxRadius || r.alpha <= 0.01f) {
                        iterator.remove()
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    ripples.add(
                        WaterRipple(
                            id = nextRippleId++,
                            center = offset,
                            radius = 10f,
                            alpha = 0.9f,
                            maxRadius = 380f
                        )
                    )
                    viewModel.onWaterRippled()
                }
            }
            .testTag("water_ripples_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Calming water gradient background
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F2B28),
                        Color(0xFF133E3A),
                        Color(0xFF1A4D48)
                    )
                )
            )

            // Draw calm water concentric ripples
            for (r in ripples) {
                drawCircle(
                    color = Color(0xFFA5E6DB).copy(alpha = r.alpha * 0.7f),
                    radius = r.radius,
                    center = r.center,
                    style = Stroke(width = 3.5f)
                )
                // Secondary subtle sub-wave
                if (r.radius > 30f) {
                    drawCircle(
                        color = Color(0xFFC7F9EE).copy(alpha = r.alpha * 0.4f),
                        radius = r.radius - 22f,
                        center = r.center,
                        style = Stroke(width = 2.0f)
                    )
                }
            }

            // Draw Floating Lotus in Center
            val lotusCenter = Offset(
                x = canvasWidth / 2f + lotusDriftX,
                y = canvasHeight / 2f + lotusDriftY
            )

            // Lotus Pad
            drawCircle(
                color = Color(0xFF2C6353),
                radius = 70f,
                center = lotusCenter
            )
            drawCircle(
                color = Color(0xFF1C473B),
                radius = 65f,
                center = lotusCenter,
                style = Stroke(width = 3f)
            )

            // Lotus Petals (Golden-Pink)
            val numPetals = 8
            for (i in 0 until numPetals) {
                val angle = (i * (2 * PI / numPetals)).toFloat()
                val petalDist = 38f
                val petalCenter = Offset(
                    x = lotusCenter.x + cos(angle) * petalDist,
                    y = lotusCenter.y + sin(angle) * petalDist
                )
                drawCircle(
                    color = Color(0xFFE89BA7).copy(alpha = 0.85f),
                    radius = 22f,
                    center = petalCenter
                )
            }
            // Inner golden flower center
            drawCircle(
                color = Color(0xFFFCD34D),
                radius = 16f,
                center = lotusCenter
            )
        }

        Text(
            text = "Touch anywhere on the pond to create soothing ripples",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFFBAECE2),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp)
                .background(Color(0x88000000), RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

// -------------------------------------------------------------
// 3. Tibetan Singing Bowl Toy
// -------------------------------------------------------------
@Composable
fun SingingBowlToy(viewModel: SerenityViewModel) {
    var bowlAuraRadius by remember { mutableFloatStateOf(0f) }
    var isRinging by remember { mutableStateOf(false) }

    val infiniteAura = rememberInfiniteTransition(label = "auraPulse")
    val auraScale by infiniteAura.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "auraScale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Tibetan Singing Bowl (432 Hz)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tap to strike bowl • Rub rim in circles to sing",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .size(280.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            isRinging = true
                            viewModel.onSingingBowlStruck()
                        }
                    )
                }
                .pointerInput(Unit) {
                    var lastAngle = 0.0
                    detectDragGestures(
                        onDragStart = { offset ->
                            val cx = size.width / 2.0
                            val cy = size.height / 2.0
                            lastAngle = atan2(offset.y - cy, offset.x - cx)
                        },
                        onDrag = { change, _ ->
                            val cx = size.width / 2.0
                            val cy = size.height / 2.0
                            val newAngle = atan2(change.position.y - cy, change.position.x - cx)
                            val diff = Math.abs(newAngle - lastAngle)
                            if (diff > 0.15) {
                                isRinging = true
                                viewModel.onSingingBowlResonated()
                                lastAngle = newAngle
                            }
                        }
                    )
                }
                .testTag("singing_bowl_interactive"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val center = Offset(cx, cy)
                val baseRadius = 110f

                // Resonant golden aura
                if (isRinging) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ZenGold.copy(alpha = 0.45f),
                                ZenGold.copy(alpha = 0.1f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = baseRadius * 1.55f * auraScale
                        ),
                        radius = baseRadius * 1.55f * auraScale,
                        center = center
                    )
                }

                // Outer Bronze Lip
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE2C17D),
                            Color(0xFFB08C44),
                            Color(0xFF755519)
                        ),
                        center = center,
                        radius = baseRadius
                    ),
                    radius = baseRadius,
                    center = center
                )

                // Bowl Inner Depth
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF422D0A),
                            Color(0xFF6B4D18),
                            Color(0xFF9E7C33)
                        ),
                        center = center,
                        radius = baseRadius - 16f
                    ),
                    radius = baseRadius - 16f,
                    center = center
                )

                // Concentric Tibetan Inscribed Rings
                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = 0.65f),
                    radius = baseRadius - 38f,
                    center = center,
                    style = Stroke(width = 2.5f)
                )

                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = 0.45f),
                    radius = baseRadius - 62f,
                    center = center,
                    style = Stroke(width = 1.8f)
                )

                // Central Sacred Om Motif center
                drawCircle(
                    color = Color(0xFFE8C87A),
                    radius = 18f,
                    center = center
                )
            }
        }

        OutlinedButton(
            onClick = {
                isRinging = true
                viewModel.onSingingBowlStruck()
            },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .height(48.dp)
                .testTag("strike_bowl_button")
        ) {
            Icon(Icons.Default.Spa, contentDescription = "Strike")
            Spacer(modifier = Modifier.width(8.dp))
            Text("Strike Bowl (Bell Chime)", fontWeight = FontWeight.Bold)
        }
    }
}

// -------------------------------------------------------------
// 4. Sand Mandala Drawing Toy
// -------------------------------------------------------------
data class SandStroke(
    val path: List<Offset>,
    val color: Color,
    val strokeWidth: Float
)

@Composable
fun SandDrawingToy(viewModel: SerenityViewModel) {
    val strokes = remember { mutableStateListOf<SandStroke>() }
    var currentPath = remember { mutableStateListOf<Offset>() }
    var selectedColor by remember { mutableStateOf(WarmSandLight) }

    val palette = listOf(
        WarmSandLight,
        CalmSkyBlue,
        SageGreenLight,
        ZenLotusPink,
        LavenderCalm
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Controls: Palette & Clear
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                palette.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(color, CircleShape)
                            .border(
                                width = if (selectedColor == color) 3.dp else 1.dp,
                                color = if (selectedColor == color) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                            .clickable {
                                viewModel.haptic.gentlePulse()
                                selectedColor = color
                            }
                    )
                }
            }

            IconButton(
                onClick = {
                    viewModel.haptic.gentlePulse()
                    strokes.clear()
                    currentPath.clear()
                },
                modifier = Modifier.testTag("sand_clear_button")
            ) {
                Icon(
                    imageVector = Icons.Default.CleaningServices,
                    contentDescription = "Smooth Sand",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFFEADBCE) // Warm Zen Sand color
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                currentPath.clear()
                                currentPath.add(offset)
                                viewModel.haptic.gentlePulse()
                            },
                            onDrag = { change, _ ->
                                currentPath.add(change.position)
                            },
                            onDragEnd = {
                                if (currentPath.isNotEmpty()) {
                                    strokes.add(
                                        SandStroke(
                                            path = currentPath.toList(),
                                            color = selectedColor,
                                            strokeWidth = 14f
                                        )
                                    )
                                    currentPath.clear()
                                }
                            }
                        )
                    }
                    .testTag("sand_drawing_canvas")
            ) {
                // Background subtle rake sand grain texture lines
                val w = size.width
                val h = size.height
                for (y in 0..h.toInt() step 28) {
                    drawLine(
                        color = Color(0xFFDCC8B7).copy(alpha = 0.35f),
                        start = Offset(0f, y.toFloat()),
                        end = Offset(w, y.toFloat()),
                        strokeWidth = 1.2f
                    )
                }

                // Render saved strokes
                for (stroke in strokes) {
                    if (stroke.path.size > 1) {
                        val path = Path().apply {
                            moveTo(stroke.path.first().x, stroke.path.first().y)
                            for (pt in stroke.path.drop(1)) {
                                lineTo(pt.x, pt.y)
                            }
                        }
                        // Sand groove shadow
                        drawPath(
                            path = path,
                            color = Color(0xFF8A6B52).copy(alpha = 0.45f),
                            style = Stroke(
                                width = stroke.strokeWidth + 4f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                        // Glowing rake stroke
                        drawPath(
                            path = path,
                            color = stroke.color,
                            style = Stroke(
                                width = stroke.strokeWidth,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                // Render active drawing stroke
                if (currentPath.size > 1) {
                    val activePath = Path().apply {
                        moveTo(currentPath.first().x, currentPath.first().y)
                        for (pt in currentPath.drop(1)) {
                            lineTo(pt.x, pt.y)
                        }
                    }
                    drawPath(
                        path = activePath,
                        color = selectedColor,
                        style = Stroke(width = 14f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Trace patterns in the zen sand with your finger to calm the mind",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// -------------------------------------------------------------
// 5. Squishy Elastic Stress Ball
// -------------------------------------------------------------
@Composable
fun SquishyBallToy(viewModel: SerenityViewModel) {
    val ballColors = listOf(
        SageGreenPrimary to "Sage Green",
        CalmSkyBlue to "Ocean Blue",
        ZenLotusPink to "Sunset Rose",
        WarmSand to "Golden Amber"
    )
    var selectedBallColor by remember { mutableStateOf(ballColors.first().first) }

    val animX = remember { Animatable(0f) }
    val animY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    var squeezeCount by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Elastic Jelly Stress Ball",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Squeeze, stretch, and drag to release physical tension",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Color Selector
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ballColors.forEach { (color, _) ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(color, CircleShape)
                            .border(
                                width = if (selectedBallColor == color) 3.dp else 1.dp,
                                color = if (selectedBallColor == color) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                            .clickable {
                                viewModel.haptic.gentlePulse()
                                selectedBallColor = color
                            }
                    )
                }
            }
        }

        // Interactive Ball Canvas
        Box(
            modifier = Modifier
                .size(300.dp)
                .pointerInput(Unit) {
                    var lastDist = 0f
                    detectDragGestures(
                        onDragStart = {
                            viewModel.onSquish()
                            squeezeCount++
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                val newX = (animX.value + dragAmount.x).coerceIn(-130f, 130f)
                                val newY = (animY.value + dragAmount.y).coerceIn(-130f, 130f)
                                animX.snapTo(newX)
                                animY.snapTo(newY)

                                val currentDist = sqrt(newX * newX + newY * newY)
                                if (Math.abs(currentDist - lastDist) > 35f) {
                                    viewModel.onSquish()
                                    lastDist = currentDist
                                }
                            }
                        },
                        onDragEnd = {
                            viewModel.onSquish()
                            scope.launch {
                                animX.animateTo(
                                    0f,
                                    spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                            scope.launch {
                                animY.animateTo(
                                    0f,
                                    spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        }
                    )
                }
                .testTag("squishy_ball_interactive"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height / 2f

                val dx = animX.value
                val dy = animY.value
                val dist = sqrt(dx * dx + dy * dy)

                val baseRadius = 85f
                // Volume-preserving elastic deformation
                val stretchFactor = 1f + (dist / 140f) * 0.45f
                val squashFactor = 1f / (stretchFactor * 0.85f)

                val ballCenter = Offset(cx + dx * 0.7f, cy + dy * 0.7f)

                // Soft shadow on surface below
                drawCircle(
                    color = Color.Black.copy(alpha = 0.18f),
                    radius = baseRadius * 1.05f,
                    center = Offset(cx, cy + baseRadius + 20f)
                )

                // Ball Ambient Outer Glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            selectedBallColor.copy(alpha = 0.35f),
                            Color.Transparent
                        ),
                        center = ballCenter,
                        radius = baseRadius * 1.4f * stretchFactor
                    ),
                    radius = baseRadius * 1.4f * stretchFactor,
                    center = ballCenter
                )

                // Main Elastic Jelly Sphere
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.45f),
                            selectedBallColor,
                            selectedBallColor.copy(alpha = 0.85f)
                        ),
                        center = Offset(ballCenter.x - 22f, ballCenter.y - 22f),
                        radius = baseRadius * stretchFactor
                    ),
                    radius = baseRadius * stretchFactor,
                    center = ballCenter
                )

                // 3D Specular Glint Highlight
                drawCircle(
                    color = Color.White.copy(alpha = 0.65f),
                    radius = 16f,
                    center = Offset(ballCenter.x - baseRadius * 0.38f, ballCenter.y - baseRadius * 0.38f)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Squeezes: $squeezeCount",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Release to watch the springy jelly snap back with calm",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// -------------------------------------------------------------
// 6. Zen Pebble Cairn Stacking Toy
// -------------------------------------------------------------
data class CairnStone(
    val id: Int,
    val widthDp: Float,
    val heightDp: Float,
    val color: Color,
    val name: String
)

@Composable
fun StoneCairnToy(viewModel: SerenityViewModel) {
    val availableStones = remember {
        listOf(
            CairnStone(1, 150f, 40f, Color(0xFF4A5568), "River Granite"),
            CairnStone(2, 130f, 36f, Color(0xFF5D6D7E), "Slate Pebble"),
            CairnStone(3, 110f, 34f, Color(0xFF717D7E), "Mossy Basalt"),
            CairnStone(4, 90f, 30f, Color(0xFF85929E), "Stream Quartz"),
            CairnStone(5, 75f, 28f, Color(0xFFAEB6BF), "Alpine Marble"),
            CairnStone(6, 60f, 24f, Color(0xFFD5D8DC), "White Jade")
        )
    }

    val stackedStones = remember { mutableStateListOf<CairnStone>() }

    // Initialize with base stone if empty
    LaunchedEffect(Unit) {
        if (stackedStones.isEmpty()) {
            stackedStones.add(availableStones.first())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Balanced Cairn: ${stackedStones.size} Stones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "In stillness, find balance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            OutlinedButton(
                onClick = {
                    viewModel.onStoneClink()
                    stackedStones.clear()
                    stackedStones.add(availableStones.first())
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("cairn_reset_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset Stack", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Rebalance", fontSize = 12.sp)
            }
        }

        // Cairn Tower Canvas / Layout
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy((-6).dp) // overlapping stones
            ) {
                stackedStones.reversed().forEach { stone ->
                    Box(
                        modifier = Modifier
                            .width(stone.widthDp.dp)
                            .height(stone.heightDp.dp)
                            .shadow(3.dp, RoundedCornerShape(50))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        stone.color.copy(alpha = 0.9f),
                                        stone.color,
                                        Color(0xFF2C3E50).copy(alpha = 0.6f)
                                    )
                                ),
                                shape = RoundedCornerShape(50)
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(50))
                    )
                }

                // River Bed Ground
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(18.dp)
                        .background(Color(0xFF2B3A42), RoundedCornerShape(10.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Next stone action button
        if (stackedStones.size < availableStones.size) {
            val nextStone = availableStones[stackedStones.size]
            Button(
                onClick = {
                    viewModel.onStoneClink()
                    stackedStones.add(nextStone)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("cairn_add_stone_button"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary)
            ) {
                Icon(Icons.Default.Spa, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Balance Next Stone (${nextStone.name})", fontWeight = FontWeight.Bold)
            }
        } else {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SageGreenPrimary.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🪨 Cairn Complete • Harmonious Equilibrium",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SageGreenPrimary
                    )
                    Text(
                        text = "All stones balanced in tranquility. Take a moment to enjoy the stillness.",
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

