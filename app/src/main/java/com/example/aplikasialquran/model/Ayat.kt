package com.example.aplikasialquran.model

import com.google.gson.annotations.SerializedName

data class Ayat(
    @SerializedName("id")
    val id: Int,
    @SerializedName("surah")
    val surah: Int,
    @SerializedName("nomor")
    val nomor: Int,
    @SerializedName("ar")
    val arabicText: String,
    @SerializedName("tr")
    val transliteration: String,
    @SerializedName("idn")
    val indonesiaText: String
)