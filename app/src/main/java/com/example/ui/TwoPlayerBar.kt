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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TwoPlayerBar(
    currentTurn: Int,
    p1Score: Int,
    p2Score: Int,
    p1Pieces: Int,
    p2Pieces: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player 1
            PlayerBadge(
                playerNumber = 1,
                name = "Player 1",
                score = p1Score,
                pieces = p1Pieces,
                isActive = currentTurn == 1,
                activeColor = Color(0xFF38BDF8)
            )

            Text(
                text = "VS",
                fontWeight = FontWeight.Black,
                color = Color(0xFF64748B),
                fontSize = 14.sp
            )

            // Player 2
            PlayerBadge(
                playerNumber = 2,
                name = "Player 2",
                score = p2Score,
                pieces = p2Pieces,
                isActive = currentTurn == 2,
                activeColor = Color(0xFFF43F5E)
            )
        }
    }
}

@Composable
private fun PlayerBadge(
    playerNumber: Int,
    name: String,
    score: Int,
    pieces: Int,
    isActive: Boolean,
    activeColor: Color
) {
    Box(
        modifier = Modifier
            .background(
                if (isActive) activeColor.copy(alpha = 0.2f) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = if (isActive) activeColor else Color(0xFF334155),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isActive) "★ $name's Turn" else name,
                fontWeight = FontWeight.Bold,
                color = if (isActive) activeColor else Color(0xFF94A3B8),
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$score pts",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "($pieces pcs)",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
            }
        }
    }
}
