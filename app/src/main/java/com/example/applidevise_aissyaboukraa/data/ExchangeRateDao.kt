package com.example.applidevise_aissyaboukraa.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.applidevise_aissyaboukraa.model.ExchangeRate

@Dao
interface ExchangeRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(rate: ExchangeRate)

    @Query("SELECT * FROM exchange_rates WHERE currencyCode = :code AND date = :date LIMIT 1")
    suspend fun getRateAtDate(code: String, date: String): ExchangeRate?

    @Query("SELECT * FROM exchange_rates WHERE date = :date ORDER BY currencyCode ASC")
    suspend fun getAllRatesAtDate(date: String): List<ExchangeRate>

    @Query("""
        SELECT * FROM exchange_rates
        WHERE currencyCode = :code
        ORDER BY date DESC
        LIMIT 1
    """)
    suspend fun getLatestRate(code: String): ExchangeRate?

    @Query("""
        SELECT * FROM exchange_rates
        WHERE currencyCode = :code AND date < :date
        ORDER BY date DESC
        LIMIT 1
    """)
    suspend fun getPreviousRate(code: String, date: String): ExchangeRate?

    @Query("SELECT DISTINCT date FROM exchange_rates ORDER BY date DESC")
    suspend fun getAllDates(): List<String>
}
