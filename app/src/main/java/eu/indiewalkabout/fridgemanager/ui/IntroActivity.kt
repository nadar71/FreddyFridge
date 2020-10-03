package eu.indiewalkabout.fridgemanager.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.github.appintro.AppIntro2
import com.github.appintro.AppIntroFragment
import com.github.appintro.model.SliderPage
import eu.indiewalkabout.fridgemanager.R

class IntroActivity : AppIntro2() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sliderPage1 = SliderPage()
        sliderPage1.title = resources.getString(R.string.intro_1_title)
        sliderPage1.description = resources.getString(R.string.intro_1_subtitle)
        sliderPage1.imageDrawable = R.drawable.shopping
        sliderPage1.backgroundColor = R.color.background_lightgreen_semitransparent
        sliderPage1.backgroundDrawable = R.drawable.food_background
        addSlide(AppIntroFragment.newInstance(sliderPage1))

        val sliderPage2 = SliderPage()
        sliderPage2.title = resources.getString(R.string.intro_2_title)
        sliderPage2.description = resources.getString(R.string.intro_2_subtitle)
        sliderPage2.imageDrawable = R.drawable.instructions_03
        sliderPage2.backgroundColor = R.color.background_lightgreen_semitransparent
        sliderPage1.backgroundDrawable = R.drawable.food_background
        addSlide(AppIntroFragment.newInstance(sliderPage2))


        val sliderPage3 = SliderPage()
        sliderPage3.title = resources.getString(R.string.intro_3_title)
        sliderPage3.description = resources.getString(R.string.intro_3_subtitle)
        sliderPage3.imageDrawable = R.drawable.instructions_end
        sliderPage3.backgroundColor = R.color.background_lightgreen_semitransparent
        sliderPage1.backgroundDrawable = R.drawable.food_background
        addSlide(AppIntroFragment.newInstance(sliderPage3))

/*
        // Declare a new image view
        val imageView = ImageView(this)

        // Bind a drawable to the imageview
        imageView.setImageResource(R.drawable.food_background)

        // Set background color
        imageView.setBackgroundColor(resources.getColor(R.color.colorPrimaryDark))

        // Set layout params
        imageView.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)

        // Bind the background to the intro
        setBackgroundDrawable(R.drawable.food_background)
 */
    }

    override fun onSkipPressed(currentFragment: androidx.fragment.app.Fragment?) {
        super.onSkipPressed(currentFragment)
        goToMain()
        finish()
    }

    override fun onDonePressed(currentFragment: androidx.fragment.app.Fragment?) {
        super.onDonePressed(currentFragment)
        val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
        mainActivityIntent.putExtra("ComingFromIntro", true)
        startActivity(mainActivityIntent)
        finish()
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Exit intro, go to main activity
     * ---------------------------------------------------------------------------------------------
     */
    fun goToMain() {
        val mainActivityIntent = Intent(applicationContext, MainActivity::class.java)
        mainActivityIntent.putExtra("ComingFromIntro", true)
        startActivity(mainActivityIntent)
    }
}