package eu.indiewalkabout.fridgemanager.feat_food.presentation.components

import android.R.color.white
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.brown
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText_02
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodGray
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodGreen
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodOrange
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodRed
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.foodYellow
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.core.util.DateUtility.getLocalDateFormat
import eu.indiewalkabout.fridgemanager.feat_food.domain.model.FoodEntryUI
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun FoodCard(
    food: FoodEntryUI,
    onCheckChanged: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val daysUntilExpiry = food.expiringAtLocalDate?.let {
        ChronoUnit.DAYS.between(today, it).toInt()
    }

    val backgroundColor = when {
        daysUntilExpiry == null -> foodGray
        daysUntilExpiry < 0 -> foodGray
        daysUntilExpiry == 0 -> foodRed
        daysUntilExpiry == 1 -> foodOrange
        daysUntilExpiry == 2 -> foodYellow
        else -> foodGreen
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(backgroundColor, shape = RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = secondaryColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        // Checkbox
        var isChecked by remember { mutableStateOf(food.done == 1) }
        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                onCheckChanged(it)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = brown,
                uncheckedColor = brown,
                checkmarkColor = secondaryColor,

            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Content
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = food.name ?: "Unnamed",
                    style = text_16(colorText_02, false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "n. ${food.quantity}",
                    style = text_14(colorText_02, false)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = food.expiringAtUI ?: "",
                style = text_14(colorText_02, false)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Delete
        Icon(
            painter = painterResource(id = R.drawable.ic_bin),
            contentDescription = stringResource(R.string.content_delete_food_icon),
            tint = brown,
            modifier = Modifier
                .size(24.dp)
                .clickable { onDelete() }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewFoodCard() {
    val foodItems = listOf(
        FoodEntryUI(name = "Expired Food",      expiringAtLocalDate = LocalDate.now().minusDays(1), expiringAtUI = LocalDate.now().minusDays(1).format(getLocalDateFormat()) ?: "", quantity = 1),
        FoodEntryUI(name = "Expires Today",     expiringAtLocalDate = LocalDate.now(),              expiringAtUI = LocalDate.now().format(getLocalDateFormat()) ?: "",              quantity = 2),
        FoodEntryUI(name = "Expires Tomorrow",  expiringAtLocalDate = LocalDate.now().plusDays(1),  expiringAtUI = LocalDate.now().plusDays(1).format(getLocalDateFormat()) ?: "",  quantity = 3),
        FoodEntryUI(name = "Expires in 2 Days", expiringAtLocalDate = LocalDate.now().plusDays(2),  expiringAtUI = LocalDate.now().plusDays(2).format(getLocalDateFormat()) ?: "",  quantity = 4),
        FoodEntryUI(name = "Fresh Food",        expiringAtLocalDate = LocalDate.now().plusDays(3),  expiringAtUI = LocalDate.now().plusDays(3).format(getLocalDateFormat()) ?: "",  quantity = 5)    )

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
