package com.example.aplikasialquran.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SurahDao {
    //favorit
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(surah: SurahFavorit)

    @Delete
    suspend fun delete(surah: SurahFavorit)

    @Query("SELECT * FROM surah_favorit")
    fun getAllFavoriteSurah(): Flow<List<SurahFavorit>>

    @Query("SELECT EXISTS(SELECT 1 FROM surah_favorit WHERE nomor = :surahNumber LIMIT 1)")
    suspend fun isSurahFavorite(surahNumber: Int): Boolean

    //history
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(surahHistory: SurahHistory)

    @Delete
    suspend fun deleteHistory(surahHistory: SurahHistory)

    @Query("SELECT * FROM surah_history ORDER BY timestamp DESC")
    fun getAllHistorySurah(): Flow<List<SurahHistory>>

    @Query("DELETE FROM surah_history")
    suspend fun clearAllHistory()
}
