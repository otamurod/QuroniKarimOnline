package com.otamurod.quronikarim.app.presentation.ui.surah

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.otamurod.quronikarim.R
import com.otamurod.quronikarim.app.domain.model.audio.SurahAudio
import com.otamurod.quronikarim.app.domain.model.reciter.Reciter
import com.otamurod.quronikarim.app.domain.model.surah.Surah
import com.otamurod.quronikarim.app.domain.model.translator.Translator
import com.otamurod.quronikarim.app.presentation.utils.checkNetworkStatus
import com.otamurod.quronikarim.app.presentation.utils.snackBar
import com.otamurod.quronikarim.databinding.FragmentSurahBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@AndroidEntryPoint
class SurahFragment : Fragment() {
    private var instance: SurahFragment? = null
    lateinit var binding: FragmentSurahBinding
    private val viewModel: SurahViewModel by viewModels()
    private lateinit var surah: Surah
    private lateinit var translator: Translator
    private lateinit var reciter: Reciter
    private lateinit var language: String
    private var mediaPlayer: MediaPlayer? = null
    private var surahAudioUrlBySurah: String? = null
    lateinit var handler: Handler
    private var endTime = "00:00:00"
    private var currentTime = "00:00:00"
    private var audioDuration: Int = 0
    private var listOfAyahsText = ArrayList<String>()
    private var ayahsDurations = ArrayList<Int>()
    private var currentDuration: Int? = null
    private var stringBuilder = StringBuilder()
    private var isSeekBarChanged = false
    private var isAudioObserved = false
    private val args by navArgs<SurahFragmentArgs>()
    private var audioPath: String? = null
    private var downloadId: Long? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        makeApiCall()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun makeApiCall() {
        try {
            // Make API Call
            if (checkNetworkStatus(requireContext())) {
                requestTranslation()
            } else {
                makeNotifyVisible()
            }
        } catch (e: Exception) {
            makeNotifyVisible()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSurahBinding.inflate(layoutInflater, container, false)
        instance = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        releaseMP()
        surah = args.surah
        translator = args.translator
        reciter = args.reciter
        language = translator.language
        setSurahInfo(surah)
        initViewModel() // init viewModel

        handler = Handler(Looper.getMainLooper())
        var size = 22f
        binding.increaseText.setOnClickListener {
            size += 2
            binding.surah.textSize = size
        }
        binding.decreaseText.setOnClickListener {
            size -= 2
            binding.surah.textSize = size
        }

        audioPath =
            "/storage/emulated/0/Music/${getString(R.string.app_name)}/${reciter.identifier}/${surah.number}.mp3"
        setUpPlayerSource(audioPath!!)

        // set retry button click listener
        binding.retry.setOnClickListener {
            makeApiCall()
        }
        // set click listener for card
        var isCardClicked = false
        binding.card.setOnClickListener {
            if (!isCardClicked) {
                binding.translatorTv.visibility = View.VISIBLE
                binding.reciterTv.visibility = View.VISIBLE
                binding.languageCard.visibility = View.VISIBLE
                isCardClicked = true
            } else {
                binding.translatorTv.visibility = View.GONE
                binding.reciterTv.visibility = View.GONE
                binding.languageCard.visibility = View.GONE
                isCardClicked = false
            }
        }
        // set click listener for surah text view
        var isSurahTextClicked = false
        binding.surah.setOnClickListener {
            if (!isSurahTextClicked) {
                binding.decreaseText.visibility = View.VISIBLE
                binding.increaseText.visibility = View.VISIBLE
                isSurahTextClicked = true
            } else {
                binding.decreaseText.visibility = View.GONE
                binding.increaseText.visibility = View.GONE
                isSurahTextClicked = false
            }
        }
        // Download audio on clicked
        binding.download.setOnClickListener {
            lifecycleScope.launch {
                downloadAudio(surah.number, reciter.identifier)
            }
        }
        // Play Audio Button Listener
        binding.playBtn.setOnClickListener {
            if (!isAudioObserved) {
                Toast.makeText(requireContext(), "Please Download Audio", Toast.LENGTH_SHORT).show()
            } else if (mediaPlayer == null) {
                // do something
//                setUpPlayerSource(audioPath)
            } else if (mediaPlayer?.isPlaying!! && isAudioObserved) { // playing, icon is pause
                binding.playBtn.setImageResource(R.drawable.ic_play_arrow)
                mediaPlayer?.pause()
            } else if (!mediaPlayer?.isPlaying!! && isAudioObserved) { // not playing, icon is play
                binding.playBtn.setImageResource(R.drawable.ic_pause)
                mediaPlayer?.start()
            }
        }
        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    if (mediaPlayer != null) {
                        isSeekBarChanged = true
                        mediaPlayer?.seekTo(binding.seekbar.progress)
                        stringBuilder.clear()
                        stringBuilder.setLength(0)
                        var index = 0
                        var time = -1
                        if (time < mediaPlayer!!.currentPosition && reciter.identifier == translator.identifier) {
                            while (time < mediaPlayer!!.currentPosition && index < ayahsDurations.size) {
                                stringBuilder.append(listOfAyahsText[index])
                                binding.surah.text = stringBuilder.toString()
                                if (index + 1 < ayahsDurations.size) {
                                    time += ayahsDurations[index]
                                }
                                index++
                            }
                        }
                    } else {
                        binding.seekbar.progress = 0
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViewModel() {
        viewModel.surahAudio.observe(viewLifecycleOwner) { surahAudio ->
            if (surahAudio != null) {
                setAudioInfo(surahAudio)
                prepareAudioUrl()
                makeNotifyInvisible()
            }
        }
        viewModel.error.observe(viewLifecycleOwner) {
            // show error to a user
            makeNotifyVisible()
            makeApiCall()
            Toast.makeText(activity, getString(R.string.loading_error), Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun requestTranslation() {
        viewModel.getSurahTranslation(surah.number, translator.identifier)
    }

    private fun makeNotifyVisible() {
        binding.progressBar.visibility = View.VISIBLE
        binding.retry.visibility = View.VISIBLE
    }

    private fun makeNotifyInvisible() {
        binding.progressBar.visibility = View.GONE
        binding.retry.visibility = View.GONE
    }

    private fun prepareAudioUrl() {
        surahAudioUrlBySurah = String.format(
            "https://cdn.islamic.network/quran/audio-surah/128/%s/%d.mp3",
            reciter.identifier,
            surah.number
        )
    }

    private fun setAudioInfo(surahAudio: SurahAudio) {
        var audio = true
        binding.numberOfAyahs.text = surahAudio.numberOfAyahs.toString()
        translate("ayahs", language)
        var ayahNumber = 1
        stringBuilder.clear()
        stringBuilder.setLength(0)
        for (ayahAudio in surahAudio.ayahs) {
            if (translator.language != "ar") {
                listOfAyahsText.add("﴾${ayahNumber++}﴿ \t${ayahAudio.text}\n\n") // retrieve ayah text
            } else {
                listOfAyahsText.add(" ${ayahAudio.text} ﴿${ayahNumber++}﴾\n\n") // retrieve ayah text
            }
            val retriever = MediaMetadataRetriever()

            if (ayahAudio.audio != null && translator.identifier == reciter.identifier) {
                retriever.setDataSource(ayahAudio.audio, HashMap())
                val timeInMillisecond =
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!
                        .toInt()
                ayahsDurations.add(timeInMillisecond) //retrieve ayah audio duration
            } else {
                audio = false
                stringBuilder.append(listOfAyahsText[ayahNumber - 2])
            }
        }
        if (!audio || reciter.identifier != translator.identifier) {
            binding.surah.text = stringBuilder.toString()
        } else {
            currentDuration = ayahsDurations[0] + 2000
            stringBuilder.append(listOfAyahsText[0])
            binding.surah.text = stringBuilder.toString() // show first ayah text in the beginning
        }
    }

    private fun setSurahInfo(surah: Surah) {
        binding.apply {
            englishName.text = surah.englishName
            name.text = surah.name
            surahNumber.text = surah.number.toString()
            languageTv.text = language
            englishNameTranslation.text = surah.englishNameTranslation
            translatorTv.text =
                String.format("%s: %s", getString(R.string.translator), translator.englishName)
            reciterTv.text =
                String.format("%s: %s", getString(R.string.reciter), reciter.englishName)
        }
        translate(surah.englishNameTranslation, language)
        translate(getString(R.string.translator), language)
        translate(getString(R.string.reciter), language)
    }

    private fun translate(text: String, targetLang: String) {
        try {
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.ENGLISH)
                .setTargetLanguage(TranslateLanguage.fromLanguageTag(targetLang)!!)
                .build()
            val langTranslator = Translation.getClient(options)

            val conditions = DownloadConditions.Builder()
                .requireWifi()
                .build()

            langTranslator.downloadModelIfNeeded(conditions)
                .addOnSuccessListener {
                    // Model downloaded successfully. Okay to start translating.
                    // (Set a flag, unhide the translation UI, etc.)
                    langTranslator.translate(text)
                        .addOnSuccessListener { translatedText ->
                            // Translation successful.
                            when (text) {
                                "ayahs" -> {
                                    binding.ayahs.text = translatedText
                                }
                                surah.englishNameTranslation -> {
                                    binding.englishNameTranslation.text = translatedText
                                }
                                getString(R.string.translator) -> {
                                    binding.translatorTv.text = String.format(
                                        "%s: %s",
                                        translatedText,
                                        translator.englishName
                                    )
                                }
                                getString(R.string.reciter) -> {
                                    binding.reciterTv.text = String.format(
                                        "%s: %s",
                                        translatedText,
                                        reciter.englishName
                                    )
                                }
                            }
                        }.addOnFailureListener { exception ->
                            // Error.
                            // ...
                        }
                }
                .addOnFailureListener { exception ->
                    // Model couldn’t be downloaded or other internal error.
                    // ...
                }

        }catch (e:Exception){}
    }

    private fun returnTime(position: Int?): String {
        audioDuration = position!! / 1000
        val hour = (audioDuration / 3600)
        val min = (audioDuration % 3600) / 60
        val sec = (audioDuration % 60)
        return String.format("%02d:%02d:%02d", hour, min, sec)
    }

    private var i = 1
    private var runnable = object : Runnable {
        override fun run() {
            /** Update playing time */
            if (mediaPlayer != null) {
                val currentPosition = mediaPlayer?.currentPosition!!
                currentTime = returnTime(currentPosition)
                binding.currentTime.text = currentTime
                binding.seekbar.progress = currentPosition

                /** Show play button if player is paused */
                if (!mediaPlayer?.isPlaying!!) {
                    binding.playBtn.setImageResource(R.drawable.ic_play_arrow) //set play icon if music finished
                } else {
                    binding.playBtn.setImageResource(R.drawable.ic_pause)   // Show pause button if player is playing
                    if (isSeekBarChanged) {
                        stringBuilder.clear()
                        stringBuilder.setLength(0)
                        for (ayahText in listOfAyahsText) {
                            stringBuilder.append(ayahText)
                        }
                        binding.surah.text = stringBuilder.toString()
                    }
                }
                /** set ayah text if duration matches */
                if (currentDuration != null && currentTime == returnTime(currentDuration) && !isSeekBarChanged && reciter.identifier == translator.identifier) {
                    if (i < listOfAyahsText.size) {
                        stringBuilder.append(listOfAyahsText[i])
                        binding.surah.text = stringBuilder.toString()
                        currentDuration = currentDuration!! + ayahsDurations[i]
                        i++
                    }
                }
            }
            handler.postDelayed(this, 100)
        }
    }

    private fun setMedia(musicPath: String): Boolean {
        mediaPlayer = MediaPlayer()
        var musicExists = false
        try {
            mediaPlayer?.apply {
                setDataSource(musicPath)
                prepare()
                onPrepared()
                musicExists = true
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: Exception) {
            println("Exception of type : $e")
            e.printStackTrace()
        }
        return musicExists
    }

    private fun onPrepared() {
        endTime = returnTime(mediaPlayer!!.duration)
        binding.totalTime.text = String.format("/%s", endTime)
        binding.seekbar.max = mediaPlayer?.duration!!
        handler.postDelayed(runnable, 100)
    }

    private fun setUpPlayerSource(audioPath: String) {
        if (setMedia(audioPath)) {
            binding.download.setImageResource(R.drawable.ic_downloaded)
            disableDownloadClick()
            isAudioObserved = true
        }
    }

    @SuppressLint("Range")
    private suspend fun downloadAudio(surahNumber: Int, reciter: String) {
        withContext(Dispatchers.IO) {
            /** Request a download */
            val filename = "$surahNumber.mp3" // format name of a file
            audioPath = String.format(
                "/storage/emulated/0/Music/%s/%s/%s",
                getString(R.string.app_name), // parent folder
                reciter, // subfolder
                filename //file
            )
            // create directory
            val direct = File(
                resources.getString(R.string.app_name) + "/"
            )
            // make directory
            if (!direct.exists()) {
                direct.mkdir()
            }
            // create download manager
            val downloadManager =
                requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri: Uri = Uri.parse(surahAudioUrlBySurah)
            val request = DownloadManager.Request(downloadUri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(filename)
                .setMimeType("audio/mpeg")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED or DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_MUSIC,
                    File.separator + getString(R.string.app_name) + File.separator.toString() + reciter + File.separator.toString() + filename
                )
            downloadId = downloadManager.enqueue(request)
            snackBar("Downloading...")
            disableDownloadClick()
        }
    }

    private val onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
                intent.extras?.let {
                    //retrieving the file
                    val downloadedFileId = it.getLong(DownloadManager.EXTRA_DOWNLOAD_ID)
                    if (downloadId == downloadedFileId) {
                        snackBar("Download Completed")
                        setUpPlayerSource(audioPath!!)
                        animateDownloadButton()
                    }
                }
            }
        }
    }

    private fun disableDownloadClick() {
        binding.download.isClickable = false
        binding.download.isEnabled = false
    }

    private fun animateDownloadButton() {
        disableDownloadClick()
        val rotate = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotate.duration = 1000
        rotate.interpolator = LinearInterpolator()
        binding.download.startAnimation(rotate)
        binding.download.setImageResource(R.drawable.ic_downloaded)
    }

    private fun releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer?.release()
                mediaPlayer = null
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(
            onComplete,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(onComplete)
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMP()
    }
}
