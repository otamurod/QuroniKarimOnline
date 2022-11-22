package com.otamurod.quronikarim.app.presentation.ui.about

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.otamurod.quronikarim.R
import com.otamurod.quronikarim.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {
    lateinit var binding: FragmentAboutBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAboutBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.share.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.type = "text/plain"
            val shareBody =
                getString(R.string.link) + " : https://play.google.com/store/apps/details?id=com.otamurod.quronikarim"
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_link))
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)))
        }

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        requireActivity().title = getString(R.string.ic_about)

        super.onResume()
    }
}