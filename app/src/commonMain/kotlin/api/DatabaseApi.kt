package api

import api.data.Credentials
import presentation.FixVersion
import presentation.JiraProject

interface DatabaseApi {
    fun save(fixVersions: List<FixVersion>): Boolean
    fun getFixVersions(): List<FixVersion>
    fun updateVersionName(versionId: List<String>, new: String)
    fun removeVersions(versionIds: List<String>)

    suspend fun getCredentials(): Credentials?
    suspend fun updateCredentials(credentials: Credentials)
    suspend fun getProjects(): List<JiraProject>
    suspend fun addProject(project: JiraProject)

}