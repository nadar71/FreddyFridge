package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.SimpleTextField
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorHintText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor


@Composable
fun InsertFoodBottomSheetContent(
    descriptionText: String,
    onDescriptionChange: (String) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                primaryColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),

    ) {

        TopBar(
            title = stringResource(R.string.insert_food_title),
            paddingTop = 16.dp,
            paddingBottom = 16.dp,
            backgroundColor = primaryColor,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // expiring date
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
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                hintText = stringResource(R.string.insert_expiring_date_hint),
                hintTextStyle = text_14(colorHintText, false),
                inputTextStyle = text_14(colorText, false),
                value = descriptionText,
                onValueChange = onDescriptionChange,
            )
            Image(
                painter = painterResource(id = R.drawable.ic_calendar_month),
                contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 16.dp)
            )
        }

        // description
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ){
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
                value = descriptionText,
                onValueChange = onDescriptionChange,
            )
            Image(
                painter = painterResource(id = R.drawable.ic_mic_white),
                contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 16.dp)
            )
        }

        // quantity
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp, 16.dp, 0.dp),
            horizontalArrangement = Arrangement.Start
        ){
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
                value = descriptionText,
                onValueChange = onDescriptionChange,
            )
            Image(
                painter = painterResource(id = R.drawable.ic_quantity_add),
                contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
}


@Preview(showBackground = true)
@Composable
fun InsertFoodBottomSheetContentPreview() {
    InsertFoodBottomSheetContent(
        descriptionText = "",
        onDescriptionChange = {},
    )
}
