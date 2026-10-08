package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.CalmSkyBlue
import com.example.ui.theme.SageGreenPrimary
import com.example.ui.theme.WarmSand

data class GroundingStep(
    val count: Int,
    val title: String,
    val instruction: String,
    val icon: ImageVector,
    val prompt: String
)

@Composable
fun QuickSosDialog(
    onDismiss: () -> Unit,
    onStepAdvanced: () -> Unit = {}
) {
    val steps = remember {
        listOf(
            GroundingStep(
                count = 5,
                title = "See",
                instruction = "Acknowledge 5 things you see around you",
                icon = Icons.Default.Visibility,
                prompt = "Notice the colors, shadows, textures, or shapes in your immediate room."
            ),
            GroundingStep(
                count = 4,
                title = "Touch",
                instruction = "Acknowledge 4 things you can physically feel",
                icon = Icons.Default.PanTool,
                prompt = "Feel the fabric of your clothing, the floor beneath your feet, or your hands touching."
            ),
            GroundingStep(
                count = 3,
                title = "Hear",
                instruction = "Acknowledge 3 things you can hear",
                icon = Icons.Default.Hearing,
                prompt = "Listen for distant traffic, a ticking clock, birds outside, or your own breath."
            ),
            GroundingStep(
                count = 2,
                title = "Smell",
                instruction = "Acknowledge 2 things you can smell",
                icon = Icons.Default.WaterDrop,
                prompt = "Notice fresh air, morning coffee, clean laundry, or take a gentle slow inhale."
            ),
            GroundingStep(
                count = 1,
                title = "Taste",
                instruction = "Acknowledge 1 thing you can taste",
                icon = Icons.Default.Restaurant,
                prompt = "Notice the taste in your mouth, a sip of cool water, or simply swallow gently."
            )
        )
    }

    var currentStepIndex by remember { mutableIntStateOf(0) }
    val isComplete = currentStepIndex >= steps.size

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("sos_grounding_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "5-4-3-2-1 Grounding",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Return to the safe present moment",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("sos_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close grounding"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    progress = {
                        if (isComplete) 1f else (currentStepIndex / steps.size.toFloat())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = SageGreenPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                AnimatedContent(
                    targetState = if (isComplete) -1 else currentStepIndex,
                    label = "GroundingStepTransition"
                ) { index ->
                    if (index == -1) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(vertical = 12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(SageGreenPrimary.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Complete",
                                    tint = SageGreenPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "You are grounded and safe",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your nervous system has reconnected with your surroundings. Take one slow, peaceful breath.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        val step = steps[index]
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(
                                        when (step.count) {
                                            5 -> SageGreenPrimary.copy(alpha = 0.15f)
                                            4 -> CalmSkyBlue.copy(alpha = 0.2f)
                                            3 -> WarmSand.copy(alpha = 0.2f)
                                            else -> MaterialTheme.colorScheme.primaryContainer
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${step.count}",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = step.icon,
                                        contentDescription = step.title,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = step.instruction,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = step.prompt,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (isComplete) {
                            onDismiss()
                        } else {
                            onStepAdvanced()
                            currentStepIndex++
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("sos_advance_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SageGreenPrimary
                    )
                ) {
                    Text(
                        text = if (isComplete) "Finish & Return Calm" else "Found it, next step →",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
