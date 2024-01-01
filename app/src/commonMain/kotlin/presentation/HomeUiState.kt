package presentation

import VersionModel
import androidx.compose.runtime.Stable

sealed class HomeUiState {

    data object Loading : HomeUiState()

    @Stable
    data class Error(
        val message: String,
        val ctaLabel: String,
        val action: () -> Unit
    ) : HomeUiState()

    data class Success(val versions: List<VersionModel>) : HomeUiState()
}
