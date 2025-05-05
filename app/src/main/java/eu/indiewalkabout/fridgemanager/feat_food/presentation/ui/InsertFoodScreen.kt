package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.components.AdBannerPlaceholder
import eu.indiewalkabout.fridgemanager.core.presentation.components.BackgroundPattern
import eu.indiewalkabout.fridgemanager.core.presentation.components.TopBar
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorHintText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.colorText
import eu.indiewalkabout.fridgemanager.core.presentation.theme.FreddyFridgeTheme
import eu.indiewalkabout.fridgemanager.core.presentation.theme.LocalAppColors
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_16
import eu.indiewalkabout.fridgemanager.feat_food.presentation.components.SimpleTextField
import eu.indiewalkabout.fridgemanager.feat_navigation.presentation.components.BottomNavigationBar

@Composable
fun InsertFoodScreen() {
    val TAG = "InsertFoodScreen"
    val colors = LocalAppColors.current
    var showOnBoarding by remember { mutableStateOf(false) }
    var selectedDateText by remember { mutableStateOf("") }
    var descriptionText by remember { mutableStateOf("") }


    Scaffold(
        bottomBar = {
            BottomNavigationBar(stringResource(R.string.menu_add_label_item))
        },
        containerColor = colors.primaryColor
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            BackgroundPattern()
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                TopBar(
                    title = stringResource(R.string.insert_food_title),
                    paddingTop = 16.dp,
                    paddingBottom = 16.dp,
                    backgroundColor = colors.primaryColor,
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 8.dp, 16.dp, 0.dp)
                            .background(Color.Transparent),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            text = stringResource(R.string.insert_expiring_date_label),
                            style = text_16(colorText, true),
                        )

                        /*Image(
                            painter = painterResource(id = R.drawable.ic_calendar_month),
                            contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )*/
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(Color.Transparent),
                        horizontalArrangement = Arrangement.Center
                    ){
                        SimpleTextField(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                                // .align(Alignment.Start)
                                // .padding(horizontal = 16.dp),
                            hintText = stringResource(R.string.insert_expiring_date_hint),
                            hintTextStyle = text_14(colorHintText, false),
                            inputTextStyle = text_14(colorText, false),
                            value = descriptionText,
                            onValueChange = {
                                descriptionText = it
                            },
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_calendar_month),
                            contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 16.dp)
                        )
                    }
                }

                // Spacer(modifier = Modifier.height(16.dp))

                /*Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .weight(1f)
                        .background(Color.Transparent),
                ){
                        CalendarContent(
                            startDate = LocalDate.now(),
                            minDate = LocalDate.now().minusMonths(2),
                            maxDate = LocalDate.MAX,
                            onDateSelected = { selectedDateText = it.toString() },
                            showSelectedDate = true,
                            selectedDateFormat = LOCAL_DATE_FORMAT
                        )
                }*/

                // Spacer(modifier = Modifier.height(16.dp))

                Column{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 8.dp, 16.dp, 0.dp)
                            .background(Color.Transparent),
                        horizontalArrangement = Arrangement.Start
                        ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            text = stringResource(R.string.insert_food_description_hint),
                            style = text_16(colorText, true),
                        )
                        /*Image(
                            painter = painterResource(id = R.drawable.ic_mic_white),
                            contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )*/
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(Color.Transparent),
                        horizontalArrangement = Arrangement.Center
                    ){
                        SimpleTextField(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                                // .align(Alignment.Start)
                                // .padding(horizontal = 16.dp),
                            hintText = stringResource(R.string.insert_food_description_hint),
                            hintTextStyle = text_14(colorHintText, false),
                            inputTextStyle = text_14(colorText, false),
                            value = descriptionText,
                            onValueChange = {
                                descriptionText = it
                            },
                        )
                        Image(
                            painter = painterResource(id = R.drawable.ic_mic_white),
                            contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 16.dp)
                        )
                    }
                }

                // Spacer(modifier = Modifier.height(16.dp))

                Column{
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 8.dp, 16.dp, 0.dp)
                            .background(Color.Transparent),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            text = stringResource(R.string.insert_quantity_label),
                            style = text_16(colorText, true),
                        )
                        /*Image(
                            painter = painterResource(id = R.drawable.ic_quantity_add),
                            contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                        )*/

                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(Color.Transparent),
                    ){
                        SimpleTextField(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                                //.align(Alignment.Start)
                                //.padding(horizontal = 16.dp),
                            hintText = stringResource(R.string.insert_quantity_label),
                            hintTextStyle = text_14(colorHintText, false),
                            inputTextStyle = text_14(colorText, false),
                            value = descriptionText,
                            onValueChange = {
                                descriptionText = it
                            },
                        )

                        Image(
                            painter = painterResource(id = R.drawable.ic_quantity_add),
                            contentDescription = stringResource(R.string.insert_expiring_date_icon_description),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(horizontal = 16.dp)
                        )
                    }
                }

                // Ad Banner Placeholder
                AdBannerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )

            }
        }
    }

}


@Preview
@Composable
fun PreviewInsertFoodScreen() {
    FreddyFridgeTheme {
        InsertFoodScreen()
    }
}
