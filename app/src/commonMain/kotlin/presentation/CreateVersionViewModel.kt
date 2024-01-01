package presentation

import ProjectModel
import androidx.compose.runtime.mutableStateOf
import api.JiraApi
import api.JiraProjectApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class CreateVersionViewModel(
    private val jiraApi: JiraApi,
    private val jiraProjectApi: JiraProjectApi,
) : ViewModel() {

    val versionCreated = MutableStateFlow(false)
    val showProgress = MutableStateFlow(false)
    var localVersionsHistory = mutableStateOf<List<Pair<String, List<ProjectModel>>>>(emptyList())
    var errorMessage = mutableStateOf("")

    var projects = mutableStateOf<List<ProjectModel>>(emptyList())

    init {
        viewModelScope.launch {
            projects.value = jiraProjectApi.getProjects().map {
                ProjectModel(
                    name = it.name,
                    key = it.key,
                    id = it.id.toInt(),
                    selected = false,
                )
            }.sortedBy { it.id }
        }
    }

    fun setVersionCreated() {
        versionCreated.value = true
    }

    fun updateProjectSelection(projectModel: ProjectModel) {
        val new = projects.value.filter { it.id != projectModel.id }
        projects.value = new.plus(projectModel).sortedBy { it.id }
    }

    fun createFixVersion(fixVersionName: String, selectedProjects: List<ProjectModel>) {
        if (fixVersionName.isBlank()) {
            errorMessage.value = "Fix version name can't be empty"
            return
        }
        if (selectedProjects.isEmpty()) {
            errorMessage.value = "You have to select at least 1 project to add fix version"
            return
        }
        viewModelScope.launch {
            errorMessage.value = ""
            showProgress.value = true
            println("Creating fix version with name: $fixVersionName for $selectedProjects")
            try {
                delay(3000)
//                jiraApi.createFixVersion(fixVersionName, selectedProjects.map { it.id })
                localVersionsHistory.value =
                    localVersionsHistory.value.plus(fixVersionName to selectedProjects)
                versionCreated.value = true
                projects.value = projects.value.map { it.copy(selected = false) }
            } catch (ex: Exception) {
                errorMessage.value = ex.message.orEmpty()
            } finally {
                showProgress.value = false
            }
        }
    }
}