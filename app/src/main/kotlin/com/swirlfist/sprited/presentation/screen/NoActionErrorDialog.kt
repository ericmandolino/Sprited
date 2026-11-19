package com.swirlfist.sprited.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.swirlfist.sprited.presentation.section.ConfirmButtonDialogFooter
import com.swirlfist.sprited.presentation.theme.SpritedTheme

@Composable
fun NoActionErrorDialog(
    errorMessage: String,
    errorTitle: String? = null,
    dismissButtonText: String? = null,
    onDismiss: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                errorTitle?.let { title ->
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                )
                dismissButtonText?.let { dismissText ->
                    TextButton(
                        onClick = onDismiss,
                    ) {
                        Text(
                            text = dismissText,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                ConfirmButtonDialogFooter(
                    confirmText = stringResource(android.R.string.ok),
                    onConfirmClick = onDismiss,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun NoActionErrorDialogNoTitlePreview() {
    SpritedTheme {
        NoActionErrorDialog(
            errorMessage = "Some error message",
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun NoActionErrorDialogTitlePreview() {
    SpritedTheme {
        NoActionErrorDialog(
            errorMessage = "Some error message",
            errorTitle = "Error title",
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun NoActionErrorDialogTitleAndDismissButtonPreview() {
    SpritedTheme {
        NoActionErrorDialog(
            errorMessage = "Some error message",
            errorTitle = "Error title",
        )
    }
}
