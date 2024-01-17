package presentation

import VersionModel
import androidx.compose.runtime.mutableStateOf
import api.JiraApi
import api.MissingCredentialsException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class HomeViewModel(
    private val jiraApi: JiraApi,
) : ViewModel() {

    val updated = MutableStateFlow(false)
    val shareUiState = mutableStateOf<SearchState>(SearchState.Loading)
    val uiState = mutableStateOf<HomeUiState>(HomeUiState.Loading)

    fun getVersionList() {
        viewModelScope.launch {
            getFixVersions()
        }
    }

    fun updateVersion(value: VersionModel?, new: String) {
        viewModelScope.launch {
            value?.let {
                jiraApi.updateVersion(it, new)
            }
            uiState.value = HomeUiState.Success(jiraApi.getFixVersions(false))
            updated.value = true
        }
    }

    fun setViewUpdated() {
        updated.value = false
    }

    fun refreshFixVersions() {
        viewModelScope.launch {
            getFixVersions(true)
        }
    }

    private suspend fun getFixVersions(force: Boolean = false) {
        try {
            uiState.value = HomeUiState.Loading
            val versions = jiraApi.getFixVersions(force)
                .filter { it.projects.all { project -> project.released.not() } }
                .filter { it.projects.all { project -> project.archived.not() } }
            uiState.value = HomeUiState.Success(versions)

        } catch (ex: Exception) {
            when (ex) {
                is CancellationException -> {}
                is MissingCredentialsException -> showError()
                else -> showError()
            }
            ex.printStackTrace()
        }
    }

    fun getTicketsFor(name: String) {
        viewModelScope.launch {
            shareUiState.value = SearchState.Loading
            shareUiState.value = SearchState.Success(jiraApi.getTicketsFor(name))
        }
    }

    fun remove(value: VersionModel?) {
        viewModelScope.launch {
            try {
                value?.let {
                    jiraApi.removeFixVersion(value)
                }
                uiState.value = HomeUiState.Success(jiraApi.getFixVersions(false))
                updated.value = true
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun showError() {
        uiState.value = HomeUiState.Error(
            message = "Configure credentials to start using the app",
            ctaLabel = "Go to settings",
            action = {}, // todo add navigation action
        )
    }

    sealed class SearchState {
        data object Loading : SearchState()
        data class Success(val tickets: List<String>) : SearchState()
    }
}