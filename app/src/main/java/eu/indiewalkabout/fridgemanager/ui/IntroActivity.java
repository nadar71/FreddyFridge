package eu.indiewalkabout.fridgemanager.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import eu.indiewalkabout.fridgemanager.R;

public class IntroActivity extends AppIntro2 {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle(getResources().getString(R.string.intro_1_title));
        sliderPage1.setDescription(getResources().getString(R.string.intro_1_subtitle));
        sliderPage1.setImageDrawable(R.drawable.shopping);
        sliderPage1.setBgColor(getResources().getColor(R.color.background_lightgreen_semitransparent));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle(getResources().getString(R.string.intro_2_title));
        sliderPage2.setDescription(getResources().getString(R.string.intro_2_subtitle));
        sliderPage2.setImageDrawable(R.drawable.instructions_03);
        sliderPage2.setBgColor(getResources().getColor(R.color.background_lightgreen_semitransparent));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle(getResources().getString(R.string.intro_3_title));
        sliderPage3.setDescription(getResources().getString(R.string.intro_3_subtitle));
        sliderPage3.setImageDrawable(R.drawable.instructions_end);
        sliderPage3.setBgColor(getResources().getColor(R.color.background_lightgreen_semitransparent));
        addSlide(AppIntroFragment.newInstance(sliderPage3));


        // Declare a new image view
        ImageView imageView = new ImageView(this);

        // Bind a drawable to the imageview
        imageView.setImageResource(R.drawable.food_background);

        // Set background color
        imageView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        // Set layout params
        imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Bind the background to the intro
        setBackgroundView(imageView);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        goToMain();
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainActivityIntent.putExtra("ComingFromIntro", true);
        startActivity(mainActivityIntent);
        finish();
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Exit intro, go to main activity
     * ---------------------------------------------------------------------------------------------
     */
    public void goToMain(){
        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainActivityIntent.putExtra("ComingFromIntro",true);
        startActivity(mainActivityIntent);
    }



}