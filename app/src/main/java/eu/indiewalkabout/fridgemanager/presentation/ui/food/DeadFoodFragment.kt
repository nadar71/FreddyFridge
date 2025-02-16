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
import eu.indiewalkabout.fridgemanager.core.data.locals.Globals.FOOD_DEAD
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.hideStatusNavBars
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import eu.indiewalkabout.fridgemanager.databinding.FragmentDeadFoodBinding
import eu.indiewalkabout.fridgemanager.presentation.components.adapter.FoodListAdapter
import java.text.DateFormat.getDateInstance

class DeadFoodFragment : Fragment(), FoodListAdapter.ItemClickListener {
    private lateinit var binding: FragmentDeadFoodBinding
    private val deadFoodViewModel: DeadFoodViewModel by viewModels()

    private var foodListAdapter: FoodListAdapter? = null
    private var foodListForShare: String = ""
    private var foodShareSubject: String = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeadFoodBinding.inflate(inflater)
        return binding.apply {
            deadFoodVM = deadFoodViewModel
            lifecycleOwner = this@DeadFoodFragment
        }.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController()
                        .navigate(R.id.action_deadFoodFragment_to_mainFragment)
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deadFoodViewModel.getFoodList() // retrieve consumed food list from db
        setupObservers()
        setupFrag()
    }

    private fun setupObservers() {
        deadFoodViewModel.foodList?.observe(viewLifecycleOwner) { foodEntries ->
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

        foodShareSubject = getString(R.string.wasted_food_list_subject)
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
            FOOD_DEAD
        )
        binding.apply {
            foodListRecycleView.layoutManager = LinearLayoutManager(context)
            foodListRecycleView.adapter = foodListAdapter

            foodListRecycleView.addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if (dy > 0 || dy < 0 && foodListFab.isShown) foodListFab.hide()
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) foodListFab.show() // TODO: delete
                        super.onScrollStateChanged(recyclerView, newState)
                    }
                }
            )
        }
    }
}
