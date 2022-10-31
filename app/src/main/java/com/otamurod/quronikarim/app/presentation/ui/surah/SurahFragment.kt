package com.otamurod.quronikarim.app.presentation.ui.surah

import SurahViewModelFactory
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
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.otamurod.quronikarim.R
import com.otamurod.quronikarim.app.data.remote.ApiClient
import com.otamurod.quronikarim.app.data.repository.RepositoryImpl
import com.otamurod.quronikarim.app.data.repository.datasource.SurahDataSourceImpl
import com.otamurod.quronikarim.app.domain.model.audio.SurahAudio
import com.otamurod.quronikarim.app.presentation.utils.checkNetworkStatus
import com.otamurod.quronikarim.databinding.FragmentSurahBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch

private const val TAG = "SurahFragment"

class SurahFragment : Fragment(), MediaPlayer.OnPreparedListener {
    lateinit var binding: FragmentSurahBinding
    private lateinit var viewModel: SurahViewModel
    private var surahNumber = 0
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var surahAudioUrlByAyahs: ArrayList<String>
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
    private var isSurahObserved = false
    private var isAudioObserved = false

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        // Make API Call
        if (checkNetworkStatus(requireContext())) {
            // Reciters https://qurancentral.com/reciters/
            surahAudioUrlBySurah = String.format(
                "https://media.blubrry.com/muslim_central_quran/podcasts.qurancentral.com/mishary-rashid-alafasy/mishary-rashid-alafasy-%03d-muslimcentral.com.mp3",
                surahNumber
            )
            viewModel.getSurahAudioCall(surahNumber, identifier)
            setMedia()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSurahBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.VISIBLE
        val bundle = this.arguments
        if (bundle != null) {
            surahNumber = bundle.getInt("surahNumber")
            identifier = bundle.getString("identifier", "")
            binding.surahNumber.text = surahNumber.toString()
        }
        mediaPlayer = MediaPlayer()

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

        // Play Audio
        binding.playBtn.setOnClickListener {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer()
            } else if (mediaPlayer?.isPlaying!!) { // playing, icon is pause
                binding.playBtn.setImageResource(R.drawable.ic_play_arrow)
                mediaPlayer?.pause()
            } else if (!mediaPlayer?.isPlaying!!) { // not playing, icon is play
                binding.playBtn.setImageResource(R.drawable.ic_pause)
                mediaPlayer?.start()
            }
        }

        binding.seekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViewModel() {
        val dataSourceImpl = SurahDataSourceImpl(ApiClient.apiService)
        val repositoryImpl = RepositoryImpl(dataSourceImpl)
        viewModel =
            ViewModelProvider(
                this,
                SurahViewModelFactory(repositoryImpl)
            )[SurahViewModel::class.java]

        surahAudioUrlByAyahs = ArrayList()

        viewModel.surahAudio.observe(viewLifecycleOwner) { surahAudio ->
            isSurahObserved = true
            //hide progress bar
            if (isAudioObserved) {
                binding.progressBar.visibility = View.GONE
            }
            setSurahInfo(surahAudio)

            var ayahNumber = 1
            for (ayahAudio in surahAudio.ayahs) {
                surahAudioUrlByAyahs.add(ayahAudio.audio)
                listOfAyahsText.add(" ${ayahNumber++}\t${ayahAudio.text}\n")

                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(ayahAudio.audio, HashMap())
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                val timeInMillisecond = time!!.toInt()
                ayahsDurations.add(timeInMillisecond)
            }
            currentDuration = ayahsDurations[0]
            stringBuilder.append(listOfAyahsText[0])
            binding.surah.text = stringBuilder.toString()
        }
    }

    private fun setSurahInfo(surahAudio: SurahAudio) {
        binding.englishName.text = surahAudio.englishName
        binding.name.text = surahAudio.name
        binding.surahNumber.text = surahAudio.number.toString()
        binding.numberOfAyahs.text = "${surahAudio.numberOfAyahs} ayahs"
        binding.englishNameTranslation.text = surahAudio.englishNameTranslation
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
        lifecycleScope.launch(Dispatchers.IO) {
            mediaPlayer!!.setDataSource(surahAudioUrlBySurah)
            mediaPlayer!!.setOnPreparedListener(this@SurahFragment)
            mediaPlayer!!.prepareAsync()
        }
    }

    override fun onPrepared(mp: MediaPlayer?) {
        isAudioObserved = true
        if (isSurahObserved) {
            binding.progressBar.visibility = View.GONE
//            binding.playBtn.isEnabled = true
        }
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
