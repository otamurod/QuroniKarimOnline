package com.otamurod.apicallusingmvvmcoroutines.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.otamurod.apicallusingmvvmcoroutines.adapter.RvAdapter
import com.otamurod.apicallusingmvvmcoroutines.databinding.FragmentRecyclerListBinding
import com.otamurod.apicallusingmvvmcoroutines.models.RecyclerList
import com.otamurod.apicallusingmvvmcoroutines.viewmodel.MainViewModel

class RecyclerListFragment : Fragment() {

    lateinit var binding: FragmentRecyclerListBinding
    lateinit var rvAdapter: RvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecyclerListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initRvAdapter()
        initViewModel()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initRvAdapter() {
        rvAdapter = RvAdapter()
        binding.recyclerView.adapter = rvAdapter
    }

    private fun initViewModel() {
        val viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        val query = "qy"
        viewModel.makeApiCall(query)

        viewModel.getRecyclerListObserver()
            .observe(viewLifecycleOwner, Observer<RecyclerList> { recyclerList ->
                if (recyclerList != null) {
                    rvAdapter.setUpdatedData(recyclerList.items)
                } else {
                    Toast.makeText(activity, "Error getting data", Toast.LENGTH_SHORT).show()
                }
            })

    }
}