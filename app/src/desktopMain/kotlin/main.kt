import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.jetbrains.exposed.sql.Database
import java.io.File

fun main() = application {
    val dbPath = File("helper-db").absolutePath
    Database.connect("jdbc:h2:file:$dbPath", driver = "org.h2.Driver")
    val windowState = rememberWindowState()

    Window(
        title = "Jira Helper",
        onCloseRequest = ::exitApplication,
        state = windowState,
    ) {
        App()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}