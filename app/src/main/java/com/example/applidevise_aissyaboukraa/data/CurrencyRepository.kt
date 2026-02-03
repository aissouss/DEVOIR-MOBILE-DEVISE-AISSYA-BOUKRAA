package com.example.applidevise_aissyaboukraa.data

import com.example.applidevise_aissyaboukraa.model.Currency
import com.example.applidevise_aissyaboukraa.model.ExchangeRate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.round

/**
 * Repository utilisant Room pour persister les devises et l'historique des cours.
 */
class CurrencyRepository(private val db: AppDatabase) {

    private val currencyDao = db.currencyDao()
    private val rateDao = db.exchangeRateDao()

    private fun todayString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    // ---- Devises ----

    suspend fun getCurrencies(): List<Currency> = currencyDao.getAll()

    suspend fun getCurrencyByCode(code: String): Currency? = currencyDao.getByCode(code)

    /**
     * Ajoute une devise. Retourne false si le code existe déjà.
     * Enregistre aussi le taux initial dans l'historique avec la date du jour.
     */
    suspend fun addCurrency(currency: Currency): Boolean {
        if (currencyDao.exists(currency.code.uppercase()) > 0) return false
        val normalized = currency.copy(code = currency.code.uppercase())
        currencyDao.insert(normalized)
        // Enregistrer le taux initial dans l'historique
        rateDao.insertOrUpdate(
            ExchangeRate(
                currencyCode = normalized.code,
                rate = normalized.currentRate,
                date = todayString()
            )
        )
        return true
    }

    // ---- Taux de change ----

    /**
     * Met à jour le taux d'une devise et enregistre dans l'historique.
     */
    suspend fun updateRate(code: String, newRate: Double): Boolean {
        val currency = currencyDao.getByCode(code) ?: return false
        currencyDao.updateRate(code, newRate)
        rateDao.insertOrUpdate(
            ExchangeRate(
                currencyCode = code,
                rate = newRate,
                date = todayString()
            )
        )
        return true
    }

    /**
     * Récupère le taux d'une devise à une date donnée.
     */
    suspend fun getRateAtDate(code: String, date: String): ExchangeRate? {
        return rateDao.getRateAtDate(code, date)
    }

    /**
     * Récupère le taux précédent (avant la date donnée) pour calculer la variation.
     */
    suspend fun getPreviousRate(code: String, date: String): ExchangeRate? {
        return rateDao.getPreviousRate(code, date)
    }

    /**
     * Récupère tous les taux à une date donnée.
     */
    suspend fun getAllRatesAtDate(date: String): List<ExchangeRate> {
        return rateDao.getAllRatesAtDate(date)
    }

    /**
     * Récupère toutes les dates disponibles dans l'historique.
     */
    suspend fun getAllDates(): List<String> = rateDao.getAllDates()

    // ---- Conversion ----

    /**
     * Conversion via TND comme pivot :
     * montantConverti = montantSource × (tauxSource / tauxCible)
     * Où tauxSource et tauxCible sont les taux par rapport au TND (1 unité = X TND).
     * TND est traité comme devise virtuelle avec taux = 1.0.
     */
    suspend fun convert(
        amountSource: Double,
        sourceCode: String,
        targetCode: String
    ): Double? {
        val sourceRate = if (sourceCode == "TND") 1.0
            else currencyDao.getByCode(sourceCode)?.currentRate ?: return null
        val targetRate = if (targetCode == "TND") 1.0
            else currencyDao.getByCode(targetCode)?.currentRate ?: return null

        val result = amountSource * (sourceRate / targetRate)
        return round(result * 100.0) / 100.0
    }
}
