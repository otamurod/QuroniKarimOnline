package com.otamurod.apicallusingmvvmcoroutines.fragment

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
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
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRecyclerListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initRvAdapter()
        initViewModel()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initRvAdapter() {
        rvAdapter = RvAdapter()
        binding.recyclerView.adapter = rvAdapter
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        checkNetworkStatus()

        viewModel.getRecyclerListObserver()
            .observe(viewLifecycleOwner, Observer<RecyclerList> { recyclerList ->
                if (recyclerList != null) {
                    rvAdapter.setUpdatedData(recyclerList.data)
                } else {
                    Toast.makeText(activity, "Error getting data", Toast.LENGTH_SHORT).show()
                }
            })

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun checkNetworkStatus() {

        val conMgr =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as (ConnectivityManager)
        val activeNetwork = conMgr.activeNetworkInfo;
        if (activeNetwork != null && activeNetwork.isConnected) {
            // notify user you are online
            Toast.makeText(activity, "Network State: Online", Toast.LENGTH_SHORT).show()
            makeAPICall()
        } else {
            // notify user you are not online
            Toast.makeText(activity, "Network State: Offline", Toast.LENGTH_LONG).show()
        }

        networkChangeListener()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun networkChangeListener() {
        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.let {
            it.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    //take action when network connection is gained
                    Toast.makeText(context, "Back Online", Toast.LENGTH_SHORT).show()
                    makeAPICall()
                }

                override fun onLost(network: Network) {
                    //take action when network connection is lost
                    Toast.makeText(context, "You are Offline", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun makeAPICall() {
        val query = ""
        viewModel.makeApiCall(query)
    }
}