package com.swirlfist.sprited.presentation.uielements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun IconButton(modifier: Modifier = Modifier, state: IconButtonState) {
    FilledIconButton(
        modifier = modifier
            .size(state.size),
        onClick = state.onClick,
        shape = RoundedCornerShape(4.dp),
        enabled = state.isEnabled,
    ) {
        Icon(
            painter = painterResource(state.drawableResId),
            contentDescription = stringResource(state.contentDescriptionResId),
        )
    }
}

data class IconButtonState(
    @DrawableRes val drawableResId: Int,
    @StringRes val contentDescriptionResId: Int,
    val size: Dp,
    val isEnabled: Boolean,
    val onClick: () -> Unit,
)
