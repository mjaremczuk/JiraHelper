@file:OptIn(ExperimentalResourceApi::class)

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import presentation.HomeViewModel

@Composable
fun FixVersionIssuesScreen(
    navigator: Navigator,
    viewModel: HomeViewModel,
    name: String
) {

    val clipboardManager = LocalClipboardManager.current
    val tickets = remember { viewModel.shareUiState }

    Column {
        TopAppBar(
            title = { Text("Tickets for fix version: $name") },
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
                        "Copy to clipboard",
                        modifier = Modifier.clickable {
                            val ticketItems = tickets.value as? HomeViewModel.SearchState.Success
                            ticketItems?.let {
                                clipboardManager.setText(
                                    AnnotatedString(it.tickets.fold("") { acc, text ->
                                        acc + "\n" + text
                                    })
                                )
                            }
                        }.padding(16.dp)
                    )
                }
            },
            backgroundColor = Color.Blue,
            contentColor = Color.White
        )

        when (val result = tickets.value) {
            HomeViewModel.SearchState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeViewModel.SearchState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    itemsIndexed(
                        result.tickets,
                        key = { index, _ -> index }) { _, ticket ->
                        Text(ticket, modifier = Modifier.padding(4.dp))
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.getTicketsFor(name)
    }
}