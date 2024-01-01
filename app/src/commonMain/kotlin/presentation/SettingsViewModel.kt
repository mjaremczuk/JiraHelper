package presentation

import api.CredentialsApi
import api.data.Credentials
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class SettingsViewModel(private val credentialsApi: CredentialsApi) : ViewModel() {

    var credentials: Credentials? = null
        private set

    init {
        viewModelScope.launch {
            credentials = try {
                credentialsApi.getCredentials()
            } catch (exception: Exception) {
                Credentials.default()
            }
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            credentials?.let {
                credentialsApi.updateCredentials(it)
            }
        }
    }

    fun updateUserName(username: String) {
        credentials = credentials?.copy(username = username)
    }

    fun updateToken(token: String) {
        credentials = credentials?.copy(token = token)
    }

    fun updateBaseUrl(url: String) {
        credentials = credentials?.copy(baseUrl = url)
    }
}