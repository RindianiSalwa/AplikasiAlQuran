package com.example.aplikasialquran

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasialquran.model.Ayat
import androidx.core.text.HtmlCompat

class AyatAdapter(private val ayatList: List<Ayat>) :
    RecyclerView.Adapter<AyatAdapter.AyatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AyatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ayat, parent, false)
        return AyatViewHolder(view)
    }

    override fun onBindViewHolder(holder: AyatViewHolder, position: Int) {
        val ayat = ayatList[position]
        holder.bind(ayat)
    }

    override fun getItemCount(): Int = ayatList.size

    class AyatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAyatNumber: TextView = itemView.findViewById(R.id.tvAyatNumber)
        private val tvAyatArabic: TextView = itemView.findViewById(R.id.tvAyatArabic)
        private val tvAyatTransliteration: TextView = itemView.findViewById(R.id.tvAyatTransliteration)
        private val tvAyatIndonesia: TextView = itemView.findViewById(R.id.tvAyatIndonesia)

        fun bind(ayat: Ayat) {
            tvAyatNumber.text = "${ayat.nomor}."
            tvAyatArabic.text = ayat.arabicText
            tvAyatTransliteration.text = HtmlCompat.fromHtml(ayat.transliteration, HtmlCompat.FROM_HTML_MODE_LEGACY)
            tvAyatIndonesia.text = ayat.indonesiaText
        }
    }
}