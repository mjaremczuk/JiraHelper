package api

import presentation.JiraProject

interface JiraProjectApi {

    suspend fun getProjects(): List<JiraProject>
}