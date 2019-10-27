package eu.indiewalkabout.fridgemanager.ui

import android.content.Intent
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView

import com.google.android.gms.ads.AdView
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import com.hlab.fabrevealmenu.view.FABRevealMenu

import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.util.ConsentSDK
import kotlinx.android.synthetic.main.activity_credits.*




class CreditsActivity : AppCompatActivity(), OnFABMenuSelectedListener {

    private lateinit var fabMenu: FABRevealMenu
    private lateinit var fab: FloatingActionButton

    private lateinit var creditsToolbar: Toolbar
    private lateinit var toolbarTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credits)


        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mAdView.loadAd(ConsentSDK.getAdRequest(this@CreditsActivity))


        // get a support action bar
        toolBarInit()

        // navigation fab
        addRevealFabBtn()

        // make bottom navigation bar and status bar hide
        hideStatusNavBars()
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Make bottom navigation bar and status bar hide, without resize when reappearing
     * ---------------------------------------------------------------------------------------------
     */
    private fun hideStatusNavBars() {
        // minsdk version is 19, no need code for lower api
        val decorView = window.decorView

        // hide status bar
        if (Build.VERSION.SDK_INT < 16) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else if (Build.VERSION.SDK_INT >= 16) {
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        // hide navigation bar
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            val v = this.window.decorView
            v.systemUiVisibility = View.GONE
        } else if (Build.VERSION.SDK_INT >= 19) {
            val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            decorView.systemUiVisibility = uiOptions
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Toolbar init
     * ---------------------------------------------------------------------------------------------
     */
    private fun toolBarInit() {
        // get the toolbar
        creditsToolbar = findViewById(R.id.credits_toolbar)

        toolbarTitle = creditsToolbar!!.findViewById(R.id.credits_toolbar_title_tv)
        toolbarTitle!!.setText(R.string.credits_btn_title)

        // place toolbar in place of action bar
        setSupportActionBar(creditsToolbar)

        // get a support action bar
        val actionBar = supportActionBar

        actionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    /**
     * ---------------------------------------------------------------------------------------------
     * MENU STUFF
     * ---------------------------------------------------------------------------------------------
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
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
        if (fabMenu != null) {
            if (fabMenu!!.isShowing) {
                fabMenu!!.closeMenu()

            }
        }
    }

    /**
     * ---------------------------------------------------------------------------------------------
     * Adding revealing main_fab button
     * ---------------------------------------------------------------------------------------------
     */
    private fun addRevealFabBtn() {
        fab = findViewById(R.id.credits_fab)
        fabMenu = findViewById(R.id.credits_fabMenu)


        try {
            if (fab != null && fabMenu != null) {
                fabMenu = fabMenu

                //attach menu to main_fab
                fabMenu!!.bindAnchorView(fab!!)

                //set menu selection listener
                fabMenu!!.setOnFABMenuSelectedListener(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        fabMenu!!.menuDirection = Direction.LEFT

    }


    /**
     * ---------------------------------------------------------------------------------------------
     * Revealing main_fab button menu management
     * ---------------------------------------------------------------------------------------------
     */
    override fun onMenuItemSelected(view: View, id: Int) {
        if (id == R.id.menu_insert) {
            val toInsertFood = Intent(this@CreditsActivity, InsertFoodActivity::class.java)
            startActivity(toInsertFood)

        } else if (id == R.id.menu_expiring_food) {
            val showExpiringFood = Intent(this@CreditsActivity, FoodListActivity::class.java)
            showExpiringFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_EXPIRING)
            startActivity(showExpiringFood)

        } else if (id == R.id.menu_consumed_food) {
            val showSavedFood = Intent(this@CreditsActivity, FoodListActivity::class.java)
            showSavedFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_SAVED)
            startActivity(showSavedFood)

        } else if (id == R.id.menu_dead_food) {
            val showDeadFood = Intent(this@CreditsActivity, FoodListActivity::class.java)
            showDeadFood.putExtra(FoodListActivity.FOOD_TYPE, FoodListActivity.FOOD_DEAD)
            startActivity(showDeadFood)

        } else if (id == R.id.menu_home) {
            val returnHome = Intent(this@CreditsActivity, MainActivity::class.java)
            startActivity(returnHome)
        }
    }
}
