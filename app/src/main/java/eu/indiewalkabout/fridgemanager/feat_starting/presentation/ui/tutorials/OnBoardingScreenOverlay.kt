package eu.indiewalkabout.fridgemanager.feat_starting.presentation.ui.tutorials

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension.Companion.fillToConstraints
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import eu.indiewalkabout.fridgemanager.core.presentation.components.RoundedCornerButton
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_14
import eu.indiewalkabout.fridgemanager.core.presentation.theme.text_26
import kotlinx.coroutines.launch


@OptIn(ExperimentalPagerApi::class)
@Composable
fun OnBoardingScreenOverlay(
    onSkip: () -> Unit = {},
    onContinue: () -> Unit = {}
) {
    val TAG = "OnBoardingScreenOverlay"
    Log.d(TAG, "OnBoardingScreenOverlay: shown")

    val pagerState = rememberPagerState()
    val coroutineScope = rememberCoroutineScope()

    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
    ) {
        val (
            pagerRef,
            indicatorsRef,
            buttonRef
        ) = createRefs()

        // Horizontal Pager for Tutorial Pages
        HorizontalPager(
            count = 2,
            state = pagerState,
            modifier = Modifier.constrainAs(pagerRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(indicatorsRef.top, 16.dp)
                height = fillToConstraints
            }
        ) { page ->
            TutorialPage(
                page = page,
                onSkip = {
                    // AppPreferences.animSelfReadingDone = true
                    onSkip()
                },
                onContinue = {
                    if (page < 1) {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page + 1)
                        }
                    } else {
                        // AppPreferences.animSelfReadingDone = true
                        onContinue()
                    }
                }
            )
        }


        // Indicator Row for page indicators
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .constrainAs(indicatorsRef) {
                    bottom.linkTo(buttonRef.top, 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
        ) {

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(if (pagerState.currentPage == 0) Color.Black else Color.Gray, CircleShape)
                    .padding(horizontal = 4.dp)
            )

            Spacer(
                modifier = Modifier
                    .width(8.dp),
            )

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(if (pagerState.currentPage == 1) Color.Black else Color.Gray, CircleShape)
                    .padding(horizontal = 4.dp)
            )

        }

        // Continue/End Button
        RoundedCornerButton(
            modifier = Modifier
                .padding(16.dp)
                .constrainAs(buttonRef) {
                    bottom.linkTo(parent.bottom, 16.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = fillToConstraints
                },
            onClick = {
                if (pagerState.currentPage < 1) {
                    coroutineScope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                } else {
                    // AppPreferences.animSelfReadingDone = true
                    onContinue()
                }
            },
            shape = RoundedCornerShape(15.dp),
            backgroundColor = Color.DarkGray,
            text = if (pagerState.currentPage == 0)
                //stringResource(R.string.abtutorial_continue_button)
                "Continue"
            else
                // stringResource(R.string.abtutorial_understand_button),
                "Understand",
            textPadding = 0,
            style = text_14(Color.White, true)
        )
    }
}



@Composable
fun TutorialPage(
    page: Int,
    onSkip: () -> Unit,
    onContinue: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White)
    ) {
        val (skipTextRef, lottieAnimRef, titleRef, descriptionRef) = createRefs()

        val horizGuideline = createGuidelineFromTop(0.60f)

        // Skip text
        Text(
            text = "Skip", //stringResource(id = R.string.abtutorial_skip_button).uppercase(),
            style = text_14(Color.Black, true),
            modifier = Modifier
                .constrainAs(skipTextRef) {
                    top.linkTo(parent.top, 24.dp)
                    end.linkTo(parent.end, 32.dp)
                }
                .clickable { onSkip() }
        )

        /*// Lottie animation
        val composition by rememberLottieComposition(
            LottieCompositionSpec.RawRes(
                if (page == 0) R.raw.step_03_data
                else R.raw.step_04_data
            )
        )
        LottieAnimation(
            composition = composition,
            modifier = Modifier
                .constrainAs(lottieAnimRef) {
                    linkTo(skipTextRef.bottom, horizGuideline, 60.dp, 0.dp, bias = 0.2f)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = fillToConstraints
                },
            contentScale = ContentScale.Fit
        )*/

        // TODO : set a image placeholder space

        // Title
        Text(
            text = if (page == 0)
                "Firs Page" //stringResource(id = R.string.abtutorial_customer_gas_meter_title)
            else
                "Other pages",// stringResource(id = R.string.abtutorial_customer_self_reading_title),
            style = text_26(Color.Black, true),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .constrainAs(titleRef) {
                    top.linkTo(horizGuideline, 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )

        // Description
        Text(
            text = if (page == 0)
                "Main Page Description"// stringResource(id = R.string.abtutorial_customer_gas_meter_description)
            else
                "Other Page Description", // stringResource(id = R.string.abtutorial_customer_self_reading_description),
            style = text_14(Color.Black, false),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .constrainAs(descriptionRef) {
                    top.linkTo(titleRef.bottom, 32.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            textAlign = TextAlign.Center
        )
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewSelfreadingTutorialScreen() {
    OnBoardingScreenOverlay(
        onSkip = {},
        onContinue = {}
    )
}
