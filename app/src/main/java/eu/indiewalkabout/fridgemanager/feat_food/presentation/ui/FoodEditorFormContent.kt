package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.components.RoundedCornerButton
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.backgroundLightGrey
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorHintText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.lightGreen
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.lightGreyVeryTransparent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.SimpleTextField

@Composable
fun FoodEditorFormContent(
    titleResId: Int,
    state: FoodEditorUiState,
    isListening: Boolean,
    rmsDb: Float,
    onDateClick: () -> Unit,
    onDescriptionChange: (String) -> Unit,
    onMicClick: () -> Unit,
    onQuantityChange: (String) -> Unit,
    onQuantityPickerClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(RoundedCornerShape(24.dp))
            .background(
                primaryColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.food_background_half),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.5f
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    primaryColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp),
        ) {
            TopBar(
                title = stringResource(titleResId),
                paddingTop = 16.dp,
                paddingBottom = 16.dp,
                backgroundColor = Color.Transparent,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(R.string.insert_expiring_date_label),
                    style = text_16(colorText, true),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                SimpleTextField(
                    enabled = false,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    hintText = stringResource(R.string.insert_expiring_date_hint),
                    hintTextStyle = text_14(colorHintText, false),
                    inputTextStyle = text_14(colorText, false),
                    value = state.expiringAtText,
                    onValueChange = {}
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_calendar_month),
                    contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 16.dp)
                        .clickable(onClick = onDateClick)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(R.string.insert_food_description_hint),
                    style = text_16(colorText, true),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                SimpleTextField(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    hintText = stringResource(R.string.insert_food_description_hint),
                    hintTextStyle = text_14(colorHintText, false),
                    inputTextStyle = text_14(colorText, false),
                    value = state.description,
                    onValueChange = onDescriptionChange,
                )

                val micColor by animateColorAsState(
                    targetValue = if (isListening) lightGreen else secondaryColor,
                    animationSpec = tween(durationMillis = 500),
                    label = "micColor"
                )

                val animatedMicScale by animateFloatAsState(
                    targetValue = if (isListening) 1f + (rmsDb / 10f).coerceIn(0f, 0.5f) else 1f,
                    animationSpec = tween(durationMillis = 500),
                    label = "micScale"
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 8.dp)
                        .size(40.dp)
                        .graphicsLayer {
                            scaleX = animatedMicScale
                            scaleY = animatedMicScale
                        }
                        .background(
                            color = micColor.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .clickable(onClick = onMicClick)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_mic_white),
                        contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                        tint = micColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp, 16.dp, 0.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = stringResource(R.string.insert_quantity_label),
                    style = text_16(colorText, true),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                SimpleTextField(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    hintText = stringResource(R.string.insert_quantity_label),
                    hintTextStyle = text_14(colorHintText, false),
                    inputTextStyle = text_14(colorText, false),
                    isNumeric = true,
                    isDecimal = false,
                    value = state.quantityText,
                    onValueChange = onQuantityChange,
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_quantity_add),
                    contentDescription = stringResource(R.string.insert_quantity_icon_description),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(horizontal = 16.dp)
                        .clickable(onClick = onQuantityPickerClick)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            RoundedCornerButton(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally),
                onClick = {
                    if (!state.canSubmit) return@RoundedCornerButton
                    onSaveClick()
                },
                shape = RoundedCornerShape(15.dp),
                elevation = 0,
                borderStroke = BorderStroke(1.dp, secondaryColor),
                backgroundColor = if (state.canSubmit) backgroundLightGrey else lightGreyVeryTransparent,
                text = stringResource(R.string.generic_save_label),
                textPadding = 0,
                style = text_14(Color.White, true)
            )
        }
    }
}
