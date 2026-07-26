package com.poetry.shijian.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.poetry.shijian.util.HashGradientGenerator

/**
 * 诗词哈希渐变背景
 *
 * 从 HSL 色值解析出两个颜色，绘制径向渐变 + 底部渐隐到背景色。
 * @param hslString "H,S,L" 格式
 * @param fadeToColor 底部渐隐的目标色
 */
@Composable
fun PoemGradientBackground(
    hslString: String,
    fadeToColor: Color,
    modifier: Modifier = Modifier,
) {
    val colors = remember(hslString) {
        val parts = hslString.split(",").map { it.trim().toFloatOrNull() ?: 0f }
        val h = parts.getOrElse(0) { 220f }
        val s = parts.getOrElse(1) { 35f }
        val l = parts.getOrElse(2) { 72f }
        val start = Color.hsl(h, s / 100f, l / 100f).copy(alpha = 0.60f)
        val end = Color.hsl((h + 30) % 360f, (s / 100f - 0.1f).coerceAtLeast(0.15f), ((l + 10f) / 100f).coerceAtMost(0.9f))
            .copy(alpha = 0.10f)
        start to end
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 径向渐变（中心偏右上）
        drawRoundRect(
            brush = Brush.radialGradient(
                colors = listOf(colors.first, colors.second),
                center = Offset(w * 0.6f, h * 0.3f),
                radius = w.coerceAtLeast(h) * 1.2f,
            ),
            size = size,
        )

        // 底部渐隐到背景色
        drawRoundRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Transparent,
                    fadeToColor.copy(alpha = 0.3f),
                    fadeToColor.copy(alpha = 0.7f),
                    fadeToColor,
                ),
                startY = h * 0.55f,
                endY = h,
            ),
            size = size,
        )
    }
}
