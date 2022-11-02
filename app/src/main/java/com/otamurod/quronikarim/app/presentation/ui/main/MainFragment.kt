package com.otamurod.quronikarim.app.presentation.ui.main

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.otamurod.quronikarim.R
import com.otamurod.quronikarim.app.domain.model.identifier.Identifier
import com.otamurod.quronikarim.app.domain.model.surah.Surah
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
    private var languages = Array(200, { "" })
    private var language = ""
    private var identifier = "ar.alafasy"
//    private var identifier = "uz.sodik"

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        if (checkNetworkStatus(requireContext())) {
            requestLanguages()
            requestAllSurahs()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
            override fun onItemClick(surah: Surah) {
                val action =
                    MainFragmentDirections.actionMainFragmentToSurahFragment(surah, identifier)
                findNavController().navigate(action)
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
        viewModel.identifier.observe(viewLifecycleOwner) { identifiers ->
            val identifierList = identifiers.filter {
                it.language == language
            }
            showIdentifierDialog(identifierList)
        }
    }

    fun requestAllSurahs() {
        viewModel.getSurahesCall()
    }

    private fun requestLanguages() {
        languages = viewModel.getAllLanguagesCall().toTypedArray()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.my_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.translation -> {
                showLanguageDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLanguageDialog() {
        val alertDialog = activity?.let {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(R.string.choose_translation)
                .setItems(languages, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        language = languages[which]

                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.getIdentifiersCall()
                        }
                    }
                })
            builder.create()
        }
        alertDialog?.show()
    }

    private fun showIdentifierDialog(identifierList: List<Identifier>) {
        val names = ArrayList<String>()
        for (identifier in identifierList) {
            names.add(identifier.englishName)
        }
        val alertDialog = activity?.let {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(R.string.choose_translation)
                .setItems(names.toTypedArray(), object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val name = identifierList.filter { it.englishName == names[which] }
                        identifier = name[0].identifier
                    }
                })
            builder.create()
        }
        alertDialog?.show()
    }

    override fun onResume() {
        super.onResume()
        binding.progressBar.visibility = View.VISIBLE
    }
}