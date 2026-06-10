package eu.indiewalkabout.fridgemanager.feat_food.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.data.locals.Globals.IS_IN_PREVIEW
import java.util.TimeZone

@Composable
fun InsertFoodBottomSheetContent(
    insertFoodViewModel: InsertFoodViewModel = hiltViewModel(),
) {
    FoodEditorExperience(
        initialState = FoodEditorUiState.empty(),
        titleResId = R.string.insert_food_title,
        onSubmit = { editorState ->
            val entries = FoodEntrySubmissionBuilder.buildInsertEntries(
                name = editorState.description,
                expiringAt = editorState.expiringAt,
                timezoneId = TimeZone.getDefault().id,
                quantity = editorState.quantity,
            )
            insertFoodViewModel.insertFoods(entries)
        }
    )
}

@Preview(showBackground = true)
@Composable
fun InsertFoodBottomSheetContentPreview() {
    IS_IN_PREVIEW = true
    InsertFoodBottomSheetContent()
}
