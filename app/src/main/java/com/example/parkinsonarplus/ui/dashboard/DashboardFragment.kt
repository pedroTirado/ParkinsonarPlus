package com.example.parkinsonarplus.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.parkinsonarplus.databinding.FragmentDashboardBinding
import com.example.parkinsonarplus.ui.home.HomeFragment
import com.example.parkinsonarplus.ui.home.HomeViewModel

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.editTextTextEmailAddress
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        var isFlat: Boolean = false // device is not in flat orientation
        var isShaky: Boolean = false // linear accel. is significant

        val textView0: TextView = binding.textFlat
        val textView1: TextView = binding.textAccel
        val textView2: TextView = binding.textTremor

        if (viewModel.atRest.value == true) {
            if (viewModel.gravZ.value!! > 9.0F) {
                textView0.text = "Yes"
                isFlat = true
            } else {
                textView0.text = "No"
            }
            if (viewModel.laccelMagn.value!! > 1.0F) {
                textView1.text = "Severe"
                isShaky = true
            } else if (viewModel.laccelMagn.value!! > 0.7F) {
                textView1.text = "Moderate"
                isShaky = true
            } else if (viewModel.laccelMagn.value!! > 0.5F) {
                textView1.text = "Mild"
                isShaky = true
            } else {
                textView1.text = "None"
            }

            if (isFlat) {
                if (isShaky) {
                    textView2.text = "Yes"
                } else {
                    textView2.text = "No"
                }
            } else {
                Toast.makeText(this.requireContext(), "Make sure your device screen is facing\n straight up when you record!", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this.requireContext(), "You must first record device on stationary surface!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}