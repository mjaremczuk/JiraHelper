import api.StorageApi
import api.data.Credentials

class DesktopStorageProvider : StorageApi {
    override suspend fun getApiCredentials(): Credentials? {
        return getDesktopApiCredentials()
    }

    override suspend fun updateApiCredentials(credentials: Credentials) {
        updateDesktopApiCredentials(credentials)
    }
}