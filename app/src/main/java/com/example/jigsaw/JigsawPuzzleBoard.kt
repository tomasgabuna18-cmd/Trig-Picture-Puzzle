package com.example.jigsaw

import android.graphics.BitmapFactory
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.model.PuzzlePiece
import kotlin.math.min

@Composable
fun JigsawPuzzleBoard(
    pieces: List<PuzzlePiece>,
    gridSize: Int,
    imageBitmap: ImageBitmap?,
    selectedPieceId: Int?,
    newlyRevealedPieceId: Int?,
    onPieceClicked: (PuzzlePiece) -> Unit,
    modifier: Modifier = Modifier
) {
    // Selection pulse animation
    val pulseAnim = remember { Animatable(1f) }
    LaunchedEffect(selectedPieceId) {
        if (selectedPieceId != null) {
            pulseAnim.animateTo(
                targetValue = 1.08f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        } else {
            pulseAnim.snapTo(1f)
        }
    }

    // Generator instance cached for this puzzle grid
    val gridGenerator = remember(gridSize) {
        JigsawGridGenerator(gridSize)
    }

    // Cache computed paths for the current canvas size
    var cachedPaths by remember { mutableStateOf<Map<Int, Path>>(emptyMap()) }
    var cachedBoardSize by remember { mutableStateOf(Size.Zero) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        val squareDimension = min(maxWidth.value, maxHeight.value).dp

        Canvas(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxSize()
                .pointerInput(pieces, cachedPaths) {
                    detectTapGestures { tapOffset ->
                        // Find tapped piece
                        for (piece in pieces) {
                            val path = cachedPaths[piece.id] ?: continue
                            if (JigsawGridGenerator.isPointInPath(path, tapOffset)) {
                                onPieceClicked(piece)
                                break
                            }
                        }
                    }
                }
        ) {
            val boardWidth = size.width
            val boardHeight = size.height

            // Recompute paths if size changed
            if (cachedBoardSize.width != boardWidth || cachedBoardSize.height != boardHeight || cachedPaths.size != pieces.size) {
                val newPaths = mutableMapOf<Int, Path>()
                for (piece in pieces) {
                    newPaths[piece.id] = gridGenerator.createPiecePath(
                        row = piece.row,
                        col = piece.col,
                        boardWidth = boardWidth,
                        boardHeight = boardHeight
                    )
                }
                cachedPaths = newPaths
                cachedBoardSize = Size(boardWidth, boardHeight)
            }

            // Draw Background Plate / Border
            drawRect(
                color = Color(0xFF0F172A),
                size = size
            )

            // Draw each piece
            for (piece in pieces) {
                val path = cachedPaths[piece.id] ?: continue
                val isSelected = piece.id == selectedPieceId

                if (piece.isRevealed) {
                    // --- REVEALED PIECE: Clipped Image Portion ---
                    clipPath(path) {
                        if (imageBitmap != null) {
                            drawImage(
                                image = imageBitmap,
                                dstOffset = IntOffset.Zero,
                                dstSize = IntSize(boardWidth.toInt(), boardHeight.toInt())
                            )
                        } else {
                            // Fallback gradient if no image loaded
                            drawRect(
                                brush = Brush.linearGradient(
                                    listOf(Color(0xFF0284C7), Color(0xFF0F766E))
                                ),
                                size = size
                            )
                        }
                    }

                    // 3D Bevel & Interlocking Joint Line
                    drawPath(
                        path = path,
                        color = Color(0x66FFFFFF),
                        style = Stroke(width = 2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    drawPath(
                        path = path,
                        color = Color(0x33000000),
                        style = Stroke(width = 1f)
                    )

                    // Sparkle celebration on freshly solved piece
                    if (piece.id == newlyRevealedPieceId) {
                        drawPath(
                            path = path,
                            color = Color(0x88F59E0B),
                            style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                        )
                    }
                } else {
                    // --- UNREVEALED PIECE: Tactile Jigsaw Tile ---
                    val cellW = boardWidth / gridSize
                    val cellH = boardHeight / gridSize
                    val centerX = (piece.col + 0.5f) * cellW
                    val centerY = (piece.row + 0.5f) * cellH

                    // Slate-Blue Radial Gradient for Depth
                    val unrevealedBrush = Brush.radialGradient(
                        colors = if (isSelected) {
                            listOf(Color(0xFF334155), Color(0xFF1E293B), Color(0xFF0F172A))
                        } else {
                            listOf(Color(0xFF1E293B), Color(0xFF0F172A), Color(0xFF020617))
                        },
                        center = Offset(centerX, centerY),
                        radius = cellW * 1.1f
                    )

                    drawPath(
                        path = path,
                        brush = unrevealedBrush,
                        style = Fill
                    )

                    // 3D Shadow & Border Lines on Jigsaw Tab/Socket Edges
                    drawPath(
                        path = path,
                        color = if (isSelected) Color(0xFFF59E0B) else Color(0xFF38BDF8).copy(alpha = 0.45f),
                        style = Stroke(
                            width = if (isSelected) 3.5f else 2f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Draw Subtle Piece Center Badge (Piece # and question cue)
                    drawPieceBadge(
                        centerX = centerX,
                        centerY = centerY,
                        pieceNumber = piece.id,
                        ratioAbbr = piece.assignedQuestion.ratio.abbreviation.uppercase(),
                        isSelected = isSelected,
                        badgeRadius = cellW * 0.28f,
                        scale = if (isSelected) pulseAnim.value else 1f
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawPieceBadge(
    centerX: Float,
    centerY: Float,
    pieceNumber: Int,
    ratioAbbr: String,
    isSelected: Boolean,
    badgeRadius: Float,
    scale: Float
) {
    val r = badgeRadius * scale

    // Outer circle
    drawCircle(
        color = if (isSelected) Color(0xFFF59E0B) else Color(0xFF1E293B),
        radius = r,
        center = Offset(centerX, centerY)
    )
    drawCircle(
        color = if (isSelected) Color(0xFFFEF3C7) else Color(0xFF38BDF8),
        radius = r,
        center = Offset(centerX, centerY),
        style = Stroke(width = if (isSelected) 3f else 1.5f)
    )

    // Number & Ratio Text
    val paintNumber = android.graphics.Paint().apply {
        color = if (isSelected) android.graphics.Color.BLACK else android.graphics.Color.WHITE
        textSize = r * 0.85f
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    val paintRatio = android.graphics.Paint().apply {
        color = if (isSelected) android.graphics.Color.DKGRAY else android.graphics.Color.parseColor("#38BDF8")
        textSize = r * 0.45f
        textAlign = android.graphics.Paint.Align.CENTER
        typeface = android.graphics.Typeface.DEFAULT_BOLD
        isAntiAlias = true
    }

    drawContext.canvas.nativeCanvas.apply {
        drawText("#$pieceNumber", centerX, centerY + (r * 0.15f), paintNumber)
        drawText(ratioAbbr, centerX, centerY + (r * 0.68f), paintRatio)
    }
}
