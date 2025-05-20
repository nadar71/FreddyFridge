package eu.indiewalkabout.fridgemanager.presentation.ui.food

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
// import com.google.android.gms.ads.interstitial.InterstitialAd
// import com.google.android.gms.ads.AdListener
// import com.google.android.gms.ads.interstitial.InterstitialAd
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.presentation.components.adapter.FoodListAdapter
import eu.indiewalkabout.fridgemanager.presentation.components.adapter.FoodListAdapter.ItemClickListener
import eu.indiewalkabout.fridgemanager.presentation.ui.intromain.MainActivity
// import eu.indiewalkabout.fridgemanager.core.util.ConsentSDK.Companion.getAdRequest
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.hideStatusNavBars
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.showRandomizedInterstAds
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import eu.indiewalkabout.fridgemanager.databinding.ActivityFoodListBinding
import java.text.DateFormat.getDateInstance

// ---------------------------------------------------------------------------------------------
// Show list of food depending on FOOD_TYPE

class FoodListActivity : AppCompatActivity(), ItemClickListener, OnFABMenuSelectedListener {
    private lateinit var binding: ActivityFoodListBinding

    companion object {
        // Key constant to use as key for intent extra
        // get the type of list to show from intent content extra
        const val FOOD_TYPE = "food_type"

        // Values to use for passing intent extras in key/value pair
        // expiring food flag
        const val FOOD_EXPIRING = "ExpiringFood"

        // expiring food Today flag
        const val FOOD_EXPIRING_TODAY = "ExpiringFoodToday"

        // saved food flag
        const val FOOD_SAVED = "SavedFood"

        // dead food flag
        const val FOOD_DEAD = "DeadFood"
    }

    private var foodListAdapter: FoodListAdapter? = null
    private var foodListForShare: String = ""
    private var foodShareSubject: String = ""

    // lateinit var mInterstitialAd: InterstitialAd

    // Hold the type of food to show in list, use here and by adapter
    // TODO : find a better way to pass this info to adapter
    private var foodlistType: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_food_list)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_list)


        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        // binding.adView.loadAd(getAdRequest(this@FoodListActivity))

        // TODO : use this in another way
        // get intent extra for configuring list type
        val intent = intent
        if (intent != null && intent.hasExtra(FOOD_TYPE)) {
            foodlistType = intent.getStringExtra(FOOD_TYPE)
            Log.d(TAG, "onCreate: FOOD_TYPE : " + foodlistType.toString())
        }

        toolBarInit()
        addRevealFabBtn()
        initRecycleView()

        hideStatusNavBars(this)

        binding.shareFoodList.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = foodListForShare
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, foodShareSubject)
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }
    }


    // Recycle touch an item callback to update/modify task
    override fun onItemClickListener(itemId: Int) {
        Log.d(TAG, "onItemClickListener: Item" + itemId + "touched.")
    }


    // Toolbar init
    private fun toolBarInit() {
        setToolBarTitle()
        // place toolbar in place of action bar
        setSupportActionBar(binding.foodListToolbar)

        // up button
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }


    private fun setToolBarTitle() {

        // set correct title
        if (foodlistType == FOOD_EXPIRING) {
            binding.toolbarTitleTv.setText(R.string.foodExpiring_activity_title)
            foodShareSubject = getString(R.string.expiring_food_list_subject)
            Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodExpiring_activity_title)

        } else if (foodlistType == FOOD_SAVED) {
            binding.toolbarTitleTv.setText(R.string.foodSaved_activity_title)
            foodShareSubject = getString(R.string.saved_food_list_subject)
            Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodSaved_activity_title)

        } else if (foodlistType == FOOD_DEAD) {
            binding.toolbarTitleTv.setText(R.string.foodDead_activity_title)
            foodShareSubject = getString(R.string.wasted_food_list_subject)
            Log.d(TAG, "onCreate: FOOD_TYPE : " + R.string.foodDead_activity_title)
        }
    }

    // Show admob interstitial on ui request
    /*private fun showAdMobInterstitialAd() {
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = resources.getString(R.string.admob_key_interstitial)

        // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
        mInterstitialAd.loadAd(getAdRequest(this))
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Toast.makeText(FoodListActivity.this, "Ad Interstitial LOADED", Toast.LENGTH_SHORT).show();
                // Show interstitial
                mInterstitialAd.show()
                super.onAdLoaded()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
                // Toast.makeText(FoodListActivity.this, "Ad Interstitial   FAILED TO LOAD", Toast.LENGTH_SHORT).show();
                // Log.i(TAG, "onAdFailedToLoad: ConsentSDK.getAdRequest(MainActivity.this) : "
                //        +ConsentSDK.getAdRequest(MainActivity.this));
            }
        }
    }
*/

    private fun initRecycleView() {
        binding.foodListRecycleView.setLayoutManager(LinearLayoutManager(this))
        foodListAdapter = FoodListAdapter(this, this, foodlistType!!)
        binding.foodListRecycleView.setAdapter(foodListAdapter)

        setupAdapter()

        // make fab button hide when scrolling list
        binding.foodListRecycleView.addOnScrollListener(
            object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    dx: Int,
                    dy: Int
                ) {
                    if (dy > 0 || dy < 0 && binding.foodListFab.isShown) binding.foodListFab.hide()
                }

                override fun onScrollStateChanged(
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    newState: Int
                ) {
                    if (newState == androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE)
                        binding.foodListFab.show()
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
    }

    // Used to reload from db the tasks list and update the list view in screen
    private fun setupAdapter() {
        Log.d(TAG, "setupAdapter: LOAD FOOD ENTRIES IN LIST ")
        retrieveAllFood()
        Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
    }


    private fun retrieveAllFood() {
        Log.d(TAG, "Actively retrieving Expiring Food from DB")

        val factory = FoodsViewModelFactory(foodlistType!!)

        // Create the FoodsViewModel  for the food list, based on  foodlistType
        val viewModel = ViewModelProvider(this, factory).get(FoodsViewModel::class.java)

        // Observe changes in data through LiveData: getFoodList() actually return LiveData<List<FoodEntry>>
        val foods: LiveData<MutableList<FoodEntry>>? = viewModel.foodList
        foods!!.observe(this, Observer<MutableList<FoodEntry>?> { foodEntries ->
            Log.d(TAG, "Receiving database $foodlistType LiveData")
            foodListAdapter!!.setFoodEntries(foodEntries)
            if (foodEntries!!.size > 0) {
                binding.emptyListText.visibility = View.INVISIBLE
                binding.shareFoodList.visibility = View.VISIBLE
                // fill the list for sharing
                foodListForShare += "${foodShareSubject} \n\n"
                for (item in foodEntries) {
                    foodListForShare += "${item.name} :  ${getDateInstance().format(item.expiringAt)}\n\n"
                }
            } else {
                binding.emptyListText.visibility = View.VISIBLE
                binding.shareFoodList.visibility = View.INVISIBLE
            }
        })
    }

    // MENU STUFF
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        // When the home button is pressed, take the user back to Home
        if (id == android.R.id.home) {
            showRandomizedInterstAds(4, this)
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    // ---------------------------------------------------------------------------------------------
    //                                  REVEALING FAB BTN STUFF
    // ---------------------------------------------------------------------------------------------
    override fun onBackPressed() {
        super.onBackPressed()
        showRandomizedInterstAds(4, this)
        if (binding.foodListFabMenu.isShowing) {
            binding.foodListFabMenu.closeMenu()
        }
    }

    // Adding revealing main_fab button
    private fun addRevealFabBtn() {

        // choose what menu item must be seen based on type of list
        if (foodlistType == FOOD_EXPIRING) {
            binding.foodListFabMenu.removeItem(R.id.menu_expiring_food)

        } else if (foodlistType == FOOD_DEAD) {
            binding.foodListFabMenu.removeItem(R.id.menu_dead_food)

        } else if (foodlistType == FOOD_SAVED) {
            binding.foodListFabMenu.removeItem(R.id.menu_consumed_food)
        }
        try {
            //attach menu to main_fab
            binding.foodListFabMenu.bindAnchorView(binding.foodListFab)

            //set menu selection listener
            binding.foodListFabMenu.setOnFABMenuSelectedListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.foodListFabMenu.menuDirection = Direction.LEFT
    }

    // Revealing main_fab button menu management
    override fun onMenuItemSelected(view: View, id: Int) {
        if (id == R.id.menu_insert) {
            val toInsertFood = Intent(this@FoodListActivity, InsertFoodActivity::class.java)
            startActivity(toInsertFood)

        } else if (id == R.id.menu_expiring_food) {
            showExpiringFood()

        } else if (id == R.id.menu_consumed_food) {
            showRandomizedInterstAds(3, this)
            showSavedFood()

        } else if (id == R.id.menu_dead_food) {
            showRandomizedInterstAds(3, this)
            showDeadFood()

        } else if (id == R.id.menu_home) {
            showRandomizedInterstAds(4, this)
            returnHome()
        }
    }

    private fun returnHome() {
        val returnHome = Intent(this@FoodListActivity, MainActivity::class.java)
        startActivity(returnHome)
    }


    private fun showDeadFood() {
        val showDeadFood = Intent(this@FoodListActivity, FoodListActivity::class.java)
        showDeadFood.putExtra(FOOD_TYPE, FOOD_DEAD)
        startActivity(showDeadFood)
    }

    private fun showSavedFood() {
        val showSavedFood = Intent(this@FoodListActivity, FoodListActivity::class.java)
        showSavedFood.putExtra(FOOD_TYPE, FOOD_SAVED)
        startActivity(showSavedFood)
    }

    private fun showExpiringFood() {
        val showExpiringFood = Intent(this@FoodListActivity, FoodListActivity::class.java)
        showExpiringFood.putExtra(FOOD_TYPE, FOOD_EXPIRING)
        startActivity(showExpiringFood)
    }


}