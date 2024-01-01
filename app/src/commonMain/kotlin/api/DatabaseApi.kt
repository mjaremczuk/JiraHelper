package api

import api.data.Credentials
import presentation.FixVersion

interface DatabaseApi {
    fun save(fixVersions: List<FixVersion>): Boolean
    fun getFixVersions(): List<FixVersion>
    fun updateVersionName(versionId: List<String>, new: String)
    fun removeVersions(versionIds: List<String>)

    fun getCredentials(): Credentials?
    fun updateCredentials(credentials: Credentials)
}