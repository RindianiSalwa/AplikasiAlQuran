package com.example.aplikasialquran

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasialquran.db.AppDatabase
import com.example.aplikasialquran.db.SurahDao
import com.example.aplikasialquran.db.SurahFavorit
import com.example.aplikasialquran.model.Surah
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {

    private lateinit var recyclerViewFavorites: RecyclerView
    private lateinit var tvNoFavorites: TextView
    private lateinit var surahDao: SurahDao
    private lateinit var favoriteAdapter: SurahAdapter

    private val TAG = "FavoriteFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: FavoriteFragment dimulai.")
        surahDao = AppDatabase.getDatabase(requireContext()).surahDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        recyclerViewFavorites = view.findViewById(R.id.recyclerViewFavorites)
        tvNoFavorites = view.findViewById(R.id.tvNoFavorites)
        setupRecyclerView()
        observeFavoriteSurahs()
        return view
    }

    private fun setupRecyclerView() {
        favoriteAdapter = SurahAdapter(mutableListOf(), { surah ->
            val intent = Intent(activity, DetailSurahActivity::class.java).apply {
                putExtra("nomor_surah", surah.nomor)
                putExtra("nama_latin_surah", surah.namaLatin)
            }
            startActivity(intent)
        }, { surah, _, _ ->
            toggleFavoriteStatus(surah)
        })
        recyclerViewFavorites.layoutManager = LinearLayoutManager(context)
        recyclerViewFavorites.adapter = favoriteAdapter
    }

    private fun observeFavoriteSurahs() {
        viewLifecycleOwner.lifecycleScope.launch {
            surahDao.getAllFavoriteSurah().collectLatest { favoriteSurahs ->
                val favoriteList = favoriteSurahs.map { surahFavorit ->
                    Surah(
                        nomor = surahFavorit.nomor,
                        nama = surahFavorit.nama,
                        namaLatin = surahFavorit.namaLatin,
                        jumlahAyat = surahFavorit.jumlahAyat,
                        tempatTurun = surahFavorit.tempatTurun,
                        arti = surahFavorit.arti,
                        deskripsi = surahFavorit.deskripsi,
                        audio = surahFavorit.audio,
                        status = true
                    )
                }

                favoriteAdapter.updateData(favoriteList)

                if (favoriteList.isEmpty()) {
                    tvNoFavorites.visibility = View.VISIBLE
                    recyclerViewFavorites.visibility = View.GONE
                } else {
                    tvNoFavorites.visibility = View.GONE
                    recyclerViewFavorites.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun toggleFavoriteStatus(surah: Surah) {
        viewLifecycleOwner.lifecycleScope.launch {
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
                surahDao.delete(surahFavorit)
                Toast.makeText(context, "${surah.namaLatin} dihapus dari favorit", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal menghapus dari favorit: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
