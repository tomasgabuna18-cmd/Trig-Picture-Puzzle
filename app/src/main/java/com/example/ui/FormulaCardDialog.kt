package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.TriangleOrientation
import com.example.model.TriangleParams
import com.example.trig.TriangleDiagram

@Composable
fun FormulaCardDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(24.dp))
                .border(2.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
            color = Color(0xFF0F172A),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.MenuBook,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Trigonometry Reference",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "The Six Trigonometric Ratios & SOH CAH TOA",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_formula_card_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Standard Reference Triangle Diagram
                TriangleDiagram(
                    params = TriangleParams(
                        opposite = 6.0,
                        adjacent = 8.0,
                        hypotenuse = 10.0,
                        thetaDegrees = 36.87,
                        orientation = TriangleOrientation.BOTTOM_RIGHT_ANGLE
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 1: Primary Ratios (SOH CAH TOA)
                Text(
                    text = "PRIMARY RATIOS (SOH CAH TOA)",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF38BDF8),
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                RatioFormulaCard(
                    mnemonic = "SOH",
                    ratioName = "SINE",
                    formula = "sin θ = Opposite / Hypotenuse",
                    color = Color(0xFF38BDF8)
                )

                Spacer(modifier = Modifier.height(8.dp))

                RatioFormulaCard(
                    mnemonic = "CAH",
                    ratioName = "COSINE",
                    formula = "cos θ = Adjacent / Hypotenuse",
                    color = Color(0xFF818CF8)
                )

                Spacer(modifier = Modifier.height(8.dp))

                RatioFormulaCard(
                    mnemonic = "TOA",
                    ratioName = "TANGENT",
                    formula = "tan θ = Opposite / Adjacent",
                    color = Color(0xFF34D399)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: Reciprocal Ratios
                Text(
                    text = "RECIPROCAL RATIOS",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFF59E0B),
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                RatioFormulaCard(
                    mnemonic = "1 / sin θ",
                    ratioName = "COSECANT",
                    formula = "csc θ = Hypotenuse / Opposite = 1 / sin θ",
                    color = Color(0xFFFBBF24)
                )

                Spacer(modifier = Modifier.height(8.dp))

                RatioFormulaCard(
                    mnemonic = "1 / cos θ",
                    ratioName = "SECANT",
                    formula = "sec θ = Hypotenuse / Adjacent = 1 / cos θ",
                    color = Color(0xFFFB923C)
                )

                Spacer(modifier = Modifier.height(8.dp))

                RatioFormulaCard(
                    mnemonic = "1 / tan θ",
                    ratioName = "COTANGENT",
                    formula = "cot θ = Adjacent / Opposite = 1 / tan θ",
                    color = Color(0xFFA78BFA)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section 3: Pythagorean & Quotient Identities
                Text(
                    text = "KEY IDENTITIES",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE2E8F0),
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Pythagorean Identity:",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "sin² θ + cos² θ = 1    |    Opposite² + Adjacent² = Hypotenuse²",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 8.dp),
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = "Quotient Identities:",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "tan θ = sin θ / cos θ    |    cot θ = cos θ / sin θ",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RatioFormulaCard(
    mnemonic: String,
    ratioName: String,
    formula: String,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ratioName,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = formula,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
            Surface(
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = mnemonic,
                    color = color,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
