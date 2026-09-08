package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TriangleOrientation
import com.example.model.TriangleParams
import com.example.model.TrigRatio
import com.example.trig.TriangleDiagram

@Composable
fun TopicsReferenceScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF020617))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "The Six Trigonometric Ratios Guide",
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 20.sp
        )
        Text(
            text = "Master the relationship between acute angles and side lengths in right triangles.",
            color = Color(0xFF94A3B8),
            fontSize = 13.sp
        )

        // Standard Triangle Diagram
        TriangleDiagram(
            params = TriangleParams(
                opposite = 3.0,
                adjacent = 4.0,
                hypotenuse = 5.0,
                thetaDegrees = 36.87,
                orientation = TriangleOrientation.BOTTOM_RIGHT_ANGLE
            ),
            modifier = Modifier.fillMaxWidth()
        )

        TrigRatio.entries.forEach { ratio ->
            val color = getRatioColor(ratio)
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = ratio.displayName,
                            fontWeight = FontWeight.Bold,
                            color = color,
                            fontSize = 16.sp
                        )
                        Surface(
                            color = color.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = ratio.abbreviation.uppercase(),
                                color = color,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Formula: ${ratio.formulaText}",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Reciprocal / Relationship: ${ratio.reciprocalOf}",
                        color = Color(0xFFF59E0B),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = getRatioExample(ratio),
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

private fun getRatioExample(ratio: TrigRatio): String {
    return when (ratio) {
        TrigRatio.SINE -> "In a (3, 4, 5) right triangle with θ opposite to side 3: sin θ = 3 / 5 = 0.60."
        TrigRatio.COSINE -> "In a (3, 4, 5) right triangle with θ adjacent to side 4: cos θ = 4 / 5 = 0.80."
        TrigRatio.TANGENT -> "In a (3, 4, 5) right triangle: tan θ = 3 / 4 = 0.75."
        TrigRatio.COSECANT -> "Invert sine: csc θ = 1 / sin θ = 5 / 3 ≈ 1.67."
        TrigRatio.SECANT -> "Invert cosine: sec θ = 1 / cos θ = 5 / 4 = 1.25."
        TrigRatio.COTANGENT -> "Invert tangent: cot θ = 1 / tan θ = 4 / 3 ≈ 1.33."
    }
}
