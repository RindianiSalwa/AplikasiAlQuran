package com.example.aplikasialquran

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasialquran.api.ApiClient
import com.example.aplikasialquran.db.AppDatabase
import com.example.aplikasialquran.db.SurahDao
import com.example.aplikasialquran.db.SurahFavorit
import com.example.aplikasialquran.model.Surah
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerViewSurah: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var surahAdapter: SurahAdapter

    private val _apiSurahList = MutableStateFlow<List<Surah>?>(null)
    private val apiSurahList: StateFlow<List<Surah>?> = _apiSurahList

    private lateinit var surahDao: SurahDao

    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerViewSurah = view.findViewById(R.id.recyclerViewSurah)
        progressBar = view.findViewById(R.id.progressBar)
        surahDao = AppDatabase.getDatabase(requireContext()).surahDao()
        setupRecyclerView()
        fetchSurahData()
        combineSurahData()
        return view
    }

    private fun setupRecyclerView() {
        surahAdapter = SurahAdapter(mutableListOf(), { surah ->
            val intent = Intent(activity, DetailSurahActivity::class.java).apply {
                putExtra("nomor_surah", surah.nomor)
                putExtra("nama_latin_surah", surah.namaLatin)
            }
            startActivity(intent)
        }, { surah, imageView, isCurrentlyFavorite ->
            toggleFavoriteStatus(surah, imageView, isCurrentlyFavorite)
        })
        recyclerViewSurah.layoutManager = LinearLayoutManager(context)
        recyclerViewSurah.adapter = surahAdapter
    }

    private fun fetchSurahData() {
        progressBar.visibility = View.VISIBLE
        lifecycleScope.launch {
            try {
                val response = ApiClient.instance.getListSurah()
                if (response.isSuccessful && response.body() != null) {
                    val surahData = response.body()!!
                    if (surahData.isNotEmpty()) {
                        _apiSurahList.value = surahData
                    } else {
                        Toast.makeText(requireContext(), "Data surah kosong dari API", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat data surah: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error jaringan: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun combineSurahData() {
        lifecycleScope.launch {
            apiSurahList.filterNotNull().combine(surahDao.getAllFavoriteSurah()) { apiList, favoriteSurahs ->
                val favoriteSurahNumbers = favoriteSurahs.map { it.nomor }.toSet()
                val updatedList = apiList.toMutableList()
                updatedList.forEach { surah ->
                    surah.status = favoriteSurahNumbers.contains(surah.nomor)
                }
                updatedList
            }.collect { finalSurahList ->
                activity?.runOnUiThread {
                    surahAdapter.updateData(finalSurahList)
                }
            }
        }
    }

    private fun toggleFavoriteStatus(surah: Surah, imageView: ImageView, isCurrentlyFavorite: Boolean) {
        lifecycleScope.launch {
            val surahFavorit = SurahFavorit(
                nomor = surah.nomor,
                namaLatin = surah.namaLatin,
                nama = surah.nama,
                jumlahAyat = surah.jumlahAyat,
                tempatTurun = surah.tempatTurun,
                arti = surah.arti,
                deskripsi = surah.deskripsi,
                audio = surah.audio
            )
            try {
                if (isCurrentlyFavorite) {
                    surahDao.delete(surahFavorit)
                    Toast.makeText(requireContext(), "${surah.namaLatin} dihapus dari favorit", Toast.LENGTH_SHORT).show()
                } else {
                    surahDao.insert(surahFavorit)
                    Toast.makeText(requireContext(), "${surah.namaLatin} ditambahkan ke favorit", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Gagal mengubah status favorit: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun filterSurah(query: String?) {
        lifecycleScope.launch {
            val currentApiList = _apiSurahList.value ?: return@launch
            val favoriteSurahs = surahDao.getAllFavoriteSurah().firstOrNull() ?: emptyList()
            val favoriteSurahNumbers = favoriteSurahs.map { it.nomor }.toSet()

            val baseList = currentApiList.map { surah ->
                surah.copy(status = favoriteSurahNumbers.contains(surah.nomor))
            }

            val filteredList = if (query.isNullOrBlank()) {
                baseList
            } else {
                baseList.filter { surah ->
                    surah.namaLatin.contains(query, ignoreCase = true) ||
                            surah.arti.contains(query, ignoreCase = true) ||
                            surah.nomor.toString().contains(query, ignoreCase = true)
                }
            }

            activity?.runOnUiThread {
                surahAdapter.updateData(filteredList)
            }
        }
    }
}
