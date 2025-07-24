package com.example.aplikasialquran

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasialquran.model.Surah

class SurahAdapter(
    private var surahList: MutableList<Surah>,
    private val clickListener: (Surah) -> Unit,
    private val favoriteClickListener: (Surah, ImageView, Boolean) -> Unit
) : RecyclerView.Adapter<SurahAdapter.SurahViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_surah, parent, false)
        return SurahViewHolder(view)
    }

    override fun onBindViewHolder(holder: SurahViewHolder, position: Int) {
        val surah = surahList[position]
        holder.bind(surah, clickListener, favoriteClickListener)
    }

    override fun getItemCount(): Int = surahList.size

    fun updateData(newSurahList: List<Surah>) {
        this.surahList.clear()
        this.surahList.addAll(newSurahList)
        notifyDataSetChanged()
    }

    class SurahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvSurahNumber: TextView = itemView.findViewById(R.id.tvSurahNumber)
        private val tvSurahNameLatin: TextView = itemView.findViewById(R.id.tvSurahNameLatin)
        private val tvSurahNameArabic: TextView = itemView.findViewById(R.id.tvSurahNameArabic)
        private val tvSurahMeaningAyat: TextView = itemView.findViewById(R.id.tvSurahMeaningAyat)
        private val tvSurahPlace: TextView = itemView.findViewById(R.id.tvSurahPlace)
        private val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)

        fun bind(
            surah: Surah,
            clickListener: (Surah) -> Unit,
            favoriteClickListener: (Surah, ImageView, Boolean) -> Unit
        ) {
            tvSurahNumber.text = "${surah.nomor}."
            tvSurahNameLatin.text = surah.namaLatin
            tvSurahNameArabic.text = surah.nama
            tvSurahMeaningAyat.text = "${surah.arti} | ${surah.jumlahAyat} Ayat"
            tvSurahPlace.text = surah.tempatTurun

            itemView.setOnClickListener { clickListener(surah) }

            val iconResId = if (surah.status) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
            ivFavorite.setImageDrawable(ContextCompat.getDrawable(itemView.context, iconResId))

            ivFavorite.setOnClickListener {
                favoriteClickListener(surah, ivFavorite, surah.status)
            }
        }
    }
}
