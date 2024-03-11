package eu.indiewalkabout.fridgemanager.presentation.ui.intromain

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.unityads.bannerListener
import eu.indiewalkabout.fridgemanager.core.util.OnSwipeTouchListener
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import eu.indiewalkabout.fridgemanager.databinding.FragmentMainBinding
import eu.indiewalkabout.fridgemanager.presentation.components.adapter.FoodListAdapter
import eu.indiewalkabout.fridgemanager.presentation.ui.food.FoodListActivity
import eu.indiewalkabout.fridgemanager.presentation.ui.food.FoodsViewModel
import eu.indiewalkabout.fridgemanager.presentation.ui.food.FoodsViewModelFactory
import eu.indiewalkabout.fridgemanager.presentation.ui.food.InsertFoodActivity
import eu.indiewalkabout.fridgemanager.presentation.ui.settings.MainSettingsActivity

class MainFragment : Fragment(), FoodListAdapter.ItemClickListener {

    private lateinit var binding: FragmentMainBinding
    // recycle View stuff
    private lateinit var foodListAdapter: FoodListAdapter
    private var foodListForShare: String = ""
    // unity bottom ads
    private var bottomBanner: BannerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentMainBinding.inflate(inflater)
        return binding.apply {
            lifecycleOwner = this@MainFragment
        }.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // set up fragment
        initRecycleView()

        binding.settingsIconImg.setOnClickListener {
            val settingsIntent = Intent(requireContext(), MainSettingsActivity::class.java)
            startActivity(settingsIntent)
        }


        binding.helpImg.setOnClickListener {
            val introIntent = Intent(requireContext(), IntroActivity::class.java)
            startActivity(introIntent)
        }

        binding.shareImg.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = foodListForShare
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_today_title))
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }

        // goto to insert on swipe left/right
        /*binding.fragmentHomeLayout.setOnTouchListener(object :
            OnSwipeTouchListener(requireActivity()) {
            override fun onSwipeLeft() {
                toInsertFood()
            }

            override fun onSwipeRight() {
                toInsertFood()
            }
        })*/

        /**/binding.todayFoodListRecycleView.setOnTouchListener(object :
            OnSwipeTouchListener(requireActivity()) {
            override fun onSwipeLeft() {
                toInsertFood()
            }

            override fun onSwipeRight() {
                toInsertFood()
            }
        })


        // Load an ad into the AdMob banner view.
        /*val adView: AdView = binding.adView
        val adRequest = AdRequest.Builder()
            .setRequestAgent("android_studio:ad_template").build()
        adView.loadAd(adRequest)*/
    }

    override fun onStart() {
        super.onStart()
        showAds()
    }


    private fun showAds() {
        /*checkConsentActive = consentSDKNeed
        if (checkConsentActive == true) {
            // Initialize ConsentSDK
            consentObjReference = ConsentSDK.Builder(this) // .addTestDeviceId("7DC1A1E8AEAD7908E42271D4B68FB270") // redminote 5 // Add your test device id "Remove addTestDeviceId on production!"
                    // .addTestDeviceId("9978A5F791A259430A0156313ED9C6A2")
                    .addCustomLogTag("gdpr_TAG") // Add custom tag default: ID_LOG
                    .addPrivacyPolicy("http://www.indie-walkabout.eu/privacy-policy-app") // Add your privacy policy url
                    .addPublisherId("pub-8846176967909254") // Add your admob publisher id
                    .build()

            // To check the consent and load ads
            consentObjReference!!.checkConsent(object : ConsentCallback() {
                override fun onResult(isRequestLocationInEeaOrUnknown: Boolean) {
                    Log.i("gdpr_TAG", "onResult: isRequestLocationInEeaOrUnknown : $isRequestLocationInEeaOrUnknown")
                    // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
                    binding.adView.loadAd(getAdRequest(this@MainActivity))
                }
            })

            // You have to pass the AdRequest from ConsentSDK.getAdRequest(this) because it handle the right way to load the ad
            binding.adView.loadAd(getAdRequest(this@MainActivity))
        }*/

        // ads banner
        // val bannerWidthDp = getScreenWidthDp(this) - (2 * marginDp) // Subtract margins from screen width
        bottomBanner = BannerView(requireActivity(), "banner",
            UnityBannerSize(320, 50))
        /*val bannerView = BannerView(this, "banner", UnityBannerSize(bannerWidthDp, 50))
        val layoutParams = ViewGroup.LayoutParams(
            dpToPx(this, bannerWidthDp), // Convert dp to pixels
            dpToPx(this, 50) // Set the height in pixels or dp as needed
        )
        bannerView.layoutParams = layoutParams*/

        bottomBanner?.listener = bannerListener
        bottomBanner?.load()
        binding.bannerLayout.addView(bottomBanner)
    }

    // Recycle touch an item callback to update/modify task
    override fun onItemClickListener(itemId: Int) {
        Log.d(TAG, "onItemClickListener: Item" + itemId + "touched.")
    }


    // Recycle view list init
    private fun initRecycleView() {
        binding.todayFoodListRecycleView.setLayoutManager(LinearLayoutManager(requireContext()))

        foodListAdapter = FoodListAdapter(
            requireActivity(), this,
            FoodListActivity.FOOD_EXPIRING_TODAY
        )
        binding.todayFoodListRecycleView.setAdapter(foodListAdapter)

        setupAdapter()

    }


    // Used to reload from db the tasks list and update the list view in screen
    private fun setupAdapter() {
        Log.d(TAG, "setupAdapter: LOAD FOOD ENTRIES IN LIST ")
        retrieveFoodExpiringToday()
        Log.d(TAG, "setupAdapter: FOOD_TYPE : " + FoodListActivity.FOOD_EXPIRING_TODAY)
    }


    // Livedata/ViewModel recovering Expiring Today Food list
    private fun retrieveFoodExpiringToday() {
        Log.d(TAG, "Actively retrieving Expiring Food from DB")

        val factory = FoodsViewModelFactory(FoodListActivity.FOOD_EXPIRING_TODAY)
        val viewModel = ViewModelProvider(this, factory).get(FoodsViewModel::class.java)

        // Observe changes in data through LiveData: getFoodList() actually return LiveData<List<FoodEntry>>
        val foods = viewModel.foodList
        foods!!.observe(requireActivity(), Observer { foodEntries ->
            Log.d(TAG, "Receiving database " + FoodListActivity.FOOD_EXPIRING_TODAY + " LiveData")
            foodListAdapter.adapterFoodEntries = foodEntries
            if (foodEntries!!.size > 0) {
                binding.emptyListText.visibility = View.INVISIBLE
                binding.shareImg.visibility = View.VISIBLE
                // fill the list for sharing
                foodListForShare += "${getString(R.string.share_today_title)} \n"
                for (item in foodEntries) {
                    foodListForShare += "${item.name} \n"
                }
            } else {
                binding.emptyListText.visibility = View.VISIBLE
                binding.shareImg.visibility = View.INVISIBLE
            }
        })
    }

    private fun toInsertFood() {
        val toInsertFood = Intent(requireActivity(), InsertFoodActivity::class.java)
        startActivity(toInsertFood)
    }

    companion object {
        // Remove the below line after defining your own ad unit ID.
        /*private const val TOAST_TEXT =
            "Test ads are being shown. " +
                    "To show live ads, replace the ad unit ID in res/values/strings.xml with your own ad unit ID."*/
    }

    /*override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }*/
}