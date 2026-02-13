package kz.aitu.fitnessworkouttracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kz.aitu.fitnessworkouttracker.data.api.ExerciseItem
import kz.aitu.fitnessworkouttracker.repository.ExerciseRepository

data class SearchState(
    val query: String = "",
    val items: List<ExerciseItem> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val hasMore: Boolean = false
)

class SearchViewModel(
    private val repo: ExerciseRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private val limit = 20
    private var offset = 0

    private var debounceJob: Job? = null

    fun onQueryChange(q: String) {
        _state.update { it.copy(query = q, error = null) }

        // если пусто — очистка
        if (q.isBlank()) {
            debounceJob?.cancel()
            offset = 0
            _state.update { it.copy(items = emptyList(), loading = false, hasMore = false, error = null) }
            return
        }

        // ✅ новый query → сброс страницы + очистить список
        offset = 0
        _state.update { it.copy(items = emptyList(), hasMore = false) }

        debounceJob?.cancel()
        debounceJob = viewModelScope.launch {
            delay(450)
            search(reset = true)
        }
    }

    fun retry() {
        if (_state.value.query.isBlank()) return
        viewModelScope.launch {
            search(reset = (offset == 0))
        }
    }

    fun loadMore() {
        val s = _state.value
        if (s.loading || !s.hasMore || s.query.isBlank()) return

        viewModelScope.launch {
            search(reset = false)
        }
    }

    private suspend fun search(reset: Boolean) {
        val q = _state.value.query.trim()
        if (q.isBlank()) return

        _state.update { it.copy(loading = true, error = null) }

        try {
            val currentOffset = if (reset) 0 else offset
            val result = repo.searchWithNames(q, limit, currentOffset)

            _state.update { st ->
                val merged = if (reset) result else (st.items + result)
                st.copy(
                    items = merged,
                    loading = false,
                    error = null,
                    hasMore = result.size == limit
                )
            }

            offset = currentOffset + result.size

        } catch (e: Exception) {
            _state.update { it.copy(loading = false, error = e.message ?: "Request failed") }
        }
    }
}
