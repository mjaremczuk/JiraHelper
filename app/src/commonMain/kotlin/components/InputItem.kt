package components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun InputItem(
    initText: String = "",
    header: String,
    onTextUpdated: (String) -> Unit,
    keyboardType: () -> KeyboardType = { KeyboardType.Text },
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
            keyboardType = keyboardType(),
        ),
    )
}