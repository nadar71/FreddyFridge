package eu.indiewalkabout.fridgemanager.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.google.android.gms.ads.AdView
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import com.hlab.fabrevealmenu.view.FABRevealMenu
import eu.indiewalkabout.fridgemanager.App
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.util.ConsentSDK.Companion.getAdRequest
import eu.indiewalkabout.fridgemanager.util.GenericUtility
import eu.indiewalkabout.fridgemanager.util.GenericUtility.hideStatusNavBars
import eu.indiewalkabout.fridgemanager.util.GenericUtility.randRange_ApiCheck
import eu.indiewalkabout.fridgemanager.util.GenericUtility.showRandomizedInterstAds

class CreditsActivity : AppCompatActivity(), OnFABMenuSelectedListener {
    // admob banner ref
    lateinit var mAdView: AdView
    lateinit var fabMenu: FABRevealMenu
    lateinit var fab: FloatingActionButton
    lateinit var creditsToolbar: Toolbar
    lateinit var toolbarTitle: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)

        // load ads banner
        mAdView = findViewById(R.id.adView)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(getAdRequest(this@CreditsActivity))


        // get a support action bar
        toolBarInit()

        // navigation fab
        addRevealFabBtn()

        hideStatusNavBars(this)
    }



    // ---------------------------------------------------------------------------------------------
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
        if (fabMenu != null) {
            if (fabMenu.isShowing) {
                fabMenu.closeMenu()
            }
        }
    }

    // ---------------------------------------------------------------------------------------------
    //  Adding revealing main_fab button

    private fun addRevealFabBtn() {
        fab     = findViewById(R.id.credits_fab)
        fabMenu = findViewById(R.id.credits_fabMenu)
        try {
            if (fab != null && fabMenu != null) {
                fabMenu = fabMenu

                //attach menu to main_fab
                fabMenu.bindAnchorView(fab)

                //set menu selection listener
                fabMenu.setOnFABMenuSelectedListener(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        fabMenu.setMenuDirection(Direction.LEFT)
    }

    // ---------------------------------------------------------------------------------------------
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