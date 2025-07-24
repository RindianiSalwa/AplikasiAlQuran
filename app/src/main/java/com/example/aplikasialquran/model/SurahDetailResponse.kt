package com.example.aplikasialquran.model

import com.google.gson.annotations.SerializedName

data class SurahDetailResponse(
    @SerializedName("nomor")
    val nomor: Int,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("nama_latin")
    val namaLatin: String,
    @SerializedName("jumlah_ayat")
    val jumlahAyat: Int,
    @SerializedName("tempat_turun")
    val tempatTurun: String,
    @SerializedName("arti")
    val arti: String,
    @SerializedName("deskripsi")
    val deskripsi: String,
    @SerializedName("audio")
    val audio: String,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("ayat")
    val ayat: List<Ayat>
)