package com.otamurod.quronikarim.app.presentation.ui.surah

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
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
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import java.io.File


private const val TAG = "SurahFragment"

@AndroidEntryPoint
class SurahFragment : Fragment() {

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
    private var i = 1
    private var stringBuilder = StringBuilder()
    private var isSeekBarChanged = false
    private var isAudioObserved = false
    private var progressDialog: ProgressDialog? = null
    private val args by navArgs<SurahFragmentArgs>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStart() {
        super.onStart()
        // Make API Call
        if (checkNetworkStatus(requireContext())) {
            viewModel.getSurahTranslation(surah.number, translator.identifier)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = requireActivity() as AppCompatActivity
        activity.supportActionBar?.hide()
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
        binding.progressBar.visibility = View.VISIBLE
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

        var audioPath =
            "/storage/emulated/0/Music/${getString(R.string.app_name)}/${reciter.identifier}/${surah.number}.mp3"
        if (setMedia(audioPath)) {
            binding.download.setImageResource(R.drawable.ic_downloaded)
            binding.download.isClickable = false
            binding.download.isEnabled = false
            isAudioObserved = true
        }
        // Download audio on clicked
        binding.download.setOnClickListener {
            lifecycleScope.launch {
                audioPath = downloadAudio(surah.number, reciter.identifier)
            }
        }
        // Play Audio Button Listener
        binding.playBtn.setOnClickListener {
            if (!isAudioObserved) {
                Toast.makeText(requireContext(), "Please Download Audio", Toast.LENGTH_SHORT).show()
            } else if (mediaPlayer == null) {
                // do something
                setMedia(audioPath)
            } else if (mediaPlayer?.isPlaying!! && isAudioObserved) { // playing, icon is pause
                binding.playBtn.setImageResource(com.otamurod.quronikarim.R.drawable.ic_play_arrow)
                mediaPlayer?.pause()
            } else if (!mediaPlayer?.isPlaying!! && isAudioObserved) { // not playing, icon is play
                binding.playBtn.setImageResource(com.otamurod.quronikarim.R.drawable.ic_pause)
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

    @RequiresApi(Build.VERSION_CODES.N)
    private fun initViewModel() {
        viewModel.surahAudio.observe(viewLifecycleOwner) { surahAudio ->
            if (surahAudio != null) {
                val audio = setAudioInfo(surahAudio)
                prepareAudioUrl(audio)
                binding.progressBar.visibility = View.GONE
            } else {
                Toast.makeText(requireContext(), "surah: $surahAudio", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private suspend fun downloadAudio(surahNumber: Int, reciter: String): String {
        val filename = "$surahNumber.mp3"
        val job = GlobalScope.launch(Dispatchers.IO) {
            val direct = File(
                resources.getString(R.string.app_name) + "/"
            )

            if (!direct.exists()) {
                direct.mkdir()
            }

            val dm = requireContext().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
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
            dm.enqueue(request)
        }
        // wait for job completes
        job.join()
        // show snackBar if job finished
        if (job.isCompleted) {
            snackBar(R.string.downloading)
            isAudioObserved = true
        }
        return "/storage/emulated/0/Music/${getString(R.string.app_name)}/$reciter/$filename"
    }

    private fun showProgressDialog() {
        progressDialog = ProgressDialog.show(
            requireContext(), "",
            "Loading. Please wait...", true
        )
    }

    private fun prepareAudioUrl(audio: Boolean) {
        val identifier = if (!audio) reciter.identifier else translator.identifier
        surahAudioUrlBySurah = String.format(
            "https://cdn.islamic.network/quran/audio-surah/128/%s/%d.mp3",
            identifier,
            surah.number
        )
    }

    private fun setAudioInfo(surahAudio: SurahAudio): Boolean {
        var audio = true
        binding.numberOfAyahs.text = "${surahAudio.numberOfAyahs} ayahs"
        var ayahNumber = 1
        stringBuilder.clear()
        for (ayahAudio in surahAudio.ayahs) {
            if (translator.language != "ar") {
                listOfAyahsText.add("﴾${ayahNumber++}﴿ \t${ayahAudio.text}\n\n") // retrieve ayah text
            } else {
                listOfAyahsText.add(" ${ayahAudio.text} ﴿${ayahNumber++}﴾\n\n") // retrieve ayah text
            }
            val retriever = MediaMetadataRetriever()

            if (ayahAudio.audio != null) {
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
        if (!audio) {
            binding.surah.text = stringBuilder.toString()
        } else {
            currentDuration = ayahsDurations[0] + 2000
            stringBuilder.append(listOfAyahsText[0])
            binding.surah.text = stringBuilder.toString() // show first ayah text in the beginning
        }
        return audio
    }

    private fun setSurahInfo(surah: Surah) {
        binding.apply {
            englishName.text = surah.englishName
            name.text = surah.name
            surahNumber.text = surah.number.toString()
            englishNameTranslation.text = surah.englishNameTranslation
            languageTv.text = language
            translatorTv.text =
                String.format("%s: %s", getString(R.string.translator), translator.englishName)
            reciterTv.text =
                String.format("%s: %s", getString(R.string.reciter), reciter.englishName)
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
            println("Exception of type : $e");
            e.printStackTrace()
        }
        return musicExists
    }

    private fun onPrepared() {
        endTime = returnTime(mediaPlayer!!.duration)
        binding.totalTime.text = "/$endTime"
        binding.seekbar.max = mediaPlayer?.duration!!
        handler.postDelayed(runnable, 100)
        animateDownloadButton()
    }

    private fun animateDownloadButton() {
        binding.download.isClickable = false
        binding.download.isEnabled = false

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
        binding.download.setImageResource(com.otamurod.quronikarim.R.drawable.ic_downloaded)
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

    /*override fun onResume() {
        super.onResume()
        if (mediaPlayer != null) {
            binding.totalTime.text = "/$endTime"
            mediaPlayer!!.start()
        }
    }*/

    override fun onDestroy() {
        super.onDestroy()
        releaseMP()
    }
}
