package api

import api.data.Credentials
import presentation.JiraProject

interface StorageApi {
    suspend fun getApiCredentials(): Credentials?
    suspend fun updateApiCredentials(credentials: Credentials)
    fun getProjects(): List<JiraProject>
    fun addProject(project: JiraProject)
}