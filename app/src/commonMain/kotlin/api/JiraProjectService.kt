package api

import presentation.JiraProject

class JiraProjectService : JiraProjectApi {

    override suspend fun getProjects(): List<JiraProject> =
        listOf(
            JiraProject("Potato", "MOB", 12944),
            JiraProject("Android Core", "AND", 11600),
            JiraProject("iOS Core", "IOS", 11000),
            JiraProject("Pandas", "APM", 13319),
            JiraProject("India iOS", "MIIOS", 14607),
            JiraProject("India Android", "MIAND", 14606),
        )
}