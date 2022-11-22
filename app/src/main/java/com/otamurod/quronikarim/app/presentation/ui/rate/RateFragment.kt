package com.otamurod.quronikarim.app.presentation.ui.rate

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.otamurod.quronikarim.R
import com.otamurod.quronikarim.databinding.FragmentRateBinding

class RateFragment : Fragment() {
    lateinit var binding: FragmentRateBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rate.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data =
                Uri.parse("https://play.google.com/store/apps/details?id=com.otamurod.quronikarim")
            startActivity(intent)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        requireActivity().title = getString(R.string.ic_rate)
        super.onResume()
    }
}