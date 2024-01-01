package components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedTextField(
    modifier: Modifier = Modifier,
    fixVersionName: MutableState<String>?,
    label: @Composable (() -> Unit)?,
    showProgress: () -> Boolean,
) {
    AnimatedVisibility(showProgress().not()) {
        TextField(
            modifier = modifier,
            value = fixVersionName?.value.orEmpty(),
            label = label,
            onValueChange = { fixVersionName?.value = it },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Ascii,
            ),
        )
    }
    if (showProgress()) {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    }
}