package com.example.jigsaw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import com.example.model.EdgeType
import com.example.model.PieceEdges
import kotlin.random.Random

/**
 * Generates an interlocking puzzle grid where neighboring pieces share exact matching edges.
 */
class JigsawGridGenerator(private val gridSize: Int) {

    // horizontal edges: (gridSize + 1) rows, each having gridSize edges
    // +1 means tab down, -1 means tab up, 0 means flat
    private val horizontalEdges = Array(gridSize + 1) { r ->
        FloatArray(gridSize) {
            if (r == 0 || r == gridSize) 0f else if (Random.nextBoolean()) 1f else -1f
        }
    }

    // vertical edges: gridSize rows, each having (gridSize + 1) edges
    // +1 means tab right, -1 means tab left, 0 means flat
    private val verticalEdges = Array(gridSize) {
        FloatArray(gridSize + 1) { c ->
            if (c == 0 || c == gridSize) 0f else if (Random.nextBoolean()) 1f else -1f
        }
    }

    // Deterministic tab edge types for a piece at (row, col)
    fun getEdgesForPiece(row: Int, col: Int): PieceEdges {
        val topSign = -horizontalEdges[row][col] // outward is UP (-hEdge)
        val rightSign = verticalEdges[row][col + 1] // outward is RIGHT (+vEdge)
        val bottomSign = horizontalEdges[row + 1][col] // outward is DOWN (+hEdge)
        val leftSign = -verticalEdges[row][col] // outward is LEFT (-vEdge)

        return PieceEdges(
            top = signToEdgeType(topSign),
            right = signToEdgeType(rightSign),
            bottom = signToEdgeType(bottomSign),
            left = signToEdgeType(leftSign)
        )
    }

    private fun signToEdgeType(sign: Float): EdgeType {
        return when {
            sign > 0.5f -> EdgeType.TAB
            sign < -0.5f -> EdgeType.SOCKET
            else -> EdgeType.FLAT
        }
    }

    /**
     * Builds the exact closed Path for piece (row, col) inside a puzzle of total size (width x height).
     */
    fun createPiecePath(
        row: Int,
        col: Int,
        boardWidth: Float,
        boardHeight: Float
    ): Path {
        val pieceWidth = boardWidth / gridSize
        val pieceHeight = boardHeight / gridSize

        val x0 = col * pieceWidth
        val x1 = (col + 1) * pieceWidth
        val y0 = row * pieceHeight
        val y1 = (row + 1) * pieceHeight

        val pTopLeft = Offset(x0, y0)
        val pTopRight = Offset(x1, y0)
        val pBottomRight = Offset(x1, y1)
        val pBottomLeft = Offset(x0, y1)

        val path = Path()
        path.moveTo(pTopLeft.x, pTopLeft.y)

        // 1. TOP EDGE: Left to Right (pTopLeft -> pTopRight)
        // Global hEdge sign: +1 is down (into this piece = SOCKET), -1 is up (outward = TAB)
        val hTopSign = horizontalEdges[row][col]
        // From piece perspective: outward is UP (-y). When hTopSign is -1 (tab up), outward is +1.
        val topLocalSign = -hTopSign
        appendEdge(path, pTopLeft, pTopRight, topLocalSign)

        // 2. RIGHT EDGE: Top to Bottom (pTopRight -> pBottomRight)
        // Global vEdge sign: +1 is right (outward = TAB), -1 is left (inward = SOCKET)
        val vRightSign = verticalEdges[row][col + 1]
        val rightLocalSign = vRightSign
        appendEdge(path, pTopRight, pBottomRight, rightLocalSign)

        // 3. BOTTOM EDGE: Right to Left (pBottomRight -> pBottomLeft)
        // Global hEdge sign: +1 is down (outward = TAB), -1 is up (inward = SOCKET)
        val hBottomSign = horizontalEdges[row + 1][col]
        val bottomLocalSign = hBottomSign
        appendEdge(path, pBottomRight, pBottomLeft, bottomLocalSign)

        // 4. LEFT EDGE: Bottom to Top (pBottomLeft -> pTopLeft)
        // Global vEdge sign: +1 is right (inward = SOCKET), -1 is left (outward = TAB)
        val vLeftSign = verticalEdges[row][col]
        val leftLocalSign = -vLeftSign
        appendEdge(path, pBottomLeft, pTopLeft, leftLocalSign)

        path.close()
        return path
    }

    /**
     * Appends an interlocking jigsaw edge from p0 to p1.
     * sign: 0 = straight flat edge, +1 = tab outward, -1 = socket inward.
     */
    private fun appendEdge(path: Path, p0: Offset, p1: Offset, sign: Float) {
        if (sign == 0f) {
            path.lineTo(p1.x, p1.y)
            return
        }

        val dx = p1.x - p0.x
        val dy = p1.y - p0.y
        val length = kotlin.math.sqrt(dx * dx + dy * dy)
        if (length <= 0.0001f) {
            path.lineTo(p1.x, p1.y)
            return
        }

        // Unit tangent vector
        val tx = dx / length
        val ty = dy / length

        // Unit normal vector (pointing to the right of direction p0 -> p1)
        // In screen coordinates: going right (tx=1, ty=0) -> normal is (0, -1) which is UP (outward for top edge)
        // Let normal be (-ty, tx).
        val nx = -ty
        val ny = tx

        fun pt(u: Float, v: Float): Offset {
            val distU = u * length
            val distV = v * length * sign
            return Offset(
                p0.x + tx * distU + nx * distV,
                p0.y + ty * distU + ny * distV
            )
        }

        // Segment 1: baseline lead-in
        val pLead = pt(0.36f, 0f)
        path.lineTo(pLead.x, pLead.y)

        // Segment 2: neck indentation & expansion into bulb
        val pC1_1 = pt(0.38f, -0.04f)
        val pC1_2 = pt(0.36f, 0.08f)
        val pC1_end = pt(0.40f, 0.18f)
        path.cubicTo(pC1_1.x, pC1_1.y, pC1_2.x, pC1_2.y, pC1_end.x, pC1_end.y)

        // Segment 3: bulbous crown of the tab
        val pC2_1 = pt(0.44f, 0.23f)
        val pC2_2 = pt(0.56f, 0.23f)
        val pC2_end = pt(0.60f, 0.18f)
        path.cubicTo(pC2_1.x, pC2_1.y, pC2_2.x, pC2_2.y, pC2_end.x, pC2_end.y)

        // Segment 4: return through neck to baseline
        val pC3_1 = pt(0.64f, 0.08f)
        val pC3_2 = pt(0.62f, -0.04f)
        val pC3_end = pt(0.64f, 0f)
        path.cubicTo(pC3_1.x, pC3_1.y, pC3_2.x, pC3_2.y, pC3_end.x, pC3_end.y)

        // Segment 5: baseline finish
        path.lineTo(p1.x, p1.y)
    }

    companion object {
        /**
         * Fast pixel-accurate hit test whether an Offset is inside an irregular Path.
         */
        fun isPointInPath(path: Path, point: Offset): Boolean {
            val androidPath = path.asAndroidPath()
            val bounds = android.graphics.RectF()
            androidPath.computeBounds(bounds, true)
            val region = android.graphics.Region()
            region.setPath(
                androidPath,
                android.graphics.Region(
                    bounds.left.toInt(),
                    bounds.top.toInt(),
                    bounds.right.toInt(),
                    bounds.bottom.toInt()
                )
            )
            return region.contains(point.x.toInt(), point.y.toInt())
        }
    }
}
