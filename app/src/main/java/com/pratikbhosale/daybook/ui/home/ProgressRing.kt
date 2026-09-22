package com.pratikbhosale.daybook.ui.home

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pratikbhosale.daybook.domain.model.PageProgress

/**
 * Circular progress ring that fills in discrete task-count segments per DESIGN.md.
 *
 * "fills by task count in discrete segments, not a smooth percentage arc.
 *  With 3-7 tasks, visible steps read more honestly than a percentage."
 *
 * On completion ([progress.isComplete]) the ring transitions to a checkmark drawn inside
 * the circle using the [sealColor].
 *
 * Respects reduced-motion: when [animate] is false, transitions are instant.
 */
@Composable
fun ProgressRing(
    progress: PageProgress,
    accentColor: Color,
    sealColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    strokeWidth: Dp = 4.dp,
    animate: Boolean = true,
) {
    val segments = progress.total.coerceAtLeast(1)
    val filledFraction = if (progress.total == 0) 0f else progress.done.toFloat() / segments

    // Animate the fill fraction for smooth segment transitions
    val animatedFraction by animateFloatAsState(
        targetValue = if (progress.isComplete) 1f else filledFraction,
        animationSpec = if (animate) tween(300) else tween(0),
        label = "ring_fraction",
    )

    val isComplete = progress.isComplete

    Canvas(modifier = modifier.size(size)) {
        val strokePx = strokeWidth.toPx()
        val diameter = this.size.minDimension - strokePx
        val topLeft = Offset(strokePx / 2f, strokePx / 2f)
        val arcSize = Size(diameter, diameter)

        if (isComplete) {
            // Draw sealed ring
            drawArc(
                color = sealColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )
            // Draw checkmark inside
            drawCheckmark(
                color = sealColor,
                strokeWidth = strokePx,
            )
        } else {
            // Track (background ring)
            drawArc(
                color = accentColor.copy(alpha = 0.2f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )

            if (progress.total > 0) {
                // Discrete segment gaps — gap is 4 degrees between segments
                val gapDegrees = if (segments > 1) 4f else 0f
                val totalGap = gapDegrees * segments
                val segmentSweep = (360f - totalGap) / segments

                for (i in 0 until progress.done) {
                    val startAngle = -90f + i * (segmentSweep + gapDegrees)
                    drawArc(
                        color = accentColor,
                        startAngle = startAngle,
                        sweepAngle = segmentSweep * animatedFraction.coerceAtLeast(
                            if (i < progress.done - 1) 1f else 0f,
                        ),
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round),
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawCheckmark(color: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height
    val path = androidx.compose.ui.graphics.Path().apply {
        moveTo(w * 0.25f, h * 0.52f)
        lineTo(w * 0.43f, h * 0.68f)
        lineTo(w * 0.75f, h * 0.35f)
    }
    drawPath(
        path = path,
        color = color,
        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
    )
}
