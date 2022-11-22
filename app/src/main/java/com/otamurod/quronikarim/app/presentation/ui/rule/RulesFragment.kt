package com.otamurod.quronikarim.app.presentation.ui.rule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.otamurod.quronikarim.R
import com.otamurod.quronikarim.databinding.FragmentRulesBinding

class RulesFragment : Fragment() {
    lateinit var binding: FragmentRulesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentRulesBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.confirm.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.MainFragment)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        requireActivity().title = getString(R.string.rule)
        super.onResume()
    }
}