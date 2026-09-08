package com.example.trig

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.example.model.TriangleOrientation
import com.example.model.TriangleParams
import com.example.model.TriangleSide
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws a clear, high-contrast, mathematically accurate right-angled triangle.
 * Labeled with Opposite, Adjacent, Hypotenuse, and reference angle theta.
 * Designed to fit comfortably and completely on compact phone screens (e.g. Infinix Hot 40i).
 */
@Composable
fun TriangleDiagram(
    params: TriangleParams,
    modifier: Modifier = Modifier,
    highlightSide: TriangleSide? = null
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 6.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.9f)
        ) {
            drawTriangleDiagram(params, highlightSide)
        }
    }
}

private fun DrawScope.drawTriangleDiagram(
    params: TriangleParams,
    highlightSide: TriangleSide?
) {
    // Generous padding to prevent labels and vertices from clipping the container bounds
    val paddingX = size.width * 0.16f
    val paddingY = size.height * 0.18f
    val w = size.width - paddingX * 2
    val h = size.height - paddingY * 2

    // Right angle coordinates based on orientation
    // Standard BOTTOM_RIGHT:
    // A (Theta vertex): Bottom-Left (paddingX, paddingY + h)
    // B (Right-angle vertex): Bottom-Right (paddingX + w, paddingY + h)
    // C (Top vertex): Top-Right (paddingX + w, paddingY)
    val vertexA: Offset
    val vertexB: Offset // Right angle
    val vertexC: Offset

    when (params.orientation) {
        TriangleOrientation.BOTTOM_RIGHT_ANGLE -> {
            vertexA = Offset(paddingX, paddingY + h)
            vertexB = Offset(paddingX + w, paddingY + h)
            vertexC = Offset(paddingX + w, paddingY)
        }
        TriangleOrientation.BOTTOM_LEFT_ANGLE -> {
            vertexA = Offset(paddingX + w, paddingY + h)
            vertexB = Offset(paddingX, paddingY + h)
            vertexC = Offset(paddingX, paddingY)
        }
        TriangleOrientation.TOP_RIGHT_ANGLE -> {
            vertexA = Offset(paddingX, paddingY)
            vertexB = Offset(paddingX + w, paddingY)
            vertexC = Offset(paddingX + w, paddingY + h)
        }
    }

    // 1. Fill Triangle with subtle accent tint
    val triPath = Path().apply {
        moveTo(vertexA.x, vertexA.y)
        lineTo(vertexB.x, vertexB.y)
        lineTo(vertexC.x, vertexC.y)
        close()
    }
    drawPath(
        path = triPath,
        color = Color(0x2238BDF8)
    )

    // 2. Draw Sides with bold lines
    val baseLineColor = Color(0xFF94A3B8)
    val strokeWidth = 4.5f

    // Side A -> B (Adjacent to Theta)
    drawLine(
        color = if (highlightSide == TriangleSide.ADJACENT) Color(0xFF38BDF8) else baseLineColor,
        start = vertexA,
        end = vertexB,
        strokeWidth = if (highlightSide == TriangleSide.ADJACENT) strokeWidth * 1.5f else strokeWidth,
        cap = StrokeCap.Round
    )

    // Side B -> C (Opposite to Theta)
    drawLine(
        color = if (highlightSide == TriangleSide.OPPOSITE) Color(0xFFF43F5E) else baseLineColor,
        start = vertexB,
        end = vertexC,
        strokeWidth = if (highlightSide == TriangleSide.OPPOSITE) strokeWidth * 1.5f else strokeWidth,
        cap = StrokeCap.Round
    )

    // Side A -> C (Hypotenuse)
    drawLine(
        color = if (highlightSide == TriangleSide.HYPOTENUSE) Color(0xFF10B981) else Color(0xFFF59E0B),
        start = vertexA,
        end = vertexC,
        strokeWidth = if (highlightSide == TriangleSide.HYPOTENUSE) strokeWidth * 1.5f else strokeWidth,
        cap = StrokeCap.Round
    )

    // 3. Right Angle Square Marker at vertex B
    val sq = 18f
    val dirAB = (vertexA - vertexB) / (vertexA - vertexB).getDistance()
    val dirCB = (vertexC - vertexB) / (vertexC - vertexB).getDistance()
    val r1 = vertexB + dirAB * sq
    val r2 = r1 + dirCB * sq
    val r3 = vertexB + dirCB * sq

    val squarePath = Path().apply {
        moveTo(r1.x, r1.y)
        lineTo(r2.x, r2.y)
        lineTo(r3.x, r3.y)
    }
    drawPath(
        path = squarePath,
        color = Color(0xFF64748B),
        style = Stroke(width = 2.5f, cap = StrokeCap.Square, join = StrokeJoin.Miter)
    )

    // 4. Angle θ Arc at vertex A
    val arcRadius = 26f
    val sweepAngle = 26f
    val startAngle = when (params.orientation) {
        TriangleOrientation.BOTTOM_RIGHT_ANGLE -> -sweepAngle
        TriangleOrientation.BOTTOM_LEFT_ANGLE -> 180f
        TriangleOrientation.TOP_RIGHT_ANGLE -> 0f
    }
    drawArc(
        color = Color(0xFFF59E0B),
        startAngle = startAngle,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(vertexA.x - arcRadius, vertexA.y - arcRadius),
        size = Size(arcRadius * 2, arcRadius * 2),
        style = Stroke(width = 2.5f)
    )

    // 5. Draw Text Labels
    val paint = android.graphics.Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 28f
        isAntiAlias = true
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    val paintTheta = android.graphics.Paint().apply {
        color = android.graphics.Color.parseColor("#F59E0B")
        textSize = 30f
        isAntiAlias = true
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    val paintUnknown = android.graphics.Paint().apply {
        color = android.graphics.Color.parseColor("#EF4444")
        textSize = 30f
        isAntiAlias = true
        typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    drawContext.canvas.nativeCanvas.apply {
        // Theta label near vertex A (inside the acute angle)
        val thetaOffset = when (params.orientation) {
            TriangleOrientation.BOTTOM_RIGHT_ANGLE -> Offset(vertexA.x + 36f, vertexA.y - 12f)
            TriangleOrientation.BOTTOM_LEFT_ANGLE -> Offset(vertexA.x - 48f, vertexA.y - 12f)
            TriangleOrientation.TOP_RIGHT_ANGLE -> Offset(vertexA.x + 36f, vertexA.y + 30f)
        }
        drawText("θ", thetaOffset.x, thetaOffset.y, paintTheta)

        // Adjacent text: halfway along A -> B
        val adjText = if (params.unknownSide == TriangleSide.ADJACENT) "? (Adj)" else "${formatNum(params.adjacent)} (Adj)"
        val adjMid = (vertexA + vertexB) / 2f
        val adjPaint = if (params.unknownSide == TriangleSide.ADJACENT) paintUnknown else paint
        val adjTextOffset = when (params.orientation) {
            TriangleOrientation.BOTTOM_RIGHT_ANGLE, TriangleOrientation.BOTTOM_LEFT_ANGLE ->
                Offset(adjMid.x - 40f, adjMid.y + 24f)
            TriangleOrientation.TOP_RIGHT_ANGLE ->
                Offset(adjMid.x - 40f, adjMid.y - 12f)
        }
        drawText(adjText, adjTextOffset.x, adjTextOffset.y, adjPaint)

        // Opposite text: halfway along B -> C
        val oppText = if (params.unknownSide == TriangleSide.OPPOSITE) "? (Opp)" else "${formatNum(params.opposite)} (Opp)"
        val oppMid = (vertexB + vertexC) / 2f
        val oppPaint = if (params.unknownSide == TriangleSide.OPPOSITE) paintUnknown else paint
        val oppTextOffset = when (params.orientation) {
            TriangleOrientation.BOTTOM_RIGHT_ANGLE, TriangleOrientation.TOP_RIGHT_ANGLE ->
                Offset(oppMid.x + 8f, oppMid.y + 8f)
            TriangleOrientation.BOTTOM_LEFT_ANGLE ->
                Offset(oppMid.x - 90f, oppMid.y + 8f)
        }
        drawText(oppText, oppTextOffset.x, oppTextOffset.y, oppPaint)

        // Hypotenuse text: halfway along A -> C
        val hypText = if (params.unknownSide == TriangleSide.HYPOTENUSE) "? (Hyp)" else "${formatNum(params.hypotenuse)} (Hyp)"
        val hypMid = (vertexA + vertexC) / 2f
        val hypPaint = if (params.unknownSide == TriangleSide.HYPOTENUSE) paintUnknown else paint
        val hypTextOffset = when (params.orientation) {
            TriangleOrientation.BOTTOM_RIGHT_ANGLE ->
                Offset(hypMid.x - 60f, hypMid.y - 12f)
            TriangleOrientation.BOTTOM_LEFT_ANGLE ->
                Offset(hypMid.x + 12f, hypMid.y - 12f)
            TriangleOrientation.TOP_RIGHT_ANGLE ->
                Offset(hypMid.x - 60f, hypMid.y + 24f)
        }
        drawText(hypText, hypTextOffset.x, hypTextOffset.y, hypPaint)
    }
}

private fun formatNum(v: Double): String {
    return if (v % 1.0 == 0.0) v.toInt().toString() else "%.1f".format(v)
}
