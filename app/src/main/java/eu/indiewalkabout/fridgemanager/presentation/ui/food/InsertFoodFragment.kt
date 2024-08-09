package eu.indiewalkabout.fridgemanager.presentation.ui.food

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hlab.fabrevealmenu.enums.Direction
import com.hlab.fabrevealmenu.listeners.OnFABMenuSelectedListener
import eu.indiewalkabout.fridgemanager.FreddyFridgeApplication
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.hideStatusNavBars
import eu.indiewalkabout.fridgemanager.core.util.KeyboardUtils
import eu.indiewalkabout.fridgemanager.core.util.KeyboardUtils.Companion.addKeyboardToggleListener
import eu.indiewalkabout.fridgemanager.core.util.OnSwipeTouchListener
import eu.indiewalkabout.fridgemanager.core.util.extensions.TAG
import eu.indiewalkabout.fridgemanager.databinding.FragmentInsertFoodBinding
import eu.indiewalkabout.fridgemanager.domain.model.FoodEntry
import eu.indiewalkabout.fridgemanager.presentation.ui.intromain.MainActivity
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale

class InsertFoodFragment : Fragment(), CalendarView.OnDateChangeListener,
    OnFABMenuSelectedListener {

    private lateinit var binding: FragmentInsertFoodBinding

    private var myDatePicked: Calendar? = null
    private var foodId = DEFAULT_ID
    lateinit var foodEntryToChange: LiveData<FoodEntry>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentInsertFoodBinding.inflate(inflater, container, false)

        initViews()
        addRevealFabBtn()

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_ID)) {
            foodId = savedInstanceState.getInt(INSTANCE_ID, DEFAULT_ID)
        }

        checkUpdateModeOn()

        hideStatusNavBars(requireActivity())

        binding.insertFoodLayout.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() {
                returnHome()
            }

            override fun onSwipeRight() {
                returnHome()
            }
        })

        binding.calendarCv.setOnDateChangeListener { _, year, month, dayOfMonth ->
            myDatePicked = GregorianCalendar(year, month, dayOfMonth)
            Log.i(TAG, "onSelectedDayChange: done: $myDatePicked")
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        addKeyboardToggleListener(requireActivity(), object :
            KeyboardUtils.SoftKeyboardToggleListener {
            override fun onToggleSoftKeyboard(isVisible: Boolean) {
                Log.d("keyboard", "keyboard visible: $isVisible")
                binding.adView.visibility = if (isVisible) View.INVISIBLE else View.VISIBLE
            }
        })
    }

    private fun initViews() {
        binding.saveBtn.setOnClickListener { onSaveBtnClicked() }
        binding.speakBtn.setOnClickListener { startVoiceInput() }
    }

    override fun onSelectedDayChange(view: CalendarView, year: Int, month: Int, dayOfMonth: Int) {
        myDatePicked = GregorianCalendar(year, month, dayOfMonth)
        Log.i(TAG, "onSelectedDayChange: done: $myDatePicked")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(INSTANCE_ID, foodId)
        super.onSaveInstanceState(outState)
    }

    private fun checkUpdateModeOn() {
        val foodIdArg = arguments?.getInt(ID_TO_BE_UPDATED, DEFAULT_ID) ?: DEFAULT_ID
        if (foodIdArg != DEFAULT_ID) {
            foodId = foodIdArg
            binding.saveBtn.setText(R.string.update_btn_label)

            val viewModel = ViewModelProvider(this).get(FoodsViewModel::class.java)
            foodEntryToChange = viewModel.getFoodEntry(foodId)
            foodEntryToChange.observe(viewLifecycleOwner, Observer { value ->
                populateUI(value)
            })
        }
    }

    private fun onSaveBtnClicked() {
        val foodName = binding.foodNameEt.text.toString()
        val itemsNumStr = binding.howmanyEt.text.toString()
        val itemsNum = if (itemsNumStr.isEmpty()) 1 else itemsNumStr.toInt()

        if (foodName.isEmpty()) {
            Toast.makeText(requireContext(), R.string.food_description_alert, Toast.LENGTH_SHORT).show()
            return
        }

        if (foodId == DEFAULT_ID && myDatePicked == null) {
            Toast.makeText(requireContext(), R.string.food_expdate_alert, Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(requireContext(), "$foodName ${getString(R.string.food_inserted_alert)}", Toast.LENGTH_SHORT).show()

        val repository = (requireContext().applicationContext as FreddyFridgeApplication).repository

        lifecycleScope.launch {
            if (foodId == DEFAULT_ID) {
                val expiringDate = myDatePicked?.time
                for (num in 1..itemsNum) {
                    val foodEntry = FoodEntry(0, "$foodName n. $num", expiringDate!!)
                    repository?.insertFoodEntry(foodEntry)
                }
                startActivity(Intent(requireContext(), MainActivity::class.java))
            } else {
                val expiringDate = myDatePicked?.time ?: Date(binding.calendarCv.date)
                val foodEntry = FoodEntry(foodId, foodName, expiringDate).apply {
                    done = foodEntryToChange.value!!.done
                }
                repository?.updateFoodEntry(foodEntry)
                startActivity(Intent(requireContext(), MainActivity::class.java))
            }
        }
    }

    private fun populateUI(foodEntry: FoodEntry?) {
        if (foodEntry != null) {
            binding.foodNameEt.setText(foodEntry.name)
            binding.calendarCv.date = foodEntry.expiringAt?.time ?: 0
        }
    }

    private fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?")
        }
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT)
        } catch (a: ActivityNotFoundException) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK) {
            val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            binding.foodNameEt.setText(result?.get(0) ?: "retry")
        }
    }

    private fun addRevealFabBtn() {
        binding.insertFoodFabMenu.removeItem(R.id.menu_insert)
        try {
            binding.insertFoodFabMenu.bindAnchorView(binding.insertFoodFab)
            binding.insertFoodFabMenu.setOnFABMenuSelectedListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.insertFoodFabMenu.menuDirection = Direction.LEFT
    }

    override fun onMenuItemSelected(view: View, id: Int) {
        when (id) {
            R.id.menu_expiring_food -> showExpiringFood()
            R.id.menu_consumed_food -> showSavedFood()
            R.id.menu_dead_food -> showDeadFood()
            R.id.menu_home -> returnHome()
        }
    }

    private fun showExpiringFood() {
        val bundle = Bundle().apply { putString(ExpiringFoodFragment.FOOD_TYPE, ExpiringFoodFragment.FOOD_EXPIRING) }
        val fragment = ExpiringFoodFragment().apply { arguments = bundle }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showSavedFood() {
        val bundle = Bundle().apply { putString(ConsumedFoodFragment.FOOD_TYPE, ConsumedFoodFragment.FOOD_SAVED) }
        val fragment = ConsumedFoodFragment().apply { arguments = bundle }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun showDeadFood() {
        val bundle = Bundle().apply { putString(DeadFoodFragment.FOOD_TYPE, DeadFoodFragment.FOOD_DEAD) }
        val fragment = DeadFoodFragment().apply { arguments = bundle }
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun returnHome() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
    }

    companion object {
        const val ID_TO_BE_UPDATED = "id"
        const val INSTANCE_ID = "instanceFoodId"
        const val DEFAULT_ID = -1
        private const val REQ_CODE_SPEECH_INPUT = 100
    }
}
