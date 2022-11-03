package com.otamurod.quronikarim.app.presentation.ui.main

import android.Manifest
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
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import com.otamurod.quronikarim.R
import com.otamurod.quronikarim.app.domain.model.reciter.Reciter
import com.otamurod.quronikarim.app.domain.model.surah.Surah
import com.otamurod.quronikarim.app.domain.model.translator.Translator
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
    private var reciters = ArrayList<Reciter>()
    private var language = ""
    private var translator = Translator("Muhammad Sodik Muhammad Yusuf", "uz.sodik", "uz")
    private var reciter = Reciter("Mishary Rashid Alafasy", "ar.alafasy")

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
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) { /* ... */
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) { /* ... */
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest?,
                    token: PermissionToken?
                ) { /* ... */
                }
            }).check()    }

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
                    MainFragmentDirections.actionMainFragmentToSurahFragment(
                        surah,
                        translator,
                        reciter
                    )
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
        viewModel.translator.observe(viewLifecycleOwner) { translators ->
            val identifierList = translators.filter {
                it.language == language
            }
            showTranslatorDialog(identifierList)
        }

        viewModel.reciter.observe(viewLifecycleOwner) { reciters ->
            this.reciters = reciters as ArrayList<Reciter>
            val reciterNames = arrayListOf<String>()
            for (reciter in reciters) {
                reciterNames.add(reciter.englishName)
            }
            showRecitersDialog(reciterNames.toTypedArray())
        }
    }

    private fun requestAllSurahs() {
        viewModel.getSurahesCall()
    }

    private fun requestReciters() {
        viewModel.getRecitersCall()
    }

    private fun requestLanguages() {
        languages = viewModel.getAllLanguagesCall().toTypedArray()
    }

    private fun showLanguageDialog() {
        val alertDialog = activity?.let {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(R.string.choose_translation)
                .setItems(languages, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        language = languages[which]

                        lifecycleScope.launch(Dispatchers.IO) {
                            viewModel.getTranslatorsCall()
                        }
                    }
                })
            builder.create()
        }
        alertDialog?.show()
    }

    private fun showTranslatorDialog(translatorList: List<Translator>) {
        val names = ArrayList<String>()
        for (identifier in translatorList) {
            names.add(identifier.englishName)
        }
        val alertDialog = activity?.let {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(R.string.choose_translation)
                .setItems(names.toTypedArray(), object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val name = translatorList.filter { it.englishName == names[which] }
                        translator = name[0]
                    }
                })
            builder.create()
        }
        alertDialog?.show()
    }

    private fun showRecitersDialog(recitersName: Array<String>) {
        val alertDialog = activity?.let {
            val builder = AlertDialog.Builder(requireActivity())
            builder.setTitle(R.string.choose_recitation)
                .setItems(recitersName, object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val selectedReciter = reciters.filter { reciter ->
                            reciter.englishName == recitersName[which]
                        }
                        reciter = selectedReciter[0]
                    }
                })
            builder.create()
        }
        alertDialog?.show()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.my_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.translation -> {
                showLanguageDialog()
            }
            R.id.recitation -> {
                if (checkNetworkStatus(requireContext())) {
                    requestReciters()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        binding.progressBar.visibility = View.VISIBLE
    }
}