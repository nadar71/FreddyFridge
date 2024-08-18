package eu.indiewalkabout.fridgemanager.presentation.ui.credits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import eu.indiewalkabout.fridgemanager.R
import eu.indiewalkabout.fridgemanager.core.util.GenericUtility.hideStatusNavBars
import eu.indiewalkabout.fridgemanager.databinding.FragmentCreditsBinding


class CreditsFragment : Fragment() {

    private lateinit var binding: FragmentCreditsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreditsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBarInit()
        hideStatusNavBars(requireActivity())
    }

    // Toolbar init
    private fun toolBarInit() {
        // get the toolbar
        val creditsToolbar: Toolbar =binding.creditsToolbar
        val toolbarTitle: TextView = binding.creditsToolbarTitleTv
        toolbarTitle.text = getString(R.string.credits_btn_title)

        // Set toolbar to activity
        (activity as AppCompatActivity).setSupportActionBar(creditsToolbar)

        // get a support action bar
        val actionBar = (activity as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onDestroyView() {
        super.onDestroyView()
    }
}