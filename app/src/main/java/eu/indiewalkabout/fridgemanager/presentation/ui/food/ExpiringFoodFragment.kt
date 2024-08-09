package eu.indiewalkabout.fridgemanager.presentation.ui.food

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.hideStatusNavBars
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.showRandomizedInterstAds
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import eu.indiewalkabout.fridgemanager.databinding.FragmentConsumedFoodBinding
import eu.indiewalkabout.fridgemanager.databinding.FragmentExpiringFoodBinding
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.presentation.components.adapter.FoodListAdapter
import eu.indiewalkabout.fridgemanager.presentation.ui.intromain.MainActivity
import java.text.DateFormat.getDateInstance

class ExpiringFoodFragment : Fragment(), FoodListAdapter.ItemClickListener,
    OnFABMenuSelectedListener {
    private lateinit var binding: FragmentExpiringFoodBinding

    companion object {
        const val FOOD_TYPE = "food_type"
        const val FOOD_EXPIRING = "ExpiringFood"
        const val FOOD_EXPIRING_TODAY = "ExpiringFoodToday"
        const val FOOD_SAVED = "SavedFood"
        const val FOOD_DEAD = "DeadFood"
    }

    private var foodListAdapter: FoodListAdapter? = null
    private var foodListForShare: String = ""
    private var foodShareSubject: String = ""

    private var foodlistType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExpiringFoodBinding.inflate(inflater, container, false)

        // Handle the received arguments (if any)
        arguments?.let {
            foodlistType = it.getString(FOOD_TYPE)
            Log.d(TAG, "onCreateView: FOOD_TYPE : " + foodlistType.toString())
        }

        toolBarInit()
        addRevealFabBtn()
        initRecycleView()

        hideStatusNavBars(requireActivity())

        binding.shareFoodList.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody = foodListForShare
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, foodShareSubject)
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, "Share via"))
        }

        return binding.root
    }

    override fun onItemClickListener(itemId: Int) {
        Log.d(TAG, "onItemClickListener: Item" + itemId + "touched.")
    }

    private fun toolBarInit() {
        setToolBarTitle()
        // place toolbar in place of action bar
        (activity as? AppCompatActivity)?.setSupportActionBar(binding.foodListToolbar)
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setToolBarTitle() {
        when (foodlistType) {
            FOOD_EXPIRING -> {
                binding.toolbarTitleTv.setText(R.string.foodExpiring_activity_title)
                foodShareSubject = getString(R.string.expiring_food_list_subject)
                Log.d(TAG, "onCreateView: FOOD_TYPE : " + R.string.foodExpiring_activity_title)
            }
            FOOD_SAVED -> {
                binding.toolbarTitleTv.setText(R.string.foodSaved_activity_title)
                foodShareSubject = getString(R.string.saved_food_list_subject)
                Log.d(TAG, "onCreateView: FOOD_TYPE : " + R.string.foodSaved_activity_title)
            }
            FOOD_DEAD -> {
                binding.toolbarTitleTv.setText(R.string.foodDead_activity_title)
                foodShareSubject = getString(R.string.wasted_food_list_subject)
                Log.d(TAG, "onCreateView: FOOD_TYPE : " + R.string.foodDead_activity_title)
            }
        }
    }

    private fun initRecycleView() {
        binding.foodListRecycleView.layoutManager = LinearLayoutManager(context)
        foodListAdapter = FoodListAdapter(requireContext(), this, foodlistType!!)
        binding.foodListRecycleView.adapter = foodListAdapter

        setupAdapter()

        binding.foodListRecycleView.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0 || dy < 0 && binding.foodListFab.isShown) binding.foodListFab.hide()
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) binding.foodListFab.show()
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
    }

    private fun setupAdapter() {
        Log.d(TAG, "setupAdapter: LOAD FOOD ENTRIES IN LIST ")
        retrieveAllFood()
        Log.d(TAG, "setupAdapter: FOOD_TYPE : $foodlistType")
    }

    private fun retrieveAllFood() {
        Log.d(TAG, "Actively retrieving Expiring Food from DB")

        val factory = FoodsViewModelFactory(foodlistType!!)

        val viewModel = ViewModelProvider(this, factory).get(FoodsViewModel::class.java)

        viewModel.foodList?.observe(viewLifecycleOwner, Observer<MutableList<FoodEntry>?> { foodEntries ->
            Log.d(TAG, "Receiving database $foodlistType LiveData")
            foodListAdapter!!.setFoodEntries(foodEntries)
            if (foodEntries!!.isNotEmpty()) {
                binding.emptyListText.visibility = View.INVISIBLE
                binding.shareFoodList.visibility = View.VISIBLE

                foodListForShare += "$foodShareSubject \n\n"
                for (item in foodEntries) {
                    foodListForShare += "${item.name} :  ${getDateInstance().format(item.expiringAt)}\n\n"
                }
            } else {
                binding.emptyListText.visibility = View.VISIBLE
                binding.shareFoodList.visibility = View.INVISIBLE
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == android.R.id.home) {
            showRandomizedInterstAds(4, requireActivity())
            activity?.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onMenuItemSelected(view: View, id: Int) {
        when (id) {
            R.id.menu_insert -> {
                val toInsertFood = Intent(requireContext(), InsertFoodActivity::class.java)
                startActivity(toInsertFood)
            }
            R.id.menu_expiring_food -> showExpiringFood()
            R.id.menu_consumed_food -> {
                showRandomizedInterstAds(3, requireActivity())
                showSavedFood()
            }
            R.id.menu_dead_food -> {
                showRandomizedInterstAds(3, requireActivity())
                showDeadFood()
            }
            R.id.menu_home -> {
                showRandomizedInterstAds(4, requireActivity())
                returnHome()
            }
        }
    }

    private fun returnHome() {
        val returnHome = Intent(requireContext(), MainActivity::class.java)
        startActivity(returnHome)
    }

    private fun showDeadFood() {
        val showDeadFood = Intent(requireContext(), FoodListFragment::class.java)
        showDeadFood.putExtra(FOOD_TYPE, FOOD_DEAD)
        startActivity(showDeadFood)
    }

    private fun showSavedFood() {
        val showSavedFood = Intent(requireContext(), FoodListFragment::class.java)
        showSavedFood.putExtra(FOOD_TYPE, FOOD_SAVED)
        startActivity(showSavedFood)
    }

    private fun showExpiringFood() {
        val showExpiringFood = Intent(requireContext(), FoodListFragment::class.java)
        showExpiringFood.putExtra(FOOD_TYPE, FOOD_EXPIRING)
        startActivity(showExpiringFood)
    }

    private fun addRevealFabBtn() {
        when (foodlistType) {
            FOOD_EXPIRING -> binding.foodListFabMenu.removeItem(R.id.menu_expiring_food)
            FOOD_DEAD -> binding.foodListFabMenu.removeItem(R.id.menu_dead_food)
            FOOD_SAVED -> binding.foodListFabMenu.removeItem(R.id.menu_consumed_food)
        }

        try {
            binding.foodListFabMenu.bindAnchorView(binding.foodListFab)
            binding.foodListFabMenu.setOnFABMenuSelectedListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        binding.foodListFabMenu.menuDirection = Direction.LEFT
    }
}