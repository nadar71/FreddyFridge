package eu.indiewalkabout.fridgemanager.feat_food.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.primaryColor
import eu.indiewalkabout.fridgemanager.core.presentation.theme.AppColors.secondaryColor


@Composable
fun ListActionDialog(
    isProductOpen: Boolean = false,
    onUpdate: () -> Unit,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {

    val TAG = "ListActionDialog"
    val context = LocalContext.current

    // ----------------------------------------- UI ------------------------------------------------
    Dialog(
        onDismissRequest = {
            onDismiss()
        })
    {
        Card(
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, secondaryColor),
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(primaryColor)
            ) {


                // Update product
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            onUpdate()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_update),
                        contentDescription = "Update product Icon",
                        modifier = Modifier.size(24.dp),
                        tint = secondaryColor
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(id = R.string.update_food_label),
                        fontSize = 16.sp,
                        color = secondaryColor
                    )
                }

                HorizontalDivider(color = secondaryColor, thickness = 1.dp)

                // Open product
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                onOpen()
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isProductOpen) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_unlock),
                                contentDescription = "Open product Icon",
                                modifier = Modifier.size(24.dp),
                                tint = secondaryColor
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = stringResource(id = R.string.open_food_label),
                                fontSize = 16.sp,
                                color = secondaryColor
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_lock),
                                contentDescription = "Open product Icon",
                                modifier = Modifier.size(24.dp),
                                tint = secondaryColor
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = stringResource(id = R.string.close_food_label),
                                fontSize = 16.sp,
                                color = secondaryColor
                            )
                        }
                    }

                HorizontalDivider(color = secondaryColor, thickness = 1.dp)


                // Delete product
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            onDelete()
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_bin),
                        contentDescription = "Delete product Icon",
                        modifier = Modifier.size(24.dp),
                        tint = secondaryColor
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(id = R.string.delete_food_label),
                        fontSize = 16.sp,
                        color = secondaryColor
                    )
                }


            }


        }
    }
}


@Preview
@Composable
fun PreviewListActionDialog() {
    ListActionDialog(
        onUpdate = {},
        onOpen = {},
        onDelete = {},
        onDismiss = {}
    )
}