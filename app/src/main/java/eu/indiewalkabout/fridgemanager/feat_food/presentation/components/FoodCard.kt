package eu.indiewalkabout.fridgemanager.feat_food.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.components.GeneralModalDialog
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.brown
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText_02
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodGray
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodGreen
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodOrange
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodRed
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodYellow
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColorDark
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColorSemitransparent
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_20
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getLocalDateFormat
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntryUI
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.toFoodEntry
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.FoodViewModel
import eu.indiewalkabout.fridgemanager.feat_food.presentation.ui.UpdateFoodOverlay
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun FoodCard(
    food: FoodEntryUI,
    isUpdatable: Boolean = false,
    onCheckChanged: () -> Unit,
    onDelete: () -> Unit = {},
    onUpdate: () -> Unit = {},
    foodViewModel: FoodViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val daysUntilExpiry = food.expiringAtLocalDate?.let {
        ChronoUnit.DAYS.between(today, it).toInt()
    }
    var isChecked by remember { mutableStateOf(food.done == 1) }

    val backgroundColor = when {
        daysUntilExpiry == null -> foodGray
        daysUntilExpiry < 0 -> foodGray
        daysUntilExpiry == 0 -> foodRed
        daysUntilExpiry == 1 -> foodOrange
        daysUntilExpiry == 2 -> foodYellow
        else -> foodGreen
    }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showCheckConfirmDialog by remember { mutableStateOf(false) }
    var showUpdateDialog by remember { mutableStateOf(false) }


    // ------------------------------ DIALOG -------------------------------------------------------
    if (showDeleteConfirmDialog){
        GeneralModalDialog(
            backgroundColor = primaryColorSemitransparent,
            title = stringResource(R.string.generic_confirm_label),
            titleStyle = text_20(colorText, true),
            message = stringResource(R.string.dialog_confirm_if_deleting_text),
            messageStyle = text_16(colorText, false),
            image = null,
            leftButtonLabel = stringResource(R.string.generic_cancel),
            rightButtonLabel = stringResource(R.string.generic_ok),
            rightButtonBackgroundColor = primaryColorDark,
            buttonCornerRadius = 5.dp,
            buttonDistanceFromMessage = 32.dp,
            buttonStrokeWidth = 1.dp,
            buttonStrokeColor = secondaryColor,
            onLeftButtonAction = {
                showDeleteConfirmDialog = false
            },
            onRightButtonAction = {
                foodViewModel.deleteFoodEntry(food.toFoodEntry())
                showDeleteConfirmDialog = false
                onDelete()
            }
        )
    }

    if (showCheckConfirmDialog){
        GeneralModalDialog(
            backgroundColor = primaryColorSemitransparent,
            title = stringResource(R.string.generic_confirm_label),
            titleStyle = text_20(colorText, true),
            message =
            if (!isChecked)
                stringResource(R.string.dialog_confirm_if_not_consumed_text)
            else
                stringResource(R.string.dialog_confirm_if_consumed_text),
            messageStyle = text_16(colorText, false),
            image = null,
            leftButtonLabel = stringResource(R.string.generic_cancel),
            rightButtonLabel = stringResource(R.string.generic_ok),
            rightButtonBackgroundColor = primaryColorDark,
            buttonCornerRadius = 5.dp,
            buttonDistanceFromMessage = 32.dp,
            buttonStrokeWidth = 1.dp,
            buttonStrokeColor = secondaryColor,
            onLeftButtonAction = {
                isChecked = false
                showCheckConfirmDialog = false
            },
            onRightButtonAction = {
                food.done = if (isChecked) 1 else 0
                food.consumedAtLocalDate = if (isChecked) LocalDate.now() else null
                foodViewModel.updateFoodEntry(food.toFoodEntry())
                showCheckConfirmDialog = false
                onCheckChanged()
            }
        )
    }


    if (showUpdateDialog) {
        UpdateFoodOverlay(
            foodEntryUI = food,
            cancelable = true,
            onLeftButtonAction = {
                showUpdateDialog = false
            },
            onSave = {
                showUpdateDialog = false
                onUpdate()
            }
        )
    }

    // ------------------------------ UI -----------------------------------------------------------

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .background(backgroundColor, shape = RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = secondaryColor,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(2.dp, 8.dp, 10.dp, 8.dp)
            .clickable {
                if (isUpdatable) {
                    showUpdateDialog = true
                }
            }
    ) {
        // Checkbox
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                showCheckConfirmDialog = true
            },
            colors = CheckboxDefaults.colors(
                checkedColor = brown,
                uncheckedColor = brown,
                checkmarkColor = secondaryColor,
                ),
            )

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = food.name ?: "Unnamed",
                    style = text_16(colorText_02, false),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "n.${food.order_number}",
                    style = text_14(colorText_02, false)
                )
                // Spacer(modifier = Modifier.width(8.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_hourglass_empty_white),
                        contentDescription = stringResource(R.string.content_expiring_date_icon),
                        tint = brown,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onDelete() }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = food.expiringAtUI ?: "",
                        style = text_14(colorText_02, false)
                    )
                }

                Spacer(modifier = Modifier.width(32.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    // modifier = Modifier.weight(1f)
                ){
                    if (food.done == 1 && food.consumedAtUI != null) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_check),
                            contentDescription = stringResource(R.string.content_delete_food_icon),
                            tint = brown,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onDelete() }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = food.consumedAtUI ?: "",
                            style = text_14(colorText_02, false)
                        )

                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Delete
        Icon(
            painter = painterResource(id = R.drawable.ic_bin),
            contentDescription = stringResource(R.string.content_delete_food_icon),
            tint = brown,
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    onDelete()
                    showDeleteConfirmDialog = true
                }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewFoodCard() {
    val foodItems = listOf(
        FoodEntryUI(
            id = 1,
            name = "Expired Food",
            expiringAtLocalDate = LocalDate.now().minusDays(1),
            expiringAtUI = LocalDate.now().minusDays(1).format(getLocalDateFormat()) ?: "",
            consumedAtUI = LocalDate.now().minusDays(3).format(getLocalDateFormat()) ?: "",
            order_number = 1
        ),
        FoodEntryUI(
            id = 2,
            name = "Expires Today",
            expiringAtLocalDate = LocalDate.now(),
            expiringAtUI = LocalDate.now().format(getLocalDateFormat()) ?: "",
            order_number = 2
        ),
        FoodEntryUI(
            id = 3,
            name = "Expires Tomorrow",
            expiringAtLocalDate = LocalDate.now().plusDays(1),
            expiringAtUI = LocalDate.now().plusDays(1).format(getLocalDateFormat()) ?: "",
            order_number = 3
        ),
        FoodEntryUI(
            id = 4,
            name = "Expires in 2 Days",
            expiringAtLocalDate = LocalDate.now().plusDays(2),
            expiringAtUI = LocalDate.now().plusDays(2).format(getLocalDateFormat()) ?: "",
            order_number = 4
        ),
        FoodEntryUI(
            id = 5,
            name = "Fresh Food",
            expiringAtLocalDate = LocalDate.now().plusDays(3),
            expiringAtUI = LocalDate.now().plusDays(3).format(getLocalDateFormat()) ?: "",
            order_number = 5
        ),
        FoodEntryUI(
            id = 6,
            name = "Fresh Food Fresh Food Fresh Food Fresh Food Fresh Food Fresh Food Fresh ciao ",
            expiringAtLocalDate = LocalDate.now().plusDays(3),
            expiringAtUI = LocalDate.now().plusDays(3).format(getLocalDateFormat()) ?: "",
            consumedAtUI = LocalDate.now().minusDays(4).format(getLocalDateFormat()) ?: "",
            order_number = 5,
            done = 1
        ),
        FoodEntryUI(
            id = 7,
            name = "Fresh Food Fresh Food Fresh Food Fresh Food Fresh Food Fresh Food Fresh ciao ",
            expiringAtLocalDate = LocalDate.now().plusDays(3),
            expiringAtUI = LocalDate.now().plusDays(3).format(getLocalDateFormat()) ?: "",
            consumedAtUI = null,
            order_number = 5,
            done = 1
        )
    )


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = primaryColor)
    ) {
        foodItems.forEach { food ->
            FoodCard(
                food = food,
                onCheckChanged = {},
                onDelete = {}
            )
        }
    }
}
