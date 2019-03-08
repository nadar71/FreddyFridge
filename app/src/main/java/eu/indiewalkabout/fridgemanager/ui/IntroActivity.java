package eu.indiewalkabout.fridgemanager.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

import eu.indiewalkabout.fridgemanager.R;

public class IntroActivity extends AppIntro2 {

    private static final String APP_OPENING_COUNTER ="app-opening-counter";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle("1 : Make your shopping...");
        sliderPage1.setDescription(" ..,and open FreddyFridge !");
        sliderPage1.setImageDrawable(R.drawable.shopping);
        sliderPage1.setBgColor(getResources().getColor(R.color.background_lightgreen_semitransparent));
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle("2 : Store your food and expiring date");
        sliderPage2.setDescription("with the New button ");
        sliderPage2.setImageDrawable(R.drawable.instructions_03);
        sliderPage2.setBgColor(getResources().getColor(R.color.background_lightgreen_semitransparent));
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle("3 : Check expiring date");
        sliderPage3.setDescription("and be alerted before happens : don't waste food!");
        sliderPage3.setImageDrawable(R.drawable.instructions_end);
        sliderPage3.setBgColor(getResources().getColor(R.color.background_lightgreen_semitransparent));
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        /*
        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle("Explore");
        sliderPage4.setDescription("Feel free to explore the rest of the library demo!");
        sliderPage4.setImageDrawable(R.drawable.ic_hourglass_empty_brown_24dp);
        sliderPage4.setBgColor(getResources().getColor(R.color.background_lightgreen_semitransparent));
        addSlide(AppIntroFragment.newInstance(sliderPage4));
        */

        // Declare a new image view
        ImageView imageView = new ImageView(this);

        // Bind a drawable to the imageview
        imageView.setImageResource(R.drawable.food_background);

        // Set background color
        imageView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        // Set layout params
        imageView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        // Bind the background to the intro
        setBackgroundView(imageView);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }
}