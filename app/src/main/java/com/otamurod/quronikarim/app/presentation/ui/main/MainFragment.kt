package com.otamurod.quronikarim.app.presentation.ui.main

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
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
import com.otamurod.quronikarim.app.presentation.ui.adapter.ListAdapter
import com.otamurod.quronikarim.app.presentation.ui.adapter.MainAdapter
import com.otamurod.quronikarim.app.presentation.utils.checkNetworkStatus
import com.otamurod.quronikarim.databinding.DialogTitleBinding
import com.otamurod.quronikarim.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(), Toolbar.OnMenuItemClickListener {
    private var instance: MainFragment? = null
    lateinit var binding: FragmentMainBinding
    lateinit var mainAdapter: MainAdapter
    private val viewModel: MainViewModel by viewModels()
    private var languages = arrayListOf<String>()
    private var reciters = arrayListOf<Reciter>()
    private lateinit var alertDialog: AlertDialog
    private lateinit var mPrefs: SharedPreferences
    private lateinit var prefsEditor: SharedPreferences.Editor
    private var language: String? = null
    private var translator: Translator? = null
    private var reciter: Reciter? = null
    private val defaultLang = "ar"
    private val defaultTranslator = Translator("Mishary Rashid Alafasy", "ar.alafasy", "ar")
    private val defaultReciter = Reciter("Mishary Rashid Alafasy", "ar.alafasy")

    fun getInstance() = instance

    fun makeApiCall() {
        requestAllSurahes()
        requestLanguages()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        if (checkNetworkStatus(requireContext(), "main")) {
            makeApiCall()
        } else {
            makeNotifyVisible()
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
            }).check()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        binding.toolbar.setOnMenuItemClickListener(this)
        binding.toolbar.inflateMenu(R.menu.my_menu)

        alertDialog = AlertDialog.Builder(requireContext()).create()
        instance = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        makeNotifyVisible()

        initViewModel()

        // create sharedPreferences
        mPrefs = requireContext().getSharedPreferences("singleton", Context.MODE_PRIVATE)
        // create editor
        prefsEditor = mPrefs.edit()
        val gson = Gson() // create Gson object

        // retrieve translator
        var json = mPrefs.getString("translator", "")
        translator = gson.fromJson(json, Translator::class.java)

        // retrieve reciter
        json = mPrefs.getString("reciter", "")
        reciter = gson.fromJson(json, Reciter::class.java)
        // retrieve language
        language = mPrefs.getString("language", null)
        // Default settings
        if (language == null) {
            language = defaultLang
            storeLanguage()
        } else if (reciter == null) {
            reciter = defaultReciter
            storeReciter()
        } else if (translator == null) {
            translator = defaultTranslator
        }

        initAdapter()
        super.onViewCreated(view, savedInstanceState)
    }

    // function to store language setting
    private fun storeLanguage() {
        //store language
        prefsEditor.putString("language", language)
        prefsEditor.apply()
    }

    // function to store translator setting
    private fun storeTranslator() {
        // create editor
        val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
        val gson = Gson() // create Gson object
        //store translator
        var json = gson.toJson(translator) // convert to json
        prefsEditor.putString("translator", json)
        prefsEditor.apply()
    }

    // function to store reciter setting
    private fun storeReciter() {
        val prefsEditor: SharedPreferences.Editor = mPrefs.edit()
        val gson = Gson() // create Gson object
        //store translator
        var json = gson.toJson(reciter) // convert to json
        prefsEditor.putString("reciter", json)
        prefsEditor.apply()
    }

    // function to set adapter
    private fun initAdapter() {
        mainAdapter = MainAdapter(requireContext(), object : MainAdapter.OnClick {
            override fun onItemClick(surah: Surah) {
                val action =
                    MainFragmentDirections.actionMainFragmentToSurahFragment(
                        surah,
                        translator!!,
                        reciter!!
                    )
                findNavController().navigate(action)
            }
        })
        binding.recyclerView.adapter = mainAdapter
    }

    // function to initialize viewModel
    private fun initViewModel() {
        // observe list of surahes
        viewModel.surahs.observe(viewLifecycleOwner) { data ->
            // hide progressBar when data observed
            makeNotifyInvisible()
            // update adapter with observed data
            mainAdapter.setUpdatedData(data)
        }
        // observe translators
        viewModel.translator.observe(viewLifecycleOwner) { translators ->
            // filter translated editions according to the chosen language
            val identifierList = translators.filter {
                it.language == language
            }
            // show available translations
            showTranslatorDialog(identifierList as ArrayList<Translator>)
        }
        // observe reciters
        viewModel.reciter.observe(viewLifecycleOwner) { list ->
            // get all reciters
            reciters = list as ArrayList<Reciter>
            val reciterNames = arrayListOf<String>()
            // get names of all reciters
            for (reciter in reciters) {
                reciterNames.add(reciter.englishName)
            }
            // show reciters to choose
            showRecitersDialog(reciterNames.toTypedArray())
        }
        // observe error
        viewModel.error.observe(viewLifecycleOwner) { error ->
            // show error to a user
            makeNotifyVisible()
            Toast.makeText(activity, "Error getting data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun makeNotifyVisible() {
        binding.progressBar.visibility = View.VISIBLE
        binding.networkOff.visibility = View.VISIBLE
        binding.errorText.visibility = View.VISIBLE
    }

    private fun makeNotifyInvisible() {
        binding.progressBar.visibility = View.GONE
        binding.networkOff.visibility = View.INVISIBLE
        binding.errorText.visibility = View.INVISIBLE
    }

    // function to request all surahes
    private fun requestAllSurahes() {
        // invoke viewModel function
        viewModel.getSurahesCall()
    }

    // fun to request translated languages
    private fun requestLanguages() {
        // invoke viewModel function
        languages = viewModel.getAllLanguagesCall()
        if (language == null) {
            showLanguageDialog()
        }
    }

    // function to request translators
    private fun requestTranslators() {
        viewModel.getTranslatorsCall()
    }

    // function to request all reciters
    private fun requestReciters() {
        // invoke viewModel function
        viewModel.getRecitersCall()
    }

    // function to pick a language
    private fun showLanguageDialog() {
        val adapter = ListAdapter(
            requireContext(),
            R.layout.list_item,
            languages.toTypedArray(), R.drawable.ic_translate, language
        )
        val builder = AlertDialog.Builder(requireActivity())
        setDialogView(builder, R.string.choose_language)

        alertDialog = requireActivity().let {
            builder.setAdapter(adapter, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    language = languages[which]
                    storeLanguage()
                    requestTranslators()
                }
            })
            builder.create()
        }
        alertDialog.show()

        setDialogLayout()
    }

    // function to pick a translator
    private fun showTranslatorDialog(translatorList: ArrayList<Translator>) {
        val names = arrayListOf<String>()
        // get names of all translators
        for (identifier in translatorList) {
            names.add(identifier.englishName)
        }
        if (language == defaultLang && !names.contains(defaultTranslator.englishName)) {
            translatorList.add(defaultTranslator)
            names.add(defaultTranslator.englishName)
        }
        val builder = AlertDialog.Builder(requireActivity())
        val adapter = ListAdapter(
            requireContext(),
            R.layout.list_item,
            names.toTypedArray(), R.drawable.ic_book, translator?.englishName
        )
        setDialogView(builder, R.string.choose_translation)
        alertDialog = requireActivity().let {
            builder.setAdapter(adapter, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    // filter translators according to their names to get their "identifier"
                    val name = translatorList.filter { it.englishName == names[which] }
                    // get translator
                    translator = name[0]
                    storeTranslator()
                }
            })
            builder.create()
        }
        alertDialog.show()
        setDialogLayout()
    }

    // function to pick a reciter
    private fun showRecitersDialog(recitersName: Array<String>) {
        val builder = AlertDialog.Builder(requireActivity())
        val adapter = ListAdapter(
            requireContext(),
            R.layout.list_item,
            recitersName, R.drawable.ic_man, reciter?.englishName
        )
        setDialogView(builder, R.string.choose_recitation)
        alertDialog = requireActivity().let {
            builder.setAdapter(adapter, object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    // get the selected reciter using his name
                    val selectedReciter = reciters.filter { reciter ->
                        reciter.englishName == recitersName[which]
                    }
                    reciter = selectedReciter[0]
                    storeReciter()
                }
            })
            builder.create()
        }
        alertDialog.show()
        setDialogLayout()
    }

    private fun setDialogView(builder: AlertDialog.Builder, titleId: Int) {
        val dialogViewBinding = DialogTitleBinding.inflate(layoutInflater)
        dialogViewBinding.title.text = getString(titleId)
        builder.setCustomTitle(dialogViewBinding.root)
    }

    private fun setDialogLayout() {
        val width = binding.toolbar.width
        alertDialog.window?.setLayout(width - 50, width + 200)
    }

    // inflate menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.my_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    // handle menu item click
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.translation -> {
                showLanguageDialog()
            }
            R.id.recitation -> {
                requestReciters()
            }
        }
        return true
    }

    // on resume
    override fun onResume() {
        super.onResume()
        makeNotifyInvisible()
        alertDialog.hide()
        alertDialog.dismiss()
        alertDialog.cancel()
    }
}