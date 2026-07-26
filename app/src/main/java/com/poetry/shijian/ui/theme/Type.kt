package com.poetry.shijian.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/** 默认仿宋字体（系统内置） */
val SongTiFamily = FontFamily.Default

/** 诗笺排版体系 */
val ShijianTypography = Typography(
    // 诗名
    titleLarge = TextStyle(
        fontFamily = SongTiFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 40.sp,
        letterSpacing = 2.sp,
    ),
    // 正文诗词
    bodyLarge = TextStyle(
        fontFamily = SongTiFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.3.sp,
    ),
    // 作者/朝代
    bodyMedium = TextStyle(
        fontFamily = SongTiFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp,
        letterSpacing = 1.sp,
    ),
    // 小字标签
    bodySmall = TextStyle(
        fontFamily = SongTiFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
    ),
    // 文库列表诗名
    titleMedium = TextStyle(
        fontFamily = SongTiFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 24.sp,
    ),
    // 按钮文字
    labelLarge = TextStyle(
        fontFamily = SongTiFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.5.sp,
    ),
)
