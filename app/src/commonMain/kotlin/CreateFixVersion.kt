@file:OptIn(ExperimentalResourceApi::class, ExperimentalMaterialApi::class)

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.AnimatedTextField
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import presentation.CreateVersionViewModel

@Composable
fun CreateFixVersion(
    windowSizeClass: WindowSizeClass,
    navigator: Navigator,
    viewModel: CreateVersionViewModel = koinViewModel(vmClass = CreateVersionViewModel::class)
) {

    val versionCreated by viewModel.versionCreated.collectAsState()
    val showProgress by viewModel.showProgress.collectAsState()
    val projects by rememberSaveable { viewModel.projects }

    val fixVersionName = remember { mutableStateOf("") }

    val createdVersionsHistory by rememberSaveable { viewModel.localVersionsHistory }
    val errorMessage by rememberSaveable { viewModel.errorMessage }

    Column {
        TopAppBar(
            title = { Text("Add new fix version") },
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
                    if (showProgress.not()) {
                        Text(
                            "Save",
                            modifier = Modifier.clickable {
                                viewModel.createFixVersion(
                                    fixVersionName.value,
                                    projects.filter { it.selected },
                                )
                            }.padding(16.dp)
                        )
                    }
                }
            },
            backgroundColor = Color.Blue,
            contentColor = Color.White
        )
        AnimatedVisibility(
            visible = errorMessage.isNotEmpty(),
            enter = slideInVertically {
                // Slide in from 40 dp from the top.
                -40.dp.value.toInt()
            } + expandVertically(
                expandFrom = Alignment.Top
            ) + fadeIn(
                initialAlpha = 0.3f
            ),
            exit = slideOutVertically() + shrinkVertically() + fadeOut()
        ) {
            Text(
                errorMessage,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .padding(16.dp),
                color = Color.White
            )
        }

        when (getPlatform().type) {
            Type.ANDROID_MOBILE,
            Type.IOS -> {
                when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Compact -> CreateVersionCompact(
                        navigator,
                        viewModel,
                        fixVersionName,
                        { showProgress },
                        { projects },
                        { createdVersionsHistory },
                    )

                    WindowWidthSizeClass.Medium,
                    WindowWidthSizeClass.Expanded -> {
                        CreateVersionTwoPane(
                            navigator,
                            viewModel,
                            fixVersionName,
                            { showProgress },
                            { projects },
                            { createdVersionsHistory },
                        )
                    }
                }
            }

            Type.DESKTOP -> CreateVersionTwoPane(
                navigator,
                viewModel,
                fixVersionName,
                { showProgress },
                { projects },
                { createdVersionsHistory })
        }
    }
    LaunchedEffect(projects) {
        if (versionCreated) {
            fixVersionName.value = ""
            viewModel.setVersionCreated()
        }
    }
}

@Composable
fun ProjectItem(
    model: ProjectModel,
    preselected: Boolean,
    onCheckBoxAction: (selected: Boolean) -> Unit
) {

    val checked = remember { mutableStateOf(model.selected) }

    val bgColor: Color by animateColorAsState(if (checked.value) Color.Blue else Color.White)
    val textColor: Color by animateColorAsState(if (checked.value) Color.White else Color.Black)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable {
                checked.value = checked.value.not()
                onCheckBoxAction(checked.value)
            }
            .drawBehind {
                drawRect(bgColor)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked.value,
            onCheckedChange = onCheckBoxAction,
            modifier = Modifier.padding(vertical = 4.dp)
        )
        Box(
            modifier = Modifier.weight(1f, fill = false),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                "${model.key} (${model.name})",
                modifier = Modifier.padding(start = 4.dp, end = 16.dp),
                color = textColor,
            )
        }
    }
    LaunchedEffect(model) {
        if (preselected.not()) {
            checked.value = false
        }
    }
}


@Composable
fun HistoryVersionItem(
    name: String,
    projects: List<ProjectModel>,
) {
    Column(
        modifier = Modifier
            .padding(2.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.LightGray)
            .clickable { }
            .padding(start = 8.dp, end = 8.dp)
    ) {
        Row(modifier = Modifier.padding(PaddingValues(end = 8.dp))) {
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .weight(1f),
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        HistoryChip(projects)
    }
}

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


@Composable
fun CreateVersionTwoPane(
    navigator: Navigator,
    viewModel: CreateVersionViewModel,
    fixVersionName: MutableState<String>,
    showProgress: () -> Boolean,
    projects: () -> List<ProjectModel>,
    createdVersionsHistory: () -> List<Pair<String, List<ProjectModel>>>,
) {
    Row(modifier = Modifier.padding(16.dp)) {
        ProjectList(viewModel, projects)

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            AnimatedTextField(
                Modifier,
                fixVersionName,
                { Text("Enter fix version name") },
                showProgress,
            )
            Text(
                "Created fix versions local history:",
                modifier = Modifier.padding(vertical = 16.dp)
            )
            LazyColumn() {
                itemsIndexed(
                    createdVersionsHistory(),
                    key = { index, _ -> index }) { _, item ->
                    HistoryVersionItem(item.first, item.second)
                }
            }
        }
    }
}

@Composable
fun CreateVersionCompact(
    navigator: Navigator,
    viewModel: CreateVersionViewModel,
    fixVersionName: MutableState<String>,
    showProgress: () -> Boolean,
    projects: () -> List<ProjectModel>,
    createdVersionsHistory: () -> List<Pair<String, List<ProjectModel>>>,
) {
    Column(modifier = Modifier.padding(16.dp)) {
        AnimatedTextField(
            Modifier.fillMaxWidth(),
            fixVersionName,
            { Text("Enter fix version name") },
            showProgress,
        )

        Text(
            "Select projects",
            color = Color.DarkGray,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 8.dp)
        )
        ProjectList(viewModel, projects)
    }
}

@Composable
fun ProjectList(
    viewModel: CreateVersionViewModel,
    projects: () -> List<ProjectModel>
) {
    val listState = rememberScrollState()
    Column(
        modifier = Modifier
            .width(IntrinsicSize.Max)
            .verticalScroll(listState),
    ) {
        projects().forEach { type ->
            ProjectItem(
                type,
                type.selected,
                onCheckBoxAction = { selected ->
                    viewModel.updateProjectSelection(type.copy(selected = selected))
                }
            )
        }
    }
}


