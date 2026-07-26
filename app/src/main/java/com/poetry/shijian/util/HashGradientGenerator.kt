package com.poetry.shijian.util

import java.security.MessageDigest

/**
 * 哈希渐变生成器
 *
 * 取诗词全文 SHA256，从中提取 HSL 色值：
 * - H（色相）：0-360，由前6位hex决定
 * - S（饱和度）：20-50%，由7-12位hex映射
 * - L（明度）：60-85%，由13-18位hex映射
 */
object HashGradientGenerator {

    data class GradientColors(
        val hsl: Triple<Float, Float, Float>,  // H, S, L
        val startColor: Int,                    // ARGB 主色（60%透明度）
        val endColor: Int,                      // ARGB 过渡色（10%透明度）
    )

    fun generate(text: String): GradientColors {
        val hash = sha256(text)
        val h = (hash.substring(0, 6).toInt(16) % 360).toFloat()
        val s = (20 + (hash.substring(6, 12).toInt(16) % 30)).toFloat()
        val l = (60 + (hash.substring(12, 18).toInt(16) % 25)).toFloat()

        val startArgb = hslToArgb(h, s, l, alpha = 0.60f)
        val endArgb = hslToArgb((h + 30) % 360, (s - 10).coerceAtLeast(15f), (l + 10).coerceAtMost(90f), alpha = 0.10f)

        return GradientColors(
            hsl = Triple(h, s, l),
            startColor = startArgb,
            endColor = endArgb,
        )
    }

    /** 生成纯色背景用的 HSL 字符串（存 Room 用） */
    fun generateHslString(text: String): String {
        val hash = sha256(text)
        val h = hash.substring(0, 6).toInt(16) % 360
        val s = 20 + (hash.substring(6, 12).toInt(16) % 30)
        val l = 60 + (hash.substring(12, 18).toInt(16) % 25)
        return "$h,$s,$l"
    }

    private fun sha256(input: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    private fun hslToArgb(h: Float, s: Float, l: Float, alpha: Float): Int {
        val c = (1f - kotlin.math.abs(2f * l / 100f - 1f)) * s / 100f
        val x = c * (1f - kotlin.math.abs(((h / 60f) % 2f) - 1f))
        val m = l / 100f - c / 2f

        val (r1, g1, b1) = when {
            h < 60 -> Triple(c, x, 0f)
            h < 120 -> Triple(x, c, 0f)
            h < 180 -> Triple(0f, c, x)
            h < 240 -> Triple(0f, x, c)
            h < 300 -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        val a = (alpha * 255).toInt().coerceIn(0, 255)
        val r = ((r1 + m) * 255).toInt().coerceIn(0, 255)
        val g = ((g1 + m) * 255).toInt().coerceIn(0, 255)
        val b = ((b1 + m) * 255).toInt().coerceIn(0, 255)

        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }
}
