@file:OptIn(
    ExperimentalMaterialApi::class, ExperimentalResourceApi::class,
    ExperimentalComposeUiApi::class, ExperimentalComposeUiApi::class
)

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.LogCompositions
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import presentation.HomeUiState.Error
import presentation.HomeUiState.Loading
import presentation.HomeUiState.Success
import presentation.HomeViewModel
import screens.HomeErrorScreen

@Composable
fun MainScreen(
    viewModel: HomeViewModel,
    navigator: Navigator,
    updateAction: (model: VersionModel, index: Int) -> Unit,
    onVersionClickAction: (model: VersionModel) -> Unit,
) {

    val state by remember { viewModel.uiState }
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            LogCompositions("home top bar")
            TopAppBar(
                title = { Text("Jira Helper") },
                modifier = Modifier.fillMaxWidth(),
                navigationIcon = null,
                actions = {
                    Row {
                        Image(
                            painterResource("ic_autorenew_24.xml"),
                            "Refresh",
                            modifier = Modifier
                                .clickable { viewModel.refreshFixVersions() }
                                .padding(16.dp)
                        )
                        Image(
                            painterResource("ic_add_24.xml"),
                            "Create fix version",
                            modifier = Modifier
                                .clickable { navigator.navigate(Route.CreateFixVersion.path) }
                                .padding(16.dp)
                        )
                        Image(
                            painterResource("ic_settings.xml"),
                            "Settings",
                            modifier = Modifier
                                .clickable { navigator.navigate(Route.Settings.path) }
                                .padding(16.dp)
                        )
                    }
                },
                backgroundColor = Color.Blue,
                contentColor = Color.White
            )
        },
        content = {
            when (state) {
                Loading ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }

                is Success -> HomeContentScreen(
                    (state as Success).versions,
                    updateAction,
                    onVersionClickAction,
                )

                is Error -> HomeErrorScreen(navigator, state as Error)
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.getVersionList()
    }
}

@Composable
fun HomeContentScreen(
    fixVersions: List<VersionModel>,
    updateAction: (model: VersionModel, index: Int) -> Unit,
    onVersionClickAction: (model: VersionModel) -> Unit,
) {
    LogCompositions("Home content screen")
    val hovered = remember { mutableStateOf<VersionModel?>(null) }

    Column {
        Row(modifier = Modifier.background(Color(0x55cacaca)).padding(horizontal = 16.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp).fillMaxWidth(),
                    text = "Fix versions",
                    textAlign = TextAlign.Center,
                    style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                )
                if (fixVersions.isEmpty()) {
//                    todo show empty state
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(400.dp),

                        modifier = Modifier
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(fixVersions, key = { _, item -> item.name }) { index, type ->
                            VersionItem(
                                type,
                                index,
                                onEditClick = updateAction,
                                onVersionClickAction = onVersionClickAction,
                                isHovered = { hovered.value?.name == type.name },
                                onHoverEnter = {
                                    hovered.value = it
                                },
                                onHoverExit = {
                                    hovered.value = null
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VersionItem(
    model: VersionModel,
    index: Int,
    onEditClick: (model: VersionModel, index: Int) -> Unit,
    onVersionClickAction: (model: VersionModel) -> Unit,
    isHovered: (VersionModel) -> Boolean,
    onHoverEnter: (VersionModel) -> Unit,
    onHoverExit: (VersionModel) -> Unit,
) {
    LogCompositions("Home version item at index $index")
//    val derivedSize  by remember { derivedStateOf { if (isHovered(model)) 425.dp else 400.dp } }
//    val size by animateDpAsState(
//        targetValue = derivedSize, tween(
//            delayMillis = 200,
//            durationMillis = 300,
//            easing = LinearEasing
//        )
//    )
    val derivedPadding by remember { derivedStateOf { if (isHovered(model)) 8.dp else 0.dp } }
    val extraPadding by animateDpAsState(
        targetValue = derivedPadding, tween(
            durationMillis = 300,
            easing = LinearEasing
        )
    )
    val derivedFocus by remember { derivedStateOf { if (isHovered(model)) Color.Blue else Color.White } }
    val focusedColor by animateColorAsState(
        targetValue = derivedFocus,
        tween(
            durationMillis = 300,
            easing = LinearEasing
        )
    )

    Surface(
        modifier = Modifier,
        elevation = extraPadding,
        color = Color.Transparent,
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .drawBehind {
                    drawRoundRect(
                        focusedColor,
                        style = Stroke(width = 2.dp.toPx()),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val type = event.changes.last().type

                            if (type != PointerType.Mouse) return@awaitPointerEventScope

                            when (event.type) {
                                PointerEventType.Enter -> {
                                    onHoverEnter(model)
                                }

                                PointerEventType.Exit -> {
                                    onHoverExit(model)
                                }
                            }
                        }
                    }
                }
                .clickable { onVersionClickAction(model) },
        ) {
            Spacer(Modifier.height(0.dp))
            Row(modifier = Modifier.padding(PaddingValues(end = 8.dp, top = 8.dp))) {
                Text(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .weight(1f),
                    text = model.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = { onEditClick(model, index) }) {
                    Text(
                        modifier = Modifier
                            .padding(0.dp),
                        text = "Edit",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
            Chips(model.projects)
            Spacer(Modifier.height(0.dp))
        }
    }
}

@Composable
fun Chips(projects: List<VersionProjectModel>) {
    Row(modifier = Modifier.padding(PaddingValues(vertical = 4.dp))) {
        projects.forEach { project ->
            Chip(
                onClick = {},
                modifier = Modifier.wrapContentSize()
                    .padding(PaddingValues(horizontal = 4.dp)),
                enabled = project.selected,
                colors = ChipDefaults.chipColors(
                    Color.Blue.copy(alpha = 0.6f),
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