package eu.indiewalkabout.fridgemanager.presentation.ui.food

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.hideStatusNavBars
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import eu.indiewalkabout.fridgemanager.databinding.FragmentConsumedFoodBinding
import eu.indiewalkabout.fridgemanager.presentation.components.adapter.FoodListAdapter
import java.text.DateFormat.getDateInstance

class ConsumedFoodFragment : Fragment(), FoodListAdapter.ItemClickListener {
    private lateinit var binding: FragmentConsumedFoodBinding
    private val consumedFoodViewModel: ConsumedFoodViewModel by viewModels()

    private var foodListAdapter: FoodListAdapter? = null
    private var foodListForShare: String = ""
    private var foodShareSubject: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConsumedFoodBinding.inflate(inflater)
        return binding.apply {
            consumedFoodFragmentVM = consumedFoodFragmentVM
            lifecycleOwner = this@ConsumedFoodFragment
        }.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController()
                        .navigate(R.id.action_consumedFoodFragment_to_mainFragment)
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        consumedFoodViewModel.getFoodList() // retrieve consumed food list from db
        setupObservers()
        setupFrag()
    }

    private fun setupObservers() {
        consumedFoodViewModel.foodList?.observe(viewLifecycleOwner) { foodEntries ->
            if (foodEntries != null) {
                foodListAdapter!!.setFoodEntries(foodEntries)
                if (foodEntries.isNotEmpty()) {
                    binding.emptyListText.visibility = View.INVISIBLE
                    binding.shareFoodList.visibility = View.VISIBLE

                    foodListForShare += "$foodShareSubject \n\n"
                    for (item in foodEntries) {
                        foodListForShare += "${item.name} :  " +
                                "${item.expiringAt?.let { getDateInstance().format(it) }}\n\n"
                    }
                } else {
                    binding.emptyListText.visibility = View.VISIBLE
                    binding.shareFoodList.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setupFrag() {
        hideStatusNavBars(requireActivity())

        foodShareSubject = getString(R.string.saved_food_list_subject)
        initRecycleView()

        binding.apply {
            toolbarTitleTv.setText(R.string.foodSaved_activity_title)

            shareFoodList.setOnClickListener {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                val shareBody = foodListForShare
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, foodShareSubject)
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
                startActivity(Intent.createChooser(sharingIntent, "Share via"))
            }
        }
    }

    override fun onItemClickListener(itemId: Int) {
        Log.d(TAG, "onItemClickListener: Item" + itemId + "touched.")
    }


    private fun initRecycleView() {
        foodListAdapter = FoodListAdapter(
            requireContext(), this,
            "SavedFood"
        )
        binding.apply{
            foodListRecycleView.layoutManager = LinearLayoutManager(context)
            foodListRecycleView.adapter = foodListAdapter
            // setupAdapter()

            foodListRecycleView.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (dy > 0 || dy < 0 && foodListFab.isShown) foodListFab.hide()
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) foodListFab.show()
                        super.onScrollStateChanged(recyclerView, newState)
                    }
                })
        }
    }

    /*private fun setupAdapter() {
        Log.d(TAG, "setupAdapter: LOAD FOOD ENTRIES IN LIST ")
        retrieveAllFood()
    }

    private fun retrieveAllFood() {
        Log.d(TAG, "Actively retrieving Expiring Food from DB")

        // val factory = FoodsViewModelFactory(foodlistType!!)



        consumedFoodViewModel.foodList?.observe(viewLifecycleOwner,
            Observer<MutableList<FoodEntry>?> { foodEntries ->
            Log.d(TAG, "Receiving database CONSUMED FOOD LiveData")
            foodListAdapter!!.setFoodEntries(foodEntries)
            if (foodEntries!!.isNotEmpty()) {
                binding.emptyListText.visibility = View.INVISIBLE
                binding.shareFoodList.visibility = View.VISIBLE

                foodListForShare += "$foodShareSubject \n\n"
                for (item in foodEntries) {
                    foodListForShare += "${item.name} :  " +
                            "${getDateInstance().format(item.expiringAt)}\n\n"
                }
            } else {
                binding.emptyListText.visibility = View.VISIBLE
                binding.shareFoodList.visibility = View.INVISIBLE
            }
        })
    }*/

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
    }*/

    /*private fun returnHome() {
        val returnHome = Intent(requireContext(), MainActivity::class.java)
        startActivity(returnHome)
    }*/

    /*private fun showDeadFood() {
        val showDeadFood = Intent(requireContext(), FoodListFragment::class.java)
        showDeadFood.putExtra(FOOD_TYPE, FOOD_DEAD)
        startActivity(showDeadFood)
    }*/

    private fun showSavedFood() {
        // TODO : show list
        /*val showSavedFood = Intent(requireContext(), FoodListFragment::class.java)
        showSavedFood.putExtra(FOOD_TYPE, FOOD_SAVED)
        startActivity(showSavedFood)*/
    }

    /*private fun showExpiringFood() {
        val showExpiringFood = Intent(requireContext(), FoodListFragment::class.java)
        showExpiringFood.putExtra(FOOD_TYPE, FOOD_EXPIRING)
        startActivity(showExpiringFood)
    }*/

    /*private fun addRevealFabBtn() {
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
    }*/
}