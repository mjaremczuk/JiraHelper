import api.StorageApi
import api.data.Credentials
import presentation.JiraProject

class DesktopStorageProvider : StorageApi {
    override suspend fun getApiCredentials(): Credentials? {
        return getDesktopApiCredentials()
    }

    override suspend fun updateApiCredentials(credentials: Credentials) {
        updateDesktopApiCredentials(credentials)
    }

    override fun getProjects(): List<JiraProject> {
        return getDesktopProjects()
    }

    override fun addProject(project: JiraProject) {
        addDesktopProject(project)
    }
}