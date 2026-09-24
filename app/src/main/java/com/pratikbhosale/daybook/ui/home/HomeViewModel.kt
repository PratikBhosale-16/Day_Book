package com.pratikbhosale.daybook.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pratikbhosale.daybook.data.model.Bucket
import com.pratikbhosale.daybook.data.model.Page
import com.pratikbhosale.daybook.data.model.Task
import com.pratikbhosale.daybook.domain.model.PageProgress
import com.pratikbhosale.daybook.domain.repository.DaybookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** UI state for the Home screen — sealed so the UI has no nullable-flag combinations. */
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data object Empty : HomeUiState // no buckets (shouldn't happen after seeding)
    data class Ready(
        val buckets: List<Bucket>,
        val activeBucketIndex: Int,
        val pages: List<PageUiModel>,
        val activePageIndex: Int,
    ) : HomeUiState
}

data class PageUiModel(
    val page: Page,
    val tasks: List<Task>,
    val progress: PageProgress,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DaybookRepository,
) : ViewModel() {

    private val activeBucketIndex = MutableStateFlow(0)
    private val activePageIndex = MutableStateFlow(0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<HomeUiState> = repository.observeBuckets()
        .flatMapLatest { buckets ->
            if (buckets.isEmpty()) {
                kotlinx.coroutines.flow.flowOf(HomeUiState.Empty)
            } else {
                val bucketIdx = activeBucketIndex.value.coerceIn(0, buckets.lastIndex)
                val bucket = buckets[bucketIdx]
                pagesFlowFor(bucket.id).map { pages ->
                    val pageIdx = activePageIndex.value.coerceIn(
                        0,
                        if (pages.isEmpty()) 0 else pages.lastIndex,
                    )
                    HomeUiState.Ready(
                        buckets = buckets,
                        activeBucketIndex = bucketIdx,
                        pages = pages,
                        activePageIndex = pageIdx,
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState.Loading,
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun pagesFlowFor(bucketId: Long): Flow<List<PageUiModel>> =
        repository.observeActivePagesForBucket(bucketId).flatMapLatest { pages ->
            if (pages.isEmpty()) {
                kotlinx.coroutines.flow.flowOf(emptyList())
            } else {
                val taskFlows: List<Flow<PageUiModel>> = pages.map { page ->
                    repository.observeTasksForPage(page.id).map { tasks ->
                        PageUiModel(
                            page = page,
                            tasks = tasks,
                            progress = PageProgress.from(tasks),
                        )
                    }
                }
                combine(taskFlows) { it.toList() }
            }
        }

    fun selectBucket(index: Int) {
        activeBucketIndex.value = index
        activePageIndex.value = 0
    }

    fun selectPage(index: Int) {
        activePageIndex.value = index
    }

    fun toggleTask(task: Task) {
        viewModelScope.launch {
            repository.setTaskDone(
                id = task.id,
                isDone = !task.isDone,
                doneAt = if (!task.isDone) System.currentTimeMillis() else null,
            )
        }
    }

    fun addTask(pageId: Long, text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            val trimmed = text.trim()
            val currentTasks = repository.getTasksForPage(pageId)
            val nextOrder = if (currentTasks.isEmpty()) 0 else currentTasks.maxOf { it.sortOrder } + 1
            repository.addTask(
                Task(
                    pageId = pageId,
                    text = trimmed,
                    sortOrder = nextOrder,
                ),
            )
        }
    }

    fun createPage() {
        val currentState = uiState.value
        if (currentState !is HomeUiState.Ready) return

        val bucketId = currentState.buckets[currentState.activeBucketIndex].id
        val maxOrder = currentState.pages.maxOfOrNull { it.page.sortOrder } ?: -1

        viewModelScope.launch {
            repository.addPage(
                Page(
                    bucketId = bucketId,
                    title = "", // untitled by default
                    sortOrder = maxOrder + 1,
                    createdAt = System.currentTimeMillis()
                )
            )
            // Immediately focus the new page (assumes it will be appended to the end)
            activePageIndex.value = currentState.pages.size
        }
    }
}
