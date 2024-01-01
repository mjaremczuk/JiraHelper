@file:OptIn(ExperimentalResourceApi::class)

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import api.data.Credentials
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import presentation.SettingsViewModel

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SettingsScreen(
    navigator: Navigator,
    viewModel: SettingsViewModel = koinViewModel(vmClass = SettingsViewModel::class)
) {
    val clipboardManager = LocalClipboardManager.current

    Column {
        TopAppBar(
            title = { Text("Settings") },
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
                        "Save",
                        modifier = Modifier
                            .clickable {
                                viewModel.saveChanges()
                                navigator.goBack()
                            }
                            .padding(16.dp)
                    )
                }
            },
            backgroundColor = Color.Blue,
            contentColor = Color.White
        )
        SettingsContent(
            credentials = viewModel.credentials,
            onEmailUpdated = { viewModel.updateUserName(it) },
            onTokenUpdated = { viewModel.updateToken(it) },
            onBaseUrlUpdated = { viewModel.updateBaseUrl(it) },
            onCopyToClipboard = {
                clipboardManager.setText(it)
            }
        )
    }
}

@Composable
fun SettingsContent(
    credentials: Credentials?,
    onEmailUpdated: (String) -> Unit,
    onTokenUpdated: (String) -> Unit,
    onBaseUrlUpdated: (String) -> Unit,
    onCopyToClipboard: (AnnotatedString) -> Unit
) {
    val listState = rememberScrollState()
    Column(
        modifier = Modifier.padding(16.dp)
            .fillMaxWidth()
            .verticalScroll(listState),
    ) {
        SettingItem(
            credentials?.username.orEmpty(),
            "User name used to authorize requests",
            onEmailUpdated
        )
        Spacer(Modifier.height(16.dp))

        SettingItem(
            credentials?.token.orEmpty(),
            "Token used to authorize requests",
            onTokenUpdated
        )
        ClickableUrl(onCopyToClipboard)
        Spacer(Modifier.height(16.dp))

        SettingItem(
            credentials?.baseUrl.orEmpty(),
            "Tickets base url",
            onBaseUrlUpdated
        )
    }
}

@Composable
fun SettingItem(
    initText: String,
    header: String,
    onTextUpdated: (String) -> Unit,
) {
    val name = remember { mutableStateOf(initText) }

    Text(header)
    TextField(
        value = name.value,
        label = null,
        onValueChange = {
            name.value = it
            onTextUpdated(it)
        },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrect = false,
            keyboardType = KeyboardType.Ascii,
        ),
    )
}

@Composable
fun ClickableUrl(onCopy: (AnnotatedString) -> Unit) {
    val text = buildAnnotatedString {
        append("To get your own token ")
        pushStringAnnotation(
            tag = "URL",
            annotation = "https://support.atlassian.com/atlassian-account/docs/manage-api-tokens-for-your-atlassian-account/"
        )
        withStyle(
            style = SpanStyle(
                color = Color.Blue, fontWeight = FontWeight.Bold
            )
        ) {
            append("copy this link")
        }
        append(" to clipboard")
        appendLine()
        append("or copy below link")
        pop()
    }
    ClickableText(text, onClick = {
        text.getStringAnnotations(
            tag = "URL", start = it, end = it
        ).firstOrNull()?.let { annotation ->
            onCopy(AnnotatedString(annotation.item))
        }
    })

    SelectionContainer {
        Text(
            "https://support.atlassian.com/atlassian-account/docs/manage-api-tokens-for-your-atlassian-account/",
            color = Color.Blue,
            fontSize = 12.sp
        )
    }
}