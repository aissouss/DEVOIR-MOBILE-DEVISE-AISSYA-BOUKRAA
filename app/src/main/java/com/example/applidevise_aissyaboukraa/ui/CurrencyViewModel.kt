package com.example.applidevise_aissyaboukraa.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.applidevise_aissyaboukraa.data.AppDatabase
import com.example.applidevise_aissyaboukraa.data.CurrencyRepository
import com.example.applidevise_aissyaboukraa.model.Currency
import com.example.applidevise_aissyaboukraa.model.ExchangeRate
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel principal utilisant Room via le Repository.
 */
class CurrencyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CurrencyRepository

    var state by mutableStateOf(CurrencyUiState())
        private set

    init {
        val db = AppDatabase.getInstance(application)
        repository = CurrencyRepository(db)

        viewModelScope.launch {
            refreshCurrencies()
            loadRatesForToday()
        }
    }

    private fun todayString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private suspend fun refreshCurrencies() {
        state = state.copy(currencies = repository.getCurrencies())
    }

    // ---------- Ecran creation ----------

    fun onCreateFieldChange(
        code: String? = null,
        name: String? = null,
        flag: String? = null,
        rate: String? = null
    ) {
        state = state.copy(
            createCode = code ?: state.createCode,
            createName = name ?: state.createName,
            createFlag = flag ?: state.createFlag,
            createRate = rate ?: state.createRate,
            createError = null,
            createSuccessMessage = null
        )
    }

    fun createCurrency() {
        val code = state.createCode.trim().uppercase()
        val name = state.createName.trim()
        val flag = state.createFlag.trim()
        val rateStr = state.createRate.trim()

        // Validation du code : obligatoire, exactement 3 lettres majuscules
        if (code.isEmpty()) {
            state = state.copy(createError = "Le code devise est obligatoire.")
            return
        }
        if (code.length != 3 || !code.all { it.isLetter() }) {
            state = state.copy(createError = "Le code devise doit contenir exactement 3 lettres (ex: USD, EUR).")
            return
        }

        // Validation de la designation : obligatoire, minimum 3 caracteres
        if (name.isEmpty()) {
            state = state.copy(createError = "La designation est obligatoire.")
            return
        }
        if (name.length < 3) {
            state = state.copy(createError = "La designation doit contenir au moins 3 caracteres.")
            return
        }

        // Validation du drapeau : obligatoire, doit etre un emoji
        if (flag.isEmpty()) {
            state = state.copy(createError = "Le drapeau emoji est obligatoire.")
            return
        }
        if (!containsEmoji(flag)) {
            state = state.copy(createError = "Le drapeau doit etre un emoji valide.")
            return
        }

        // Validation du cours : obligatoire, nombre decimal > 0
        if (rateStr.isEmpty()) {
            state = state.copy(createError = "Le cours par rapport au TND est obligatoire.")
            return
        }
        val rate = rateStr.toDoubleOrNull()
        if (rate == null || rate <= 0.0) {
            state = state.copy(createError = "Le cours doit etre un nombre decimal positif.")
            return
        }

        viewModelScope.launch {
            try {
                val added = repository.addCurrency(
                    Currency(code, name, flag, rate)
                )
                if (!added) {
                    state = state.copy(createError = "Le code de devise existe deja.")
                    return@launch
                }
                refreshCurrencies()
                state = state.copy(
                    createCode = "",
                    createName = "",
                    createFlag = "",
                    createRate = "",
                    createError = null,
                    createSuccessMessage = "Devise $code creee avec succes."
                )
            } catch (e: Exception) {
                state = state.copy(createError = "Erreur lors de la creation: ${e.message}")
            }
        }
    }

    private fun containsEmoji(text: String): Boolean {
        for (codePoint in text.codePoints().toArray()) {
            val type = Character.getType(codePoint)
            if (type == Character.SURROGATE.toInt() ||
                type == Character.OTHER_SYMBOL.toInt() ||
                type == Character.NON_SPACING_MARK.toInt() ||
                codePoint in 0x1F000..0x1FFFF ||
                codePoint in 0x2600..0x27BF ||
                codePoint in 0xFE00..0xFE0F ||
                codePoint in 0x1F900..0x1F9FF ||
                codePoint in 0x1FA00..0x1FA6F ||
                codePoint in 0x1FA70..0x1FAFF ||
                codePoint in 0x200D..0x200D ||
                codePoint in 0xE0020..0xE007F) {
                return true
            }
        }
        return false
    }

    // ---------- Ecran mise a jour ----------

    fun selectCurrencyForUpdate(code: String) {
        viewModelScope.launch {
            val currency = repository.getCurrencyByCode(code)
            if (currency != null) {
                state = state.copy(
                    selectedCodeForUpdate = currency.code,
                    updateRate = currency.currentRate.toString(),
                    updateError = null,
                    updateSuccessMessage = null
                )
            }
        }
    }

    fun onUpdateRateChange(newRate: String) {
        state = state.copy(
            updateRate = newRate,
            updateError = null,
            updateSuccessMessage = null
        )
    }

    fun updateCurrencyRate() {
        val code = state.selectedCodeForUpdate
        if (code == null) {
            state = state.copy(updateError = "Veuillez choisir une devise.")
            return
        }

        val rateStr = state.updateRate.trim()
        if (rateStr.isEmpty()) {
            state = state.copy(updateError = "Le nouveau cours est obligatoire.")
            return
        }

        val rate = rateStr.toDoubleOrNull()
        if (rate == null || rate <= 0.0) {
            state = state.copy(updateError = "Le cours doit etre un nombre decimal positif (> 0).")
            return
        }

        viewModelScope.launch {
            try {
                val ok = repository.updateRate(code, rate)
                if (!ok) {
                    state = state.copy(updateError = "Erreur lors de la mise a jour.")
                    return@launch
                }
                refreshCurrencies()
                val today = todayString()
                state = state.copy(
                    updateSuccessMessage = "Cours de $code mis a jour ($today).",
                    updateError = null
                )
            } catch (e: Exception) {
                state = state.copy(updateError = "Erreur lors de la mise a jour: ${e.message}")
            }
        }
    }

    // ---------- Ecran cours a une date ----------

    fun onDateChange(date: String) {
        state = state.copy(selectedDateText = date, dateError = null)
    }

    fun loadRatesForDate(date: String) {
        viewModelScope.launch {
            try {
                val rates = repository.getAllRatesAtDate(date)
                val currencies = repository.getCurrencies()

                val displayItems = currencies.mapNotNull { currency ->
                    val rateAtDate = rates.find { it.currencyCode == currency.code }
                    if (rateAtDate != null) {
                        val previousRate = repository.getPreviousRate(currency.code, date)
                        val variation = if (previousRate != null) {
                            rateAtDate.rate - previousRate.rate
                        } else null
                        val variationPercent = if (previousRate != null && previousRate.rate > 0) {
                            ((rateAtDate.rate - previousRate.rate) / previousRate.rate) * 100.0
                        } else null

                        RateDisplayItem(
                            code = currency.code,
                            name = currency.name,
                            flag = currency.flag,
                            rate = rateAtDate.rate,
                            date = rateAtDate.date,
                            variation = variation,
                            variationPercent = variationPercent
                        )
                    } else null
                }

                state = state.copy(
                    rateDisplayItems = displayItems,
                    selectedDateText = date,
                    dateError = if (displayItems.isEmpty()) "Aucun cours enregistre pour cette date." else null
                )
            } catch (e: Exception) {
                state = state.copy(dateError = "Erreur lors du chargement: ${e.message}")
            }
        }
    }

    fun loadRatesForToday() {
        val today = todayString()
        state = state.copy(selectedDateText = today)
        loadRatesForDate(today)
    }

    // ---------- Ecran conversion ----------

    fun onConversionChange(
        sourceCode: String? = null,
        targetCode: String? = null,
        amount: String? = null
    ) {
        state = state.copy(
            sourceCode = sourceCode ?: state.sourceCode,
            targetCode = targetCode ?: state.targetCode,
            amountInput = amount ?: state.amountInput,
            conversionError = null,
            conversionResult = null
        )
    }

    fun convert() {
        val src = state.sourceCode
        val dst = state.targetCode

        if (src == null) {
            state = state.copy(conversionError = "Veuillez selectionner une devise source.")
            return
        }
        if (dst == null) {
            state = state.copy(conversionError = "Veuillez selectionner une devise cible.")
            return
        }
        if (src == dst) {
            state = state.copy(conversionError = "Les devises source et cible doivent etre differentes.")
            return
        }

        val amountStr = state.amountInput.trim()
        if (amountStr.isEmpty()) {
            state = state.copy(conversionError = "Le montant est obligatoire.")
            return
        }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0.0) {
            state = state.copy(conversionError = "Le montant doit etre un nombre decimal positif (> 0).")
            return
        }

        viewModelScope.launch {
            try {
                val result = repository.convert(amount, src, dst)
                if (result == null) {
                    state = state.copy(conversionError = "Erreur de conversion. Verifiez que les devises existent.")
                } else {
                    state = state.copy(conversionResult = result, conversionError = null)
                }
            } catch (e: Exception) {
                state = state.copy(conversionError = "Erreur de conversion: ${e.message}")
            }
        }
    }
}

/**
 * Element d'affichage pour la liste des cours a une date.
 */
data class RateDisplayItem(
    val code: String,
    val name: String,
    val flag: String,
    val rate: Double,
    val date: String,
    val variation: Double?,         // variation absolue
    val variationPercent: Double?   // variation en pourcentage
)

/**
 * Donnees d'etat de l'application pour tous les ecrans.
 */
data class CurrencyUiState(
    val currencies: List<Currency> = emptyList(),

    // Ecran creation
    val createCode: String = "",
    val createName: String = "",
    val createFlag: String = "",
    val createRate: String = "",
    val createError: String? = null,
    val createSuccessMessage: String? = null,

    // Ecran mise a jour
    val selectedCodeForUpdate: String? = null,
    val updateRate: String = "",
    val updateError: String? = null,
    val updateSuccessMessage: String? = null,

    // Ecran cours a une date
    val selectedDateText: String = "",
    val rateDisplayItems: List<RateDisplayItem> = emptyList(),
    val dateError: String? = null,

    // Ecran conversion
    val sourceCode: String? = null,
    val targetCode: String? = null,
    val amountInput: String = "",
    val conversionResult: Double? = null,
    val conversionError: String? = null
)
