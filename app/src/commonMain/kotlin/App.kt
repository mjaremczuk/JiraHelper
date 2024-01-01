@file:OptIn(
    ExperimentalMaterial3WindowSizeClassApi::class
)

import androidx.compose.material.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.query
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.koin.compose.KoinApplication
import presentation.HomeViewModel

@Composable
fun App() {
    val windowSizeClass = calculateWindowSizeClass()

    KoinApplication(application = {
        modules(koinModules() + commonModules)
    }) {
        PreComposeApp {
            MaterialTheme {
                Navigation(windowSizeClass)
            }
        }
    }
}

@Composable
fun Navigation(windowSizeClass: WindowSizeClass) {
    val navigator = rememberNavigator()
    val updatedVersion = remember { mutableStateOf<VersionModel?>(null) }
    val viewModel = koinViewModel(vmClass = HomeViewModel::class)

    NavHost(
        navigator = navigator,
        navTransition = NavTransition(),
        initialRoute = Route.Home.path,
    ) {
        scene(
            route = Route.Home.path,
            navTransition = NavTransition(),
        ) {
            MainScreen(
                viewModel = viewModel,
                navigator = navigator,
                updateAction = { model, itemIndex ->
                    updatedVersion.value = model
                    navigator.navigate(Route.EditFixVersion.withParam(itemIndex, model.name))
                }
            ) {
                navigator.navigate(Route.FixVersionTicket.withParam(it.name))
            }
        }
        scene(
            route = Route.EditFixVersion.path,
            navTransition = NavTransition()
        ) { backStackEntry ->

            val name: String =
                backStackEntry.query<String>(Route.EditFixVersion.NAME_PARAMETER).orEmpty()
            EditFixVersionScreen(
                name = name,
                onBack = { navigator.goBack() },
                onSave = { _, new ->
                    viewModel.updateVersion(updatedVersion.value, new)
                },
                onDelete = {
                    viewModel.remove(updatedVersion.value)
                },
                navigator,
                viewModel,
            )
        }
        scene(
            route = Route.CreateFixVersion.path,
            navTransition = NavTransition()
        ) {
            CreateFixVersion(
                windowSizeClass,
                navigator,
            )
        }
        scene(
            route = Route.FixVersionTicket.path,
            navTransition = NavTransition()
        ) { backStackEntry ->
            val name: String =
                backStackEntry.query<String>(Route.FixVersionTicket.VERSION_NAME_PARAMETER)
                    .orEmpty()
            FixVersionIssuesScreen(
                navigator,
                viewModel,
                name
            )
        }
        scene(
            route = Route.Settings.path,
            navTransition = NavTransition()
        ) {
            SettingsScreen(
                navigator
            )
        }
    }
}