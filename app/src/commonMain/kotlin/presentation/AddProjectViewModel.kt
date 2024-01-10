package presentation

import ProjectModel
import androidx.compose.runtime.mutableStateOf
import api.DispatchersProvider
import api.JiraProjectApi
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class AddProjectViewModel(
    private val projectApi: JiraProjectApi,
    private val dispatchersProvider: DispatchersProvider,
) : ViewModel() {

    var existingProjects = mutableStateOf<List<ProjectModel>>(emptyList())
        private set

    var projectToAdd: JiraProject? = null

    init {
        viewModelScope.launch(dispatchersProvider.getIoDispatcher()) {
            getAddedProjects()
        }
    }

    fun addProjectKey(key: String) {
        projectToAdd?.let { projectToAdd = it.copy(key = key) }
            ?: createDefaultProject(key = key)
    }

    fun addProjectId(id: String) {
        projectToAdd?.let { projectToAdd = it.copy(id = id.toInt()) }
            ?: createDefaultProject(id = id.toInt())
    }

    fun addProjectName(name: String) {
        projectToAdd?.let { projectToAdd = it.copy(name = name) }
            ?: createDefaultProject(name = name)
    }

    fun addProject() {
        viewModelScope.launch(dispatchersProvider.getIoDispatcher()) {
            projectToAdd?.let { projectApi.addProject(it) }
            projectToAdd = null
            getAddedProjects()
        }
    }

    private suspend fun getAddedProjects() {
        val projects = projectApi.getProjects().map {
            ProjectModel(
                name = it.name,
                key = it.key,
                id = it.id,
                selected = it.selected,
            )
        }
        viewModelScope.launch(dispatchersProvider.getMainDispatcher()) {
            existingProjects.value = projects
        }
    }

    private fun createDefaultProject(name: String = "", key: String = "", id: Int = -1) {
        projectToAdd = JiraProject(
            name = name,
            key = key,
            id = id,
        )
    }
}