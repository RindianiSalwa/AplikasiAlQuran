package com.example.aplikasialquran

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasialquran.api.ApiClient
import com.example.aplikasialquran.db.AppDatabase
import com.example.aplikasialquran.db.SurahDao
import com.example.aplikasialquran.db.SurahHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailSurahActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var tvSurahNameLatinDetail: TextView
    private lateinit var tvSurahNumberAndPlaceDetail: TextView
    private lateinit var tvSurahMeaningDetail: TextView
    private lateinit var tvSurahDescriptionDetail: TextView
    private lateinit var recyclerViewAyat: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var btnPlayPauseAudio: ImageButton
    private lateinit var progressBarAudio: ProgressBar

    private var mediaPlayer: MediaPlayer? = null
    private var isAudioPlaying = false
    private var currentAudioUrl: String? = null

    private lateinit var surahDao: SurahDao

    companion object {
        private const val TAG = "DetailSurahActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_surah)

        toolbar = findViewById(R.id.toolbarDetailSurah)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvSurahNameLatinDetail = findViewById(R.id.tvSurahNameLatinDetail)
        tvSurahNumberAndPlaceDetail = findViewById(R.id.tvSurahNumberAndPlaceDetail)
        tvSurahMeaningDetail = findViewById(R.id.tvSurahMeaningDetail)
        tvSurahDescriptionDetail = findViewById(R.id.tvSurahDescriptionDetail)
        recyclerViewAyat = findViewById(R.id.recyclerViewAyat)
        progressBar = findViewById(R.id.progressBar)
        btnPlayPauseAudio = findViewById(R.id.btnPlayPauseAudio)
        progressBarAudio = findViewById(R.id.progressBarAudio)

        surahDao = AppDatabase.getDatabase(this).surahDao()

        val surahNumber = intent.getIntExtra("nomor_surah", -1)

        if (surahNumber != -1) {
            fetchDetailSurah(surahNumber)
        } else {
            Toast.makeText(this, "Nomor surah tidak valid.", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnPlayPauseAudio.setOnClickListener {
            toggleAudioPlayback()
        }
    }

    private fun fetchDetailSurah(nomorSurah: Int) {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = ApiClient.instance.getDetailSurah(nomorSurah)
                if (response.isSuccessful) {
                    val detailSurahResponse = response.body()
                    if (detailSurahResponse != null) {
                        withContext(Dispatchers.Main) {
                            supportActionBar?.title = detailSurahResponse.namaLatin
                            tvSurahNameLatinDetail.text = detailSurahResponse.namaLatin
                            tvSurahNumberAndPlaceDetail.text =
                                "${detailSurahResponse.nomor}. ${detailSurahResponse.tempatTurun} | ${detailSurahResponse.jumlahAyat} Ayat"
                            tvSurahMeaningDetail.text = detailSurahResponse.arti
                            tvSurahDescriptionDetail.text = HtmlCompat.fromHtml(
                                detailSurahResponse.deskripsi,
                                HtmlCompat.FROM_HTML_MODE_LEGACY
                            ).toString()

                            recyclerViewAyat.layoutManager = LinearLayoutManager(this@DetailSurahActivity)
                            recyclerViewAyat.adapter = AyatAdapter(detailSurahResponse.ayat)

                            currentAudioUrl = detailSurahResponse.audio
                            Log.d(TAG, "Audio URL: $currentAudioUrl")
                        }

                        withContext(Dispatchers.IO) {
                            val surahHistory = SurahHistory(
                                nomor = detailSurahResponse.nomor,
                                namaLatin = detailSurahResponse.namaLatin,
                                nama = detailSurahResponse.nama,
                                jumlahAyat = detailSurahResponse.jumlahAyat,
                                tempatTurun = detailSurahResponse.tempatTurun,
                                arti = detailSurahResponse.arti,
                                deskripsi = detailSurahResponse.deskripsi,
                                audio = detailSurahResponse.audio,
                                timestamp = System.currentTimeMillis()
                            )
                            surahDao.insertHistory(surahHistory)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@DetailSurahActivity, "Data kosong dari API", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DetailSurahActivity, "Gagal load data: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@DetailSurahActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            } finally {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun toggleAudioPlayback() {
        currentAudioUrl?.let { url ->
            if (mediaPlayer == null) {
                startAudioPlayback(url)
            } else {
                if (isAudioPlaying) {
                    pauseAudio()
                } else {
                    resumeAudio()
                }
            }
        } ?: Toast.makeText(this, "URL audio tidak tersedia.", Toast.LENGTH_SHORT).show()
    }

    private fun startAudioPlayback(url: String) {
        progressBarAudio.visibility = View.VISIBLE
        btnPlayPauseAudio.isEnabled = false
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener {
                    it.start()
                    isAudioPlaying = true
                    updatePlayPauseButton()
                    progressBarAudio.visibility = View.GONE
                    btnPlayPauseAudio.isEnabled = true
                    Toast.makeText(this@DetailSurahActivity, "Memutar audio...", Toast.LENGTH_SHORT).show()
                }
                setOnCompletionListener {
                    isAudioPlaying = false
                    updatePlayPauseButton()
                    it.seekTo(0)
                }
                setOnErrorListener { _, _, _ ->
                    Toast.makeText(this@DetailSurahActivity, "Gagal memutar audio", Toast.LENGTH_SHORT).show()
                    releaseMediaPlayer()
                    progressBarAudio.visibility = View.GONE
                    btnPlayPauseAudio.isEnabled = true
                    isAudioPlaying = false
                    updatePlayPauseButton()
                    false
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailSurahActivity, "Error audio: ${e.message}", Toast.LENGTH_SHORT).show()
                releaseMediaPlayer()
                progressBarAudio.visibility = View.GONE
                btnPlayPauseAudio.isEnabled = true
                isAudioPlaying = false
                updatePlayPauseButton()
            }
        }
    }

    private fun pauseAudio() {
        mediaPlayer?.takeIf { it.isPlaying }?.apply {
            pause()
            isAudioPlaying = false
            updatePlayPauseButton()
            Toast.makeText(this@DetailSurahActivity, "Audio dijeda.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun resumeAudio() {
        mediaPlayer?.takeIf { !it.isPlaying }?.apply {
            start()
            isAudioPlaying = true
            updatePlayPauseButton()
            Toast.makeText(this@DetailSurahActivity, "Melanjutkan audio...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updatePlayPauseButton() {
        val iconResId = if (isAudioPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
        btnPlayPauseAudio.setImageDrawable(ContextCompat.getDrawable(this, iconResId))
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseMediaPlayer()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
