package com.poetry.shijian.ui.daily

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Shuffle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.poetry.shijian.data.repository.PoetryRepository
import com.poetry.shijian.domain.model.Poem
import com.poetry.shijian.domain.model.TimeSlot
import com.poetry.shijian.ui.components.PoemGradientBackground
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DailyScreen(
    repository: PoetryRepository,
) {
    var currentPoem by remember { mutableStateOf<Poem?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isFavorite by remember { mutableStateOf(false) }
    var animationTarget by remember { mutableStateOf(AnimationTarget.None) }
    var stayStartTime by remember { mutableStateOf(System.currentTimeMillis()) }

    val scope = rememberCoroutineScope()

    // 获取当前时段
    val currentSlot = remember {
        val hour = LocalTime.now().hour
        when {
            hour in 5..11 -> TimeSlot.MORNING
            hour in 12..17 -> TimeSlot.AFTERNOON
            else -> TimeSlot.NIGHT
        }
    }

    // 首次加载
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val poem = repository.getDailyPoem(currentSlot)
            currentPoem = poem
            isFavorite = repository.isFavorite(poem.id)
            repository.recordRead(poem.id)
        } catch (_: Exception) {
            // 网络错误时尝试从缓存取
        }
        isLoading = false
    }

    // 滑动切换
    var dragStartY by remember { mutableStateOf(0f) }
    var dragDelta by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        if (isLoading) {
            // 加载占位
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "诗笺",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                )
            }
        }

        currentPoem?.let { poem ->
            AnimatedContent(
                targetState = poem,
                transitionSpec = {
                    when (animationTarget) {
                        AnimationTarget.Next -> {
                            (slideInVertically(tween(400)) { it }) togetherWith
                                (slideOutVertically(tween(400)) { -it })
                        }
                        AnimationTarget.Previous -> {
                            (slideInVertically(tween(400)) { -it }) togetherWith
                                (slideOutVertically(tween(400)) { it })
                        }
                        AnimationTarget.None -> {
                            (slideInVertically(tween(300)) { it / 3 }) togetherWith
                                (slideOutVertically(tween(300)) { -it / 3 })
                        }
                    }
                },
                label = "poem_swipe",
            ) { targetPoem ->
                PoemContent(
                    poem = targetPoem,
                    isFavorite = isFavorite,
                    slot = currentSlot,
                    onSwipeUp = {
                        animationTarget = AnimationTarget.Next
                        scope.launch {
                            val newPoem = repository.shufflePoem(
                                currentPoemId = targetPoem.id,
                                preferSimilar = isFavorite || (System.currentTimeMillis() - stayStartTime > 10_000),
                            )
                            repository.recordRead(newPoem.id)
                            currentPoem = newPoem
                            isFavorite = repository.isFavorite(newPoem.id)
                            stayStartTime = System.currentTimeMillis()
                            animationTarget = AnimationTarget.None
                        }
                    },
                    onSwipeDown = {
                        // 下滑暂不做上一首（每日推荐没有"上一首"的概念）
                    },
                    onFavoriteClick = {
                        scope.launch {
                            repository.toggleFavorite(targetPoem.id)
                            isFavorite = !isFavorite
                        }
                    },
                    onShuffleClick = {
                        animationTarget = AnimationTarget.Next
                        scope.launch {
                            val newPoem = repository.shufflePoem(
                                currentPoemId = targetPoem.id,
                                preferSimilar = isFavorite || (System.currentTimeMillis() - stayStartTime > 10_000),
                            )
                            repository.recordRead(newPoem.id)
                            currentPoem = newPoem
                            isFavorite = repository.isFavorite(newPoem.id)
                            stayStartTime = System.currentTimeMillis()
                            animationTarget = AnimationTarget.None
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun PoemContent(
    poem: Poem,
    isFavorite: Boolean,
    slot: TimeSlot,
    onSwipeUp: () -> Unit,
    onSwipeDown: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShuffleClick: () -> Unit,
) {
    val today = remember {
        LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd  EEEE", Locale.CHINESE))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = { /* 不需要额外操作 */ },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        if (dragAmount < -50) onSwipeUp()
                        else if (dragAmount > 50) onSwipeDown()
                    },
                )
            },
    ) {
        // 渐变背景
        PoemGradientBackground(
            hslString = poem.hashGradient,
            fadeToColor = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize(),
        )

        // 内容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // 时段标签
            Text(
                text = slot.label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 诗名
            Text(
                text = poem.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 作者
            Text(
                text = poem.author,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 正文
            Text(
                text = poem.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 体裁标签
            Text(
                text = "── ${poem.dynasty} · ${poem.type} ──",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) "取消收藏" else "收藏",
                        tint = if (isFavorite)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                Spacer(modifier = Modifier.width(48.dp))

                IconButton(onClick = onShuffleClick) {
                    Icon(
                        imageVector = Icons.Outlined.Shuffle,
                        contentDescription = "换一首",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 日期
            Text(
                text = today,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            )

            Spacer(modifier = Modifier.height(48.dp))

            // 滑动提示
            Text(
                text = "↑ 上滑换一首 ↓",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f),
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private enum class AnimationTarget { None, Next, Previous }
