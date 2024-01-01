package screens

import Route
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.tlaster.precompose.navigation.Navigator
import presentation.HomeUiState

@Composable
fun HomeErrorScreen(
    navigator: Navigator,
    error: HomeUiState.Error,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(error.message)
            TextButton(
                onClick = {
                    navigator.navigate(Route.Settings.path)
                    error.action()
                },
                content = {
                    Text(error.ctaLabel)
                }
            )
        }
    }
}