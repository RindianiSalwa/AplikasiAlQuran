package com.example.aplikasialquran.api

import com.example.aplikasialquran.model.Surah
import com.example.aplikasialquran.model.SurahDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("surah")
    suspend fun getListSurah(): Response<List<Surah>>

    @GET("surah/{nomor}")
    suspend fun getDetailSurah(@Path("nomor") nomor: Int): Response<SurahDetailResponse>
}