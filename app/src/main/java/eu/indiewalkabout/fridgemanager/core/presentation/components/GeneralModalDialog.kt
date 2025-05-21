package eu.indiewalkabout.fridgemanager.core.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.backgroundLightGreen
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.backgroundLightGrey
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorAccent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorHintText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.fabYellow
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodOrange
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColorDark
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColorSemitransparent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_15
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_20


@Composable
fun GeneralModalDialog(
    modifier: Modifier = Modifier,
    backgroundColor: Color = primaryColor,
    image: Int? = null,
    title: String,
    titleStyle: TextStyle,
    message: String,
    messageStyle: TextStyle,
    cancelable: Boolean = true,
    leftButtonLabel: String? = null,  // if one of the label is null, it set the dialog with only 1 btn at centre
    leftButtonTextStyle: TextStyle = TextStyle.Default,
    leftButtonBackgroundColor: Color = Color.Gray,
    rightButtonLabel: String? = null,
    rightButtonTextStyle: TextStyle = TextStyle.Default,
    rightButtonBackgroundColor: Color = Color.Blue,
    buttonCornerRadius: Dp = 10.dp,
    buttonDistanceFromMessage: Dp = 16.dp,
    buttonStrokeWidth: Dp = 0.dp,
    buttonStrokeColor: Color = Color.Transparent,
    isHorizontal: Boolean = true, // buttons arrangement: horizontal (default) or vertical.
    onLeftButtonAction: (() -> Unit)? = null,
    onRightButtonAction: (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = { if (cancelable) onLeftButtonAction?.invoke() },
        properties = DialogProperties(
            dismissOnBackPress = cancelable,
            dismissOnClickOutside = cancelable
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(1.dp, secondaryColor, RoundedCornerShape(buttonCornerRadius)),
            shape = RoundedCornerShape(buttonCornerRadius)
        ) {
            ConstraintLayout(
                modifier = modifier
                    .fillMaxWidth()
                    .background(backgroundColor)
                    .padding(16.dp)
            ) {
                val (
                    imageRef,
                    titleRef,
                    messageRef,
                    leftButtonRef,
                    rightButtonRef
                ) = createRefs()


                if (image != null) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = null,
                        modifier = Modifier
                            .constrainAs(imageRef) {
                                top.linkTo(parent.top, 32.dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                            .size(48.dp)
                    )
                }

                Text(
                    text = title,
                    textAlign = TextAlign.Center,
                    style = titleStyle,
                    modifier = Modifier.constrainAs(titleRef) {
                        if (image != null) {
                            top.linkTo(imageRef.bottom, 32.dp)
                        } else {
                            top.linkTo(parent.top, 32.dp)
                        }
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )

                Text(
                    text = message,
                    style = messageStyle,
                    modifier = Modifier.constrainAs(messageRef) {
                        top.linkTo(titleRef.bottom, 8.dp)
                        start.linkTo(parent.start, 8.dp)
                        end.linkTo(parent.end, 8.dp)
                        width = Dimension.fillToConstraints
                    },
                    textAlign = TextAlign.Center
                )

                if (leftButtonLabel != null && rightButtonLabel != null) {
                    if (isHorizontal) {

                        Button(
                            onClick = { onLeftButtonAction?.invoke() },
                            modifier = Modifier
                                .constrainAs(leftButtonRef) {
                                    top.linkTo(messageRef.bottom, buttonDistanceFromMessage)
                                    start.linkTo(parent.start)
                                    end.linkTo(rightButtonRef.start, 8.dp)
                                    width = Dimension.fillToConstraints
                                },
                            border = BorderStroke(buttonStrokeWidth, buttonStrokeColor),
                            shape = RoundedCornerShape(buttonCornerRadius),
                            colors = ButtonDefaults.buttonColors(containerColor = leftButtonBackgroundColor)
                        ) {
                            Text(text = leftButtonLabel, style = leftButtonTextStyle)
                        }


                        Button(
                            onClick = { onRightButtonAction?.invoke() },
                            modifier = Modifier
                                .constrainAs(rightButtonRef) {
                                    top.linkTo(messageRef.bottom, buttonDistanceFromMessage)
                                    start.linkTo(leftButtonRef.end, 0.dp)
                                    end.linkTo(parent.end)
                                    width = Dimension.fillToConstraints
                                },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            border = BorderStroke(buttonStrokeWidth, buttonStrokeColor),
                            shape = RoundedCornerShape(buttonCornerRadius),
                            colors = ButtonDefaults.buttonColors(containerColor = rightButtonBackgroundColor)
                        ) {
                            Text(text = rightButtonLabel, style = rightButtonTextStyle)
                        }


                    } else {
                        Button(
                            onClick = { onRightButtonAction?.invoke() },
                            modifier = Modifier
                                .constrainAs(rightButtonRef) {
                                    top.linkTo(messageRef.bottom, buttonDistanceFromMessage)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    width = Dimension.fillToConstraints
                                },
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(buttonCornerRadius),
                            border = BorderStroke(buttonStrokeWidth, buttonStrokeColor),
                            colors = ButtonDefaults.buttonColors(containerColor = rightButtonBackgroundColor)
                        ) {
                            Text(text = rightButtonLabel, style = rightButtonTextStyle)
                        }

                        Button(
                            onClick = { onLeftButtonAction?.invoke() },
                            modifier = Modifier
                                .constrainAs(leftButtonRef) {
                                    top.linkTo(rightButtonRef.bottom,0.dp)
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    width = Dimension.fillToConstraints
                                },
                            shape = RoundedCornerShape(buttonCornerRadius),
                            border = BorderStroke(buttonStrokeWidth, buttonStrokeColor),
                            colors = ButtonDefaults.buttonColors(containerColor = leftButtonBackgroundColor)
                        ) {
                            Text(text = leftButtonLabel, style = leftButtonTextStyle)
                        }
                    }
                } else if (leftButtonLabel != null) {
                    Button(
                        onClick = { onLeftButtonAction?.invoke() },
                        modifier = Modifier
                            .constrainAs(leftButtonRef) {
                                top.linkTo(messageRef.bottom, buttonDistanceFromMessage)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                            },
                        shape = RoundedCornerShape(buttonCornerRadius),
                        border = BorderStroke(buttonStrokeWidth, buttonStrokeColor),
                        colors = ButtonDefaults.buttonColors(containerColor = leftButtonBackgroundColor)
                    ) {
                        Text(text = leftButtonLabel, style = leftButtonTextStyle)
                    }

                } else if (rightButtonLabel != null) {
                    Button(
                        onClick = { onRightButtonAction?.invoke() },
                        modifier = Modifier
                            .constrainAs(rightButtonRef) {
                                top.linkTo(messageRef.bottom, buttonDistanceFromMessage)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                width = Dimension.fillToConstraints
                            },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        border = BorderStroke(buttonStrokeWidth, buttonStrokeColor),
                        shape = RoundedCornerShape(buttonCornerRadius),
                        colors = ButtonDefaults.buttonColors(containerColor = rightButtonBackgroundColor)
                    ) {
                        Text(text = rightButtonLabel, style = rightButtonTextStyle)
                    }
                }


            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GeneralModalDialogPreview() {
    GeneralModalDialog(
        title = "Confirmation",
        titleStyle = text_20(colorText, true),
        message = "Are you sure you want to proceed?",
        messageStyle = text_16(colorText, false),
        image = R.drawable.food_right,
        leftButtonLabel = "Cancel",
        rightButtonLabel = "OK",
        buttonDistanceFromMessage = 64.dp,
        onLeftButtonAction = { /* Handle cancel action */ },
        onRightButtonAction = { /* Handle OK action */ }
    )
}

@Preview(showBackground = true)
@Composable
fun GeneralModalDialog_BtnsVertical_Preview() {
    GeneralModalDialog(
        title = "Confirmation",
        titleStyle = text_20(colorText, true),
        message = "Are you sure you want to proceed?",
        messageStyle = text_16(colorText, false),
        image = R.drawable.ic_flower_white,
        isHorizontal = false,
        leftButtonLabel = "Cancel",
        rightButtonLabel = "OK",
        buttonDistanceFromMessage = 64.dp,
        onLeftButtonAction = { /* Handle cancel action */ },
        onRightButtonAction = { /* Handle OK action */ }
    )
}

@Preview(showBackground = true)
@Composable
fun GeneralModalDialog_no_image_Preview() {
    GeneralModalDialog(
        backgroundColor = primaryColorSemitransparent,
        title = "Confirmation",
        titleStyle = text_20(colorText, true),
        message = "Are you sure you want to proceed?",
        messageStyle = text_16(colorText, false),
        image = null,
        leftButtonLabel = "Cancel",
        // leftButtonBackgroundColor = colorHintText,
        rightButtonLabel = "OK",
        rightButtonBackgroundColor = primaryColorDark,
        buttonCornerRadius = 5.dp,
        buttonDistanceFromMessage = 32.dp,
        buttonStrokeWidth = 1.dp,
        buttonStrokeColor = secondaryColor,
        onLeftButtonAction = { /* Handle cancel action */ },
        onRightButtonAction = { /* Handle OK action */ }
    )
}


@Preview(showBackground = true)
@Composable
fun GeneralModalDialog_1_btn_Preview() {
    GeneralModalDialog(
        title = "Confirmation",
        titleStyle = text_20(colorText, true),
        message = "Are you sure you want to proceed?",
        messageStyle = text_16(colorText, false),
        image = null,
        // leftButtonLabel = "Cancel",
        rightButtonLabel = "OK",
        buttonDistanceFromMessage = 32.dp,
        onLeftButtonAction = { /* Handle cancel action */ },
        onRightButtonAction = { /* Handle OK action */ }
    )
}

@Preview(showBackground = true)
@Composable
fun GeneralModalDialog_no_btn_bg_Preview() {
    GeneralModalDialog(
        title = "Confirmation",
        titleStyle = text_20(colorText, true),
        message = "Are you sure you want to proceed?",
        messageStyle = text_16(colorText, false),
        image = null,
        leftButtonLabel = "Cancel",
        rightButtonLabel = "Disconnetti",
        leftButtonTextStyle = text_15(colorText, true),
        rightButtonTextStyle = text_15(colorAccent, true),
        leftButtonBackgroundColor = primaryColor,
        rightButtonBackgroundColor = primaryColor,
        buttonDistanceFromMessage = 32.dp,
        onLeftButtonAction = { /* Handle cancel action */ },
        onRightButtonAction = { /* Handle OK action */ }
    )
}