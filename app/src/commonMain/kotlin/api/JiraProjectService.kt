package api

import presentation.JiraProject

class JiraProjectService(private val databaseApi: DatabaseApi) : JiraProjectApi {

    override suspend fun getProjects(): List<JiraProject> =
        databaseApi.getProjects()

    override suspend fun addProject(jiraProject: JiraProject) {
        databaseApi.addProject(jiraProject)
    }
}