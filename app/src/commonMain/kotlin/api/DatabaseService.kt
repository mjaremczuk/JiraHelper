package api

import api.data.Credentials
import getFixVersionList
import presentation.FixVersion
import removeFixVersions
import saveFixVersions
import updateFixVersionName

class DatabaseService(
    private val storageApi: StorageApi,
) : DatabaseApi {
    override fun save(fixVersions: List<FixVersion>): Boolean {
        return saveFixVersions(fixVersions)
    }

    override fun updateVersionName(versionId: List<String>, new: String) {
        updateFixVersionName(versionId, new)
    }

    override fun removeVersions(versionIds: List<String>) {
        removeFixVersions(versionIds)
    }

    override suspend fun getCredentials(): Credentials? {
        return storageApi.getApiCredentials()
    }

    override suspend fun updateCredentials(credentials: Credentials) {
        storageApi.updateApiCredentials(credentials)
    }

    override fun getFixVersions(): List<FixVersion> {
        return getFixVersionList()
    }
}