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
import com.example.aplikasialquran.db.SurahHistory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private lateinit var recyclerViewHistory: RecyclerView
    private lateinit var tvNoHistory: TextView
    private lateinit var surahDao: SurahDao
    private lateinit var historyAdapter: HistoryAdapter

    private val TAG = "HistoryFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: HistoryFragment dimulai.")
        surahDao = AppDatabase.getDatabase(requireContext()).surahDao()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        recyclerViewHistory = view.findViewById(R.id.recyclerViewHistory)
        tvNoHistory = view.findViewById(R.id.tvNoHistory)
        setupRecyclerView()
        observeHistorySurahs()
        return view
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(mutableListOf(), { surah ->
            val intent = Intent(activity, DetailSurahActivity::class.java).apply {
                putExtra("nomor_surah", surah.nomor)
                putExtra("nama_latin_surah", surah.namaLatin)
            }
            startActivity(intent)
        }, { surahHistory ->
            deleteHistory(surahHistory)
        })
        recyclerViewHistory.layoutManager = LinearLayoutManager(context)
        recyclerViewHistory.adapter = historyAdapter
    }

    private fun observeHistorySurahs() {
        viewLifecycleOwner.lifecycleScope.launch {
            surahDao.getAllHistorySurah().collectLatest { historySurahs ->
                historyAdapter.updateData(historySurahs)
                if (historySurahs.isEmpty()) {
                    tvNoHistory.visibility = View.VISIBLE
                    recyclerViewHistory.visibility = View.GONE
                } else {
                    tvNoHistory.visibility = View.GONE
                    recyclerViewHistory.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun deleteHistory(surahHistory: SurahHistory) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                surahDao.deleteHistory(surahHistory)
                Toast.makeText(context, "${surahHistory.namaLatin} dihapus dari riwayat", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal menghapus dari riwayat: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
