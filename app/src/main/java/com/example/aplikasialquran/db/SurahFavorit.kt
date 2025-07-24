package com.example.aplikasialquran.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "surah_favorit")
data class SurahFavorit(
    @PrimaryKey
    val nomor: Int,
    val namaLatin: String,
    val nama: String,
    val jumlahAyat: Int,
    val tempatTurun: String,
    val arti: String,
    val deskripsi: String,
    val audio: String
)