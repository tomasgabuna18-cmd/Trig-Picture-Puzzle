package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.RatioStats
import com.example.model.TrigRatio

@Composable
fun SixRatioTracker(
    ratioStats: Map<TrigRatio, RatioStats>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TRIGONOMETRIC RATIOS",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 14.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Live Mastery",
                    color = Color(0xFF38BDF8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2 rows of 3 ratios for clean responsive grid
            val ratios = TrigRatio.entries
            val row1 = ratios.take(3)
            val row2 = ratios.drop(3)

            RatioRow(ratios = row1, statsMap = ratioStats)
            Spacer(modifier = Modifier.height(10.dp))
            RatioRow(ratios = row2, statsMap = ratioStats)
        }
    }
}

@Composable
private fun RatioRow(
    ratios: List<TrigRatio>,
    statsMap: Map<TrigRatio, RatioStats>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ratios.forEach { ratio ->
            val stat = statsMap[ratio] ?: RatioStats(ratio)
            val isPracticed = stat.attempted > 0
            val isMastered = isPracticed && stat.accuracyPercentage >= 75
            val color = getRatioColor(ratio)

            val animatedProgress by animateFloatAsState(
                targetValue = if (stat.attempted > 0) (stat.accuracyPercentage / 100f) else 0f,
                label = "ratio_progress"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(Color(0xFF1E293B), RoundedCornerShape(10.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Checkmark indicator (✓ or ○)
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(if (isMastered) Color(0xFF10B981) else Color(0xFF334155)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isMastered) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Mastered",
                                    tint = Color.White,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }

                        Text(
                            text = if (isPracticed) "${stat.accuracyPercentage}%" else "--",
                            fontWeight = FontWeight.Bold,
                            color = if (isPracticed) color else Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = ratio.displayName,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = color,
                        trackColor = Color(0xFF334155)
                    )
                }
            }
        }
    }
}
