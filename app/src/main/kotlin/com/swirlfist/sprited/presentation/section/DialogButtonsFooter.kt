package com.swirlfist.sprited.presentation.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swirlfist.sprited.presentation.theme.SpritedTheme

@Composable
fun ActionCancelButtonsDialogFooter(
    modifier: Modifier = Modifier,
    state: ActionCancelButtonsDialogFooterState,
) {
    BaseDialogButtonsFooter(
        modifier = modifier,
        state = state.toBaseDialogButtonsFooterState(),
    )
}

@Composable
fun ConfirmButtonDialogFooter(
    modifier: Modifier = Modifier,
    state: ConfirmButtonDialogFooterState,
) {
    BaseDialogButtonsFooter(
        modifier = modifier,
        state = state.toBaseDialogButtonsFooterState(),
    )
}

@Composable
fun CancelButtonDialogFooter(modifier: Modifier = Modifier, state: CancelButtonDialogFooterState) {
    BaseDialogButtonsFooter(
        modifier = modifier,
        state = state.toBaseDialogButtonsFooterState(),
    )
}

@Composable
fun BaseDialogButtonsFooter(modifier: Modifier = Modifier, state: BaseDialogButtonsFooterState) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            8.dp,
            Alignment.End,
        ),
    ) {
        TextButton(
            onClick = state.onLeftClick,
        ) {
            Text(
                text = state.leftText,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
            )
        }
        state.rightText?.let { text ->
            TextButton(
                enabled = state.isRightEnabled,
                onClick = state.onRightClick,
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionCancelButtonsDialogFooterPreview() {
    SpritedTheme {
        ActionCancelButtonsDialogFooter(
            state = ActionCancelButtonsDialogFooterState(
                isActionEnabled = true,
                actionText = "Save",
                cancelText = "Cancel",
                onActionClick = {},
                onCancelClick = {},
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmButtonDialogFooterPreview() {
    SpritedTheme {
        ConfirmButtonDialogFooter(
            state = ConfirmButtonDialogFooterState(
                confirmText = "Ok",
                onConfirmClick = {},
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CancelButtonDialogFooterPreview() {
    SpritedTheme {
        CancelButtonDialogFooter(
            state = CancelButtonDialogFooterState(
                onCancelClick = {},
            ),
        )
    }
}

data class BaseDialogButtonsFooterState(
    val leftText: String,
    val onLeftClick: () -> Unit,
    val isRightEnabled: Boolean = true,
    val rightText: String? = null,
    val onRightClick: () -> Unit = {},
)

data class ActionCancelButtonsDialogFooterState(
    val isActionEnabled: Boolean,
    val actionText: String,
    val cancelText: String,
    val onActionClick: () -> Unit,
    val onCancelClick: () -> Unit,
)

data class CancelButtonDialogFooterState(val onCancelClick: () -> Unit)

data class ConfirmButtonDialogFooterState(val confirmText: String, val onConfirmClick: () -> Unit)

private fun ActionCancelButtonsDialogFooterState.toBaseDialogButtonsFooterState() =
    BaseDialogButtonsFooterState(
        leftText = cancelText,
        onLeftClick = onCancelClick,
        isRightEnabled = isActionEnabled,
        rightText = actionText,
        onRightClick = onActionClick,
    )

private fun ConfirmButtonDialogFooterState.toBaseDialogButtonsFooterState() =
    BaseDialogButtonsFooterState(
        leftText = confirmText,
        onLeftClick = onConfirmClick,
    )

@Composable
private fun CancelButtonDialogFooterState.toBaseDialogButtonsFooterState() =
    BaseDialogButtonsFooterState(
        leftText = stringResource(android.R.string.cancel),
        onLeftClick = onCancelClick,
    )
