@file:OptIn(ExperimentalResourceApi::class)

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import components.AnimatedTextField
import moe.tlaster.precompose.navigation.BackHandler
import moe.tlaster.precompose.navigation.Navigator
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import presentation.HomeViewModel

@Composable
fun EditFixVersionScreen(
    name: String,
    onBack: () -> Unit,
    onSave: (oldName: String, newName: String) -> Unit,
    onDelete: () -> Unit,
    navigator: Navigator,
    viewModel: HomeViewModel,
) {

    val text = remember { mutableStateOf(name) }
    val updatingDataState by viewModel.updated.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        BackHandler(onBack = onBack)

        TopAppBar(
            title = { Text("Edit $name") },
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = {
                Row {
                    Image(
                        painterResource("ic_arrow_back.xml"),
                        "Back button",
                        modifier = Modifier
                            .clickable { onBack() }
                            .padding(16.dp)
                    )
                }
            },
            actions = {
                Row {
                    Text(
                        "Save",
                        modifier = Modifier
                            .clickable { onSave(name, text.value) }
                            .padding(16.dp)
                    )
                }
            },
            backgroundColor = Color.Blue,
            contentColor = Color.White
        )
        Column(modifier = Modifier.padding(16.dp)) {
            AnimatedTextField(
                Modifier.fillMaxWidth(),
                text,
                null
            ) { updatingDataState }
        }
        Text("DANGER ZONE", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
        Text(
            "By removing fix version all issues \"fixVersion\" connected to it will be cleared",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Button(
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
            onClick = { onDelete() }
        ) {
            Text(
                modifier = Modifier
                    .padding(0.dp)
                    .background(Color.Red),
                text = "Remove fix version",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal
            )
        }

    }
    LaunchedEffect(updatingDataState) {
        if (updatingDataState) {
            viewModel.setViewUpdated()
            navigator.popBackStack()
        }
    }
}