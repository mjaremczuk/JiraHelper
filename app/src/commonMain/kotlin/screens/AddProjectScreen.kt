package screens

import ProjectModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.InputItem
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import presentation.AddProjectViewModel


@OptIn(ExperimentalResourceApi::class)
@Composable
fun AddProjectScreen(
    navigator: Navigator,
    viewModel: AddProjectViewModel = koinViewModel(vmClass = AddProjectViewModel::class)
) {

    val projects = remember { viewModel.existingProjects }
    Column {
        TopAppBar(
            title = { Text("Add project") },
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = {
                Row {
                    Image(
                        painterResource("ic_arrow_back.xml"),
                        "Back button",
                        modifier = Modifier.clickable { navigator.goBack() }
                            .padding(16.dp)
                    )
                }
            },
            actions = {
                Row {
                    Text(
                        "Add",
                        modifier = Modifier
                            .clickable {
                                viewModel.addProject()
                            }
                            .padding(16.dp)
                    )
                }
            },
            backgroundColor = Color.Blue,
            contentColor = Color.White
        )
        AddProjectContent(
            alreadyAddedProjects = projects.value,
            onProjectKeyUpdated = { viewModel.addProjectKey(it) },
            onProjectIdUpdated = { viewModel.addProjectId(it) },
            onProjectNameUpdated = { viewModel.addProjectName(it) },
        )
    }
}

@Composable
fun AddProjectContent(
    alreadyAddedProjects: List<ProjectModel>,
    onProjectKeyUpdated: (String) -> Unit,
    onProjectIdUpdated: (String) -> Unit,
    onProjectNameUpdated: (String) -> Unit,
) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        HistoryChip(alreadyAddedProjects)
        Spacer(Modifier.height(16.dp))
        Spacer(Modifier.height(16.dp))
        InputItem(
            header = "Enter project key",
            onTextUpdated = onProjectKeyUpdated,
        )
        Spacer(Modifier.height(16.dp))
        InputItem(
            header = "Enter project Id",
            onTextUpdated = onProjectIdUpdated,
            keyboardType = { KeyboardType.Number },
        )
        Spacer(Modifier.height(16.dp))
        InputItem(
            header = "Optional, add project name",
            onTextUpdated = onProjectNameUpdated,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HistoryChip(projects: List<ProjectModel>) {
    val listState = rememberScrollState()

    Row(
        modifier = Modifier
            .padding(PaddingValues(vertical = 4.dp))
            .horizontalScroll(listState)
    ) {
        projects.forEach { project ->
            Chip(
                onClick = {},
                modifier = Modifier
                    .wrapContentSize()
                    .padding(PaddingValues(horizontal = 4.dp)),
                enabled = true,
                colors = ChipDefaults.chipColors(
                    Color.Blue,
                    disabledBackgroundColor = Color.LightGray,
                    contentColor = Color.White,
                    disabledContentColor = Color.Black
                )
            ) {
                Text(text = project.key, fontSize = 12.sp)
            }
        }
    }
}