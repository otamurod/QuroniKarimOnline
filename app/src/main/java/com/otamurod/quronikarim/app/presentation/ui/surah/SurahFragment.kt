package com.otamurod.quronikarim.app.presentation.ui.surah

import android.app.ProgressDialog
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.otamurod.quronikarim.R
import com.otamurod.quronikarim.app.domain.model.audio.SurahAudio
import com.otamurod.quronikarim.app.domain.model.surah.Surah
import com.otamurod.quronikarim.app.presentation.utils.checkNetworkStatus
import com.otamurod.quronikarim.databinding.FragmentSurahBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch

private const val TAG = "SurahFragment"

@AndroidEntryPoint
class SurahFragment : Fragment(), MediaPlayer.OnPreparedListener {

    lateinit var binding: FragmentSurahBinding
    private val viewModel: SurahViewModel by viewModels()
    private lateinit var surah: Surah
    private var mediaPlayer: MediaPlayer? = null
    private var surahAudioUrlBySurah: String? = null
    lateinit var handler: Handler
    private var endTime = "00:00:00"
    private var currentTime = "00:00:00"
    private var audioDuration: Int = 0
    private var listOfAyahsText = ArrayList<String>()
    private var ayahsDurations = ArrayList<Int>()
    private var currentDuration = 0
    private var i = 1
    private var stringBuilder = StringBuilder()
    private var identifier = ""
    private var isSeekBarChanged = false
    private var isAudioObserved = false
    private var progressDialog: ProgressDialog? = null
    private val args by navArgs<SurahFragmentArgs>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        // Make API Call
        if (checkNetworkStatus(requireContext())) {
            lifecycleScope.launch(Dispatchers.IO) {
                viewModel.getSurahAudioCall(surah.number, identifier)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSurahBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        surah = args.surah
        identifier = args.identifier

        prepareAudioUrl()
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

        binding.download.setOnClickListener {
            showProgressDialog()
            setMedia()
        }
        // Play Audio Button Listener
        binding.playBtn.setOnClickListener {
            if (mediaPlayer == null) {
                setMedia()
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
                        var index = 0
                        var time = 0
                        if (time < mediaPlayer!!.currentPosition) {
                            while (time < mediaPlayer!!.currentPosition && index < ayahsDurations.size) {
                                stringBuilder.append(listOfAyahsText[index])
                                binding.surah.text = stringBuilder.toString()
                                if (index + 1 < ayahsDurations.size) {
                                    time += ayahsDurations[index]
                                }
                                index++
                            }
                        } else {
                            binding.surah.text = stringBuilder.toString()
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

    private fun showProgressDialog() {
        progressDialog = ProgressDialog.show(
            requireContext(), "",
            "Loading. Please wait...", true
        )
    }

    private fun prepareAudioUrl() {
        surahAudioUrlBySurah = String.format(
            "https://media.blubrry.com/muslim_central_quran/podcasts.qurancentral.com/mishary-rashid-alafasy/mishary-rashid-alafasy-%03d-muslimcentral.com.mp3",
            surah.number
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViewModel() {
        viewModel.surahAudio.observe(viewLifecycleOwner) { surahAudio ->
            if (surahAudio != null) {
                setAudioInfo(surahAudio)
                binding.progressBar.visibility = View.GONE
            } else {
                Toast.makeText(requireContext(), "surahAudio: $surahAudio", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun setAudioInfo(surahAudio: SurahAudio) {
        binding.numberOfAyahs.text = "${surahAudio.numberOfAyahs} ayahs"
        var ayahNumber = 1
        for (ayahAudio in surahAudio.ayahs) {
            listOfAyahsText.add(" (${ayahNumber++}) \t${ayahAudio.text}\n") // retrieve ayah text

            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(ayahAudio.audio, HashMap())
            val timeInMillisecond =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!.toInt()
            ayahsDurations.add(timeInMillisecond) //retrieve ayah audio duration
        }
        currentDuration = ayahsDurations[0]
        stringBuilder.append(listOfAyahsText[0])
        binding.surah.text = stringBuilder.toString() // show first ayah text in the beginning
    }

    private fun setSurahInfo(surah: Surah) {
        binding.apply {
            englishName.text = surah.englishName
            name.text = surah.name
            surahNumber.text = surah.number.toString()
            englishNameTranslation.text = surah.englishNameTranslation
        }
    }

    private fun returnTime(position: Int?): String {
        audioDuration = position!! / 1000
        val hour = (audioDuration / 3600)
        val min = (audioDuration % 3600) / 60
        val sec = (audioDuration % 60)
        return String.format("%02d:%02d:%02d", hour, min, sec)
    }

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
                        for (ayahText in listOfAyahsText) {
                            stringBuilder.append(ayahText)
                        }
                        binding.surah.text = stringBuilder.toString()
                    }
                }
                /** set ayah text if duration matches */
                if (currentTime == returnTime(currentDuration) && !isSeekBarChanged) {
                    if (i < listOfAyahsText.size) {
                        stringBuilder.append(listOfAyahsText[i])
                        binding.surah.text = stringBuilder.toString()
                        currentDuration += ayahsDurations[i]
                        i++
                    }
                }
            }
            handler.postDelayed(this, 100)
        }
    }

    private fun setMedia() {
        mediaPlayer = MediaPlayer()
        lifecycleScope.launch(Dispatchers.IO) {
            mediaPlayer?.apply {
                setDataSource(surahAudioUrlBySurah)
                setOnPreparedListener(this@SurahFragment)
                prepareAsync()
            }
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        isAudioObserved = true
        progressDialog!!.cancel()
        binding.download.setImageResource(R.drawable.ic_downloaded)
        endTime = returnTime(mediaPlayer!!.duration)
        binding.totalTime.text = "/$endTime"
        binding.seekbar.max = mediaPlayer?.duration!!
        handler.postDelayed(runnable, 100)
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
        if (mediaPlayer != null) {
            binding.totalTime.text = "/$endTime"
            mediaPlayer!!.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMP()
    }
}
