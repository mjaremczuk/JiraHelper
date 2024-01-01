import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.exposed.sql.Database
import java.io.File

fun main() = application {
    val dbPath = File("helper-db").absolutePath
    Database.connect("jdbc:h2:file:$dbPath", driver = "org.h2.Driver")
    Window(
        title = "Jira Helper",
        onCloseRequest = ::exitApplication
    ) {
        App()
    }
}

@Preview
@Composable
fun AppDesktopPreview() {
    App()
}