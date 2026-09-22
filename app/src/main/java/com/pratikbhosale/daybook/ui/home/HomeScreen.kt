package com.pratikbhosale.daybook.ui.home

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewFontScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pratikbhosale.daybook.data.model.Bucket
import com.pratikbhosale.daybook.data.model.ColorKey
import com.pratikbhosale.daybook.data.model.Page
import com.pratikbhosale.daybook.data.model.Task
import com.pratikbhosale.daybook.domain.model.PageProgress
import com.pratikbhosale.daybook.ui.theme.DaybookTheme
import com.pratikbhosale.daybook.ui.theme.Ink
import com.pratikbhosale.daybook.ui.theme.InkMuted
import com.pratikbhosale.daybook.ui.theme.Paper
import com.pratikbhosale.daybook.ui.theme.Seal
import com.pratikbhosale.daybook.ui.theme.colorPairFor
import kotlinx.coroutines.launch

// ── Top-level screen ──────────────────────────────────────────────────────────

@Composable
fun HomeScreen(
    state: HomeUiState,
    onBucketSelected: (Int) -> Unit,
    onPageSelected: (Int) -> Unit,
    onTaskToggle: (Task) -> Unit,
    onAddTask: (pageId: Long, text: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = Paper,
    ) {
        when (state) {
            is HomeUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Seal)
            }
            is HomeUiState.Empty -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No buckets found.", color = InkMuted)
            }
            is HomeUiState.Ready -> ReadyContent(
                state = state,
                onBucketSelected = onBucketSelected,
                onPageSelected = onPageSelected,
                onTaskToggle = onTaskToggle,
                onAddTask = onAddTask,
            )
        }
    }
}

@Composable
private fun ReadyContent(
    state: HomeUiState.Ready,
    onBucketSelected: (Int) -> Unit,
    onPageSelected: (Int) -> Unit,
    onTaskToggle: (Task) -> Unit,
    onAddTask: (pageId: Long, text: String) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // ── Bucket pills ─────────────────────────────────────────────────────
        BucketPillRow(
            buckets = state.buckets,
            activeBucketIndex = state.activeBucketIndex,
            onBucketSelected = onBucketSelected,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        )

        // ── Page pager ───────────────────────────────────────────────────────
        val pagerState = rememberPagerState(
            initialPage = state.activePageIndex,
            pageCount = { state.pages.size.coerceAtLeast(1) },
        )
        val scope = rememberCoroutineScope()

        // Sync pager → ViewModel when user swipes
        LaunchedEffect(pagerState) {
            snapshotFlow { pagerState.currentPage }.collect { page ->
                onPageSelected(page)
            }
        }
        // Sync ViewModel → pager when bucket changes
        LaunchedEffect(state.activePageIndex) {
            if (pagerState.currentPage != state.activePageIndex) {
                scope.launch { pagerState.animateScrollToPage(state.activePageIndex) }
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
        ) { pageIndex ->
            if (state.pages.isEmpty()) {
                EmptyPageCard(modifier = Modifier.fillMaxSize())
            } else {
                val pageUiModel = state.pages[pageIndex]
                val activeBucket = state.buckets[state.activeBucketIndex]
                PageCard(
                    pageUiModel = pageUiModel,
                    bucket = activeBucket,
                    onTaskToggle = onTaskToggle,
                    onAddTask = onAddTask,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // ── Bottom chrome: archive · dots · settings ──────────────────────
        BottomChrome(
            pageCount = state.pages.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }
}

// ── Bucket pills ──────────────────────────────────────────────────────────────

@Composable
private fun BucketPillRow(
    buckets: List<Bucket>,
    activeBucketIndex: Int,
    onBucketSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        buckets.forEachIndexed { index, bucket ->
            BucketPill(
                bucket = bucket,
                isActive = index == activeBucketIndex,
                onClick = { onBucketSelected(index) },
            )
        }
    }
}

@Composable
private fun BucketPill(
    bucket: Bucket,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val colors = colorPairFor(bucket.colorKey)
    val shape = RoundedCornerShape(50)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(if (isActive) colors.fill else Color.Transparent)
            .then(
                if (!isActive) Modifier.drawBehind {
                    drawRoundRect(
                        color = colors.fill,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(50f),
                        style = Stroke(width = 1.5.dp.toPx()),
                    )
                } else Modifier,
            )
            .clickable(
                onClick = onClick,
                onClickLabel = "${bucket.name} bucket",
            )
            .semantics {
                role = Role.Tab
                contentDescription = "${bucket.name}${if (isActive) ", selected" else ""}"
            }
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = bucket.name,
            color = if (isActive) colors.ink else colors.fill,
            fontSize = 14.sp,
            maxLines = 1,
        )
    }
}

// ── Page card ─────────────────────────────────────────────────────────────────

@Composable
private fun PageCard(
    pageUiModel: PageUiModel,
    bucket: Bucket,
    onTaskToggle: (Task) -> Unit,
    onAddTask: (pageId: Long, text: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = colorPairFor(bucket.colorKey)

    Column(
        modifier = modifier.padding(vertical = 8.dp),
    ) {
        // Title + ring row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = pageUiModel.page.title,
                color = Ink,
                fontSize = 22.sp,
                fontFamily = FontFamily.Default,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Page: ${pageUiModel.page.title}" },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.width(12.dp))
            ProgressRing(
                progress = pageUiModel.progress,
                accentColor = colors.fill,
                sealColor = Seal,
                size = 48.dp,
                modifier = Modifier.semantics {
                    contentDescription = "Progress: ${pageUiModel.progress.done} of ${pageUiModel.progress.total} tasks done"
                },
            )
        }

        // Task list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            items(pageUiModel.tasks, key = { it.id }) { task ->
                TaskRow(
                    task = task,
                    accentColor = colors.fill,
                    onToggle = { onTaskToggle(task) },
                )
            }

            // Write-a-task dashed line
            item(key = "write_task") {
                WriteTaskRow(
                    pageId = pageUiModel.page.id,
                    onAddTask = onAddTask,
                )
            }
        }
    }
}

@Composable
private fun EmptyPageCard(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text("No pages yet.", color = InkMuted)
    }
}

// ── Task row with strikethrough animation ─────────────────────────────────────

@Composable
private fun TaskRow(
    task: Task,
    accentColor: Color,
    onToggle: () -> Unit,
) {
    val view = LocalView.current
    // Animate strikethrough width: 0f → 1f when done
    val strikeProgress = remember(task.id) { Animatable(if (task.isDone) 1f else 0f) }

    LaunchedEffect(task.isDone) {
        if (task.isDone && strikeProgress.value < 1f) {
            strikeProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 200, easing = LinearEasing),
            )
        } else if (!task.isDone) {
            strikeProgress.snapTo(0f)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                    onToggle()
                },
                onClickLabel = if (task.isDone) "Mark ${task.text} undone" else "Mark ${task.text} done",
            )
            .semantics {
                role = Role.Checkbox
                contentDescription = "${task.text}, ${if (task.isDone) "done" else "not done"}"
            }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Completion circle
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (task.isDone) accentColor else Color.Transparent)
                .drawBehind {
                    drawCircle(
                        color = accentColor,
                        style = Stroke(width = 1.5.dp.toPx()),
                    )
                },
        )
        Spacer(Modifier.width(12.dp))

        // Task text with animated strikethrough drawn on the Canvas layer
        val textColor = if (task.isDone) InkMuted else Ink
        Box(modifier = Modifier.weight(1f)) {
            Text(
                text = task.text,
                color = textColor,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        if (strikeProgress.value > 0f) {
                            val y = size.height / 2f
                            val endX = size.width * strikeProgress.value
                            // Slightly hand-drawn: nudge y slightly off perfect center
                            val nudge = 1.5f
                            drawLine(
                                color = textColor,
                                start = androidx.compose.ui.geometry.Offset(0f, y + nudge),
                                end = androidx.compose.ui.geometry.Offset(endX, y - nudge),
                                strokeWidth = 1.5.dp.toPx(),
                                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                            )
                        }
                    },
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

// ── Write-a-task dashed input row ─────────────────────────────────────────────

@Composable
private fun WriteTaskRow(
    pageId: Long,
    onAddTask: (pageId: Long, text: String) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    val inkMuted = InkMuted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 4.dp)
            .drawBehind {
                // Dashed underline
                val dashWidth = 6.dp.toPx()
                val dashGap = 4.dp.toPx()
                val y = size.height - 1.dp.toPx()
                var x = 0f
                while (x < size.width) {
                    drawLine(
                        color = inkMuted.copy(alpha = 0.4f),
                        start = androidx.compose.ui.geometry.Offset(x, y),
                        end = androidx.compose.ui.geometry.Offset((x + dashWidth).coerceAtMost(size.width), y),
                        strokeWidth = 1.dp.toPx(),
                    )
                    x += dashWidth + dashGap
                }
            }
            .semantics { contentDescription = "Write a task" },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Placeholder circle to match task row layout
        Box(modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))

        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            textStyle = TextStyle(
                color = Ink,
                fontSize = 16.sp,
                fontFamily = FontFamily.Default,
            ),
            cursorBrush = SolidColor(Ink),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (text.isNotBlank()) {
                        onAddTask(pageId, text)
                        text = ""
                    }
                },
            ),
            singleLine = true,
            decorationBox = { inner ->
                if (text.isEmpty()) {
                    Text(
                        text = "write a task…",
                        color = InkMuted.copy(alpha = 0.6f),
                        fontSize = 16.sp,
                    )
                }
                inner()
            },
        )
    }
}

// ── Bottom chrome ─────────────────────────────────────────────────────────────

@Composable
private fun BottomChrome(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { /* TODO: navigate to archive */ },
            modifier = Modifier.semantics { contentDescription = "Open archive" },
        ) {
            Icon(Icons.Outlined.Archive, contentDescription = null, tint = InkMuted)
        }

        // Page dots
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(pageCount.coerceAtLeast(1)) { index ->
                val isActive = index == currentPage
                Box(
                    modifier = Modifier
                        .size(if (isActive) 7.dp else 5.dp)
                        .clip(CircleShape)
                        .background(if (isActive) Ink else InkMuted.copy(alpha = 0.4f))
                        .semantics {
                            contentDescription = "Page ${index + 1} of $pageCount"
                        },
                )
            }
        }

        IconButton(
            onClick = { /* TODO: navigate to settings */ },
            modifier = Modifier.semantics { contentDescription = "Open settings" },
        ) {
            Icon(Icons.Outlined.Settings, contentDescription = null, tint = InkMuted)
        }
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

private val previewBuckets = listOf(
    Bucket(1, "Today", ColorKey.TODAY, 0, true),
    Bucket(2, "This week", ColorKey.WEEK, 1, true),
    Bucket(3, "Someday", ColorKey.SOMEDAY, 2, true),
)

private val previewTasks = listOf(
    Task(1, 1, "Buy coffee beans", isDone = true, sortOrder = 0, doneAt = 1000L),
    Task(2, 1, "Write unit tests", isDone = false, sortOrder = 1),
    Task(3, 1, "Push PR before EOD", isDone = false, sortOrder = 2),
)

private val previewPage = PageUiModel(
    page = Page(1, 1, "Morning", sortOrder = 0, createdAt = 0L),
    tasks = previewTasks,
    progress = PageProgress.from(previewTasks),
)

private val previewState = HomeUiState.Ready(
    buckets = previewBuckets,
    activeBucketIndex = 0,
    pages = listOf(previewPage),
    activePageIndex = 0,
)

@Preview(name = "Home — Light", showBackground = true)
@Composable
private fun HomeScreenPreviewLight() {
    DaybookTheme(darkTheme = false) {
        HomeScreen(
            state = previewState,
            onBucketSelected = {},
            onPageSelected = {},
            onTaskToggle = {},
            onAddTask = { _, _ -> },
        )
    }
}

@Preview(name = "Home — Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun HomeScreenPreviewDark() {
    DaybookTheme(darkTheme = true) {
        HomeScreen(
            state = previewState,
            onBucketSelected = {},
            onPageSelected = {},
            onTaskToggle = {},
            onAddTask = { _, _ -> },
        )
    }
}

@PreviewFontScale
@Preview(name = "Home — Large font", showBackground = true)
@Composable
private fun HomeScreenPreviewLargeFont() {
    DaybookTheme(darkTheme = false) {
        HomeScreen(
            state = previewState,
            onBucketSelected = {},
            onPageSelected = {},
            onTaskToggle = {},
            onAddTask = { _, _ -> },
        )
    }
}
