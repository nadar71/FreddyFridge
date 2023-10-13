package eu.indiewalkabout.fridgemanager.presentation.ui.credits

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.util.ConsentSDK.Companion.getAdRequest
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.hideStatusNavBars
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.showRandomizedInterstAds
import eu.indiewalkabout.fridgemanager.databinding.ActivityCreditsBinding
import eu.indiewalkabout.fridgemanager.presentation.ui.food.FoodListActivity
import eu.indiewalkabout.fridgemanager.presentation.ui.food.InsertFoodActivity
import eu.indiewalkabout.fridgemanager.presentation.ui.intromain.MainActivity

class CreditsActivity : AppCompatActivity(), OnFABMenuSelectedListener {
    private lateinit var binding: ActivityCreditsBinding
    private lateinit var creditsToolbar: Toolbar
    private lateinit var toolbarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_credits)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        binding.adView.loadAd(getAdRequest(this@CreditsActivity))
        toolBarInit()
        addRevealFabBtn()
        hideStatusNavBars(this)
    }


    // Toolbar init
    private fun toolBarInit() {
        // get the toolbar
        creditsToolbar = findViewById(R.id.credits_toolbar)
        toolbarTitle = creditsToolbar.findViewById(R.id.credits_toolbar_title_tv)
        toolbarTitle.setText(R.string.credits_btn_title)

        // place toolbar in place of action bar
        setSupportActionBar(creditsToolbar)

        // get a support action bar
        val actionBar = supportActionBar
        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    // ---------------------------------------------------------------------------------------------
    // MENU STUFF
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                showRandomizedInterstAds(6, this)
                onBackPressed()
                return true
            }

            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // ---------------------------------------------------------------------------------------------
    //                                  REVEALING FAB BTN STUFF
    // ---------------------------------------------------------------------------------------------
    override fun onBackPressed() {
        super.onBackPressed()
        showRandomizedInterstAds(6, this)
        if (binding.creditsFabMenu.isShowing) {
            binding.creditsFabMenu.closeMenu()
        }
    }

    //  Adding revealing main_fab button
    private fun addRevealFabBtn() {
        try {
            //attach menu to main_fab
            binding.creditsFabMenu.bindAnchorView(binding.creditsFab)
            //set menu selection listener
            binding.creditsFabMenu.setOnFABMenuSelectedListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.creditsFabMenu.setMenuDirection(Direction.LEFT)
    }


    // Revealing main_fab button menu management
    override fun onMenuItemSelected(view: View, id: Int) {
        if (id == R.id.menu_insert) {
            val toInsertFood = Intent(this@CreditsActivity, InsertFoodActivity::class.java)
            startActivity(toInsertFood)
        } else if (id == R.id.menu_expiring_food) {
            showRandomizedInterstAds(6, this)
            val showExpiringFood = Intent(this@CreditsActivity, FoodListActivity::class.java)
            showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING)
            startActivity(showExpiringFood)
        } else if (id == R.id.menu_consumed_food) {
            showRandomizedInterstAds(6, this)
            val showSavedFood = Intent(this@CreditsActivity, FoodListActivity::class.java)
            showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED)
            startActivity(showSavedFood)
        } else if (id == R.id.menu_dead_food) {
            showRandomizedInterstAds(6, this)
            val showDeadFood = Intent(this@CreditsActivity, FoodListActivity::class.java)
            showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD)
            startActivity(showDeadFood)
        } else if (id == R.id.menu_home) {
            showRandomizedInterstAds(6, this)
            val returnHome = Intent(this@CreditsActivity, MainActivity::class.java)
            startActivity(returnHome)
        }
    }


}