package com.otamurod.quronikarim.app.presentation.ui.main

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.otamurod.quronikarim.R
import com.otamurod.quronikarim.app.presentation.ui.adapter.MainAdapter
import com.otamurod.quronikarim.app.presentation.utils.checkNetworkStatus
import com.otamurod.quronikarim.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainFragment : Fragment() {
    lateinit var binding: FragmentMainBinding
    lateinit var mainAdapter: MainAdapter
    private val viewModel: MainViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        if (checkNetworkStatus(requireContext())) {
            lifecycleScope.launch(Dispatchers.IO) {
                requestAllSurahs()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.progressBar.visibility = View.VISIBLE
        initAdapter()
        initViewModel()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initAdapter() {
        mainAdapter = MainAdapter(requireContext(), object : MainAdapter.OnClick {
            override fun onItemClick(surahNumber: Int) {
                val bundle = bundleOf("surahNumber" to surahNumber, "identifier" to "ar.alafasy")
                findNavController().navigate(R.id.surahFragment, bundle)
            }
        })
        binding.recyclerView.adapter = mainAdapter
    }

    private fun initViewModel() {
        viewModel.surahs.observe(viewLifecycleOwner) { data ->
            binding.progressBar.visibility = View.GONE
            mainAdapter.setUpdatedData(data)
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            Toast.makeText(activity, "Error getting data", Toast.LENGTH_SHORT).show()
        }
    }

    fun requestAllSurahs() {
        viewModel.getSurahesCall()
    }

    override fun onResume() {
        super.onResume()
        binding.progressBar.visibility = View.VISIBLE
    }
}