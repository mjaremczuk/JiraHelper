package presentation

import androidx.compose.runtime.mutableStateOf
import api.CredentialsApi
import api.DispatchersProvider
import api.data.Credentials
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class SettingsViewModel(
    private val credentialsApi: CredentialsApi,
    private val dispatchersProvider: DispatchersProvider,
) : ViewModel() {

    var credentials = mutableStateOf<Credentials?>(null)
        private set

    var uiState = mutableStateOf<SettingState>(SettingState.Loading)
        private set

    init {
        viewModelScope.launch(dispatchersProvider.getIoDispatcher()) {
            val credential = try {
                credentialsApi.getCredentials()
            } catch (exception: Exception) {
                Credentials.default()
            }

            viewModelScope.launch(dispatchersProvider.getMainDispatcher()) {
                println("have creds = $credential, nad ${credentials.value}")
                credentials.value = credential
                uiState.value = SettingState.Success(credential)
                println("have creds = $credential, nad ${credentials.value}")
            }
        }
    }

    fun saveChanges() {
        viewModelScope.launch(dispatchersProvider.getIoDispatcher()) {
            credentials.value?.let {
                credentialsApi.updateCredentials(it)
            }
        }
    }

    fun updateUserName(username: String) {
        credentials.value = credentials.value?.copy(username = username)
    }

    fun updateToken(token: String) {
        credentials.value = credentials.value?.copy(token = token)
    }

    fun updateBaseUrl(url: String) {
        credentials.value = credentials.value?.copy(baseUrl = url)
    }

    sealed class SettingState {
        data object Loading : SettingState()
        data class Success(val credentials: Credentials?) : SettingState()
    }
}