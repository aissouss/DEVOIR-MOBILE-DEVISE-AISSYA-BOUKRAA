package com.example.applidevise_aissyaboukraa.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.applidevise_aissyaboukraa.model.Currency

@Dao
interface CurrencyDao {

    @Query("SELECT * FROM currencies ORDER BY code ASC")
    suspend fun getAll(): List<Currency>

    @Query("SELECT * FROM currencies WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): Currency?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(currency: Currency)

    @Query("UPDATE currencies SET currentRate = :rate WHERE code = :code")
    suspend fun updateRate(code: String, rate: Double)

    @Query("SELECT COUNT(*) FROM currencies WHERE code = :code")
    suspend fun exists(code: String): Int
}
