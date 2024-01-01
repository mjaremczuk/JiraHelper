package api

import api.data.Credentials
import getApiCredentials
import getFixVersionList
import presentation.FixVersion
import removeFixVersions
import saveFixVersions
import updateApiCredentials
import updateFixVersionName

class DatabaseService() : DatabaseApi {
    override fun save(fixVersions: List<FixVersion>): Boolean {
        return saveFixVersions(fixVersions)
    }

    override fun updateVersionName(versionId: List<String>, new: String) {
        updateFixVersionName(versionId, new)
    }

    override fun removeVersions(versionIds: List<String>) {
        removeFixVersions(versionIds)
    }

    override fun getCredentials(): Credentials? {
        return getApiCredentials()
    }

    override fun updateCredentials(credentials: Credentials) {
        updateApiCredentials(credentials)
    }

    override fun getFixVersions(): List<FixVersion> {
        return getFixVersionList()
    }
}