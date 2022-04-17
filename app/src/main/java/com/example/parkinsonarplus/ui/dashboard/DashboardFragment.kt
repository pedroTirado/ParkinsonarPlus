package com.example.parkinsonarplus.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

        val textView: TextView = binding.textDashboard
        dashboardViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        println("HomeViewModel.gyroX: ${viewModel.gyroX.value}")
        println("HomeViewModel.gyroY: ${viewModel.gyroY.value}")
        println("HomeViewModel.gyroZ: ${viewModel.gyroZ.value}")

        val textViewX: TextView = binding.textGyroX
        val textViewY: TextView = binding.textGyroY
        val textViewZ: TextView = binding.textGyroZ

        viewModel.gyroX.observe(viewLifecycleOwner) {
            textViewX.text = it.toString()
        }
        viewModel.gyroY.observe(viewLifecycleOwner) {
            textViewY.text = it.toString()
        }
        viewModel.gyroZ.observe(viewLifecycleOwner) {
            textViewZ.text = it.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}