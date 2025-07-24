package com.example.aplikasialquran

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasialquran.db.SurahHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private var surahList: MutableList<SurahHistory>,
    private val onItemClick: (SurahHistory) -> Unit,
    private val onDeleteClick: (SurahHistory) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.SurahHistoryViewHolder>() {

    class SurahHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvSurahNumber: TextView = itemView.findViewById(R.id.tvSurahNumber)
        val tvSurahNameLatin: TextView = itemView.findViewById(R.id.tvSurahNameLatin)
        val tvSurahNameArabic: TextView = itemView.findViewById(R.id.tvSurahNameArabic)
        val tvSurahMeaningAyat: TextView = itemView.findViewById(R.id.tvSurahMeaningAyat)
        val tvSurahPlace: TextView = itemView.findViewById(R.id.tvSurahPlace)
        val tvLastOpened: TextView = itemView.findViewById(R.id.tvLastOpened)
        val ivDeleteHistory: ImageView = itemView.findViewById(R.id.ivDeleteHistory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history_surah, parent, false)
        return SurahHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: SurahHistoryViewHolder, position: Int) {
        val currentSurah = surahList[position]
        holder.tvSurahNumber.text = "${currentSurah.nomor}."
        holder.tvSurahNameLatin.text = currentSurah.namaLatin
        holder.tvSurahNameArabic.text = currentSurah.nama
        holder.tvSurahMeaningAyat.text = "${currentSurah.arti} | ${currentSurah.jumlahAyat} Ayat"
        holder.tvSurahPlace.text = currentSurah.tempatTurun

        val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        holder.tvLastOpened.text = "Dibuka: ${dateFormat.format(Date(currentSurah.timestamp))}"

        holder.itemView.setOnClickListener { onItemClick(currentSurah) }
        holder.ivDeleteHistory.setOnClickListener { onDeleteClick(currentSurah) }
    }

    override fun getItemCount(): Int = surahList.size

    fun updateData(newSurahList: List<SurahHistory>) {
        surahList.clear()
        surahList.addAll(newSurahList)
        notifyDataSetChanged()
    }
}
