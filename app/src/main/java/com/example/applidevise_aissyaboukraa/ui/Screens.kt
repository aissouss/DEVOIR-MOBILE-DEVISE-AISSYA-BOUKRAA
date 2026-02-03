package com.example.applidevise_aissyaboukraa.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.applidevise_aissyaboukraa.model.Currency
import java.util.Calendar

// ============================================================
//  Ecran 1 : Creation d'une devise
// ============================================================

@Composable
fun CreateCurrencyScreen(
    state: CurrencyUiState,
    onFieldChange: (code: String?, name: String?, flag: String?, rate: String?) -> Unit,
    onCreate: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Creer une devise", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = state.createCode,
            onValueChange = { onFieldChange(it, null, null, null) },
            label = { Text("Code (ex: USD, EUR)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.createName,
            onValueChange = { onFieldChange(null, it, null, null) },
            label = { Text("Designation (ex: Dollar US)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.createFlag,
            onValueChange = { onFieldChange(null, null, it, null) },
            label = { Text("Drapeau (emoji, ex: \uD83C\uDDFA\uD83C\uDDF8)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = state.createRate,
            onValueChange = { onFieldChange(null, null, null, it) },
            label = { Text("Cours par rapport au TND") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (state.createError != null) {
            Text(state.createError, color = MaterialTheme.colorScheme.error)
        }
        if (state.createSuccessMessage != null) {
            Text(state.createSuccessMessage, color = MaterialTheme.colorScheme.primary)
        }

        Button(
            onClick = onCreate,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Enregistrer")
        }

        // Liste des devises existantes
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text("Devises enregistrees", style = MaterialTheme.typography.titleMedium)
        if (state.currencies.isEmpty()) {
            Text(
                "Aucune devise enregistree.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            state.currencies.forEach { currency ->
                CurrencyListItem(currency)
            }
        }
    }
}

@Composable
private fun CurrencyListItem(currency: Currency) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = currency.flag,
                fontSize = 28.sp
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${currency.code} - ${currency.name}",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "1 ${currency.code} = ${currency.currentRate} TND",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============================================================
//  Ecran 2 : Mise a jour des cours
// ============================================================

@Composable
fun UpdateCurrencyRateScreen(
    state: CurrencyUiState,
    onSelectCurrency: (String) -> Unit,
    onRateChange: (String) -> Unit,
    onUpdate: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Mettre a jour le cours", style = MaterialTheme.typography.titleLarge)

        if (state.currencies.isEmpty()) {
            Text(
                "Aucune devise enregistree. Veuillez d'abord creer une devise.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        CurrencyDropdown(
            label = "Devise",
            currencies = state.currencies,
            selectedCode = state.selectedCodeForUpdate,
            onSelect = onSelectCurrency
        )

        if (state.selectedCodeForUpdate != null) {
            val selected = state.currencies.find { it.code == state.selectedCodeForUpdate }
            if (selected != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(selected.flag, fontSize = 24.sp)
                        Text(
                            "${selected.code} - ${selected.name}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        OutlinedTextField(
            value = state.updateRate,
            onValueChange = onRateChange,
            label = { Text("Nouveau cours (TND)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (state.updateError != null) {
            Text(state.updateError, color = MaterialTheme.colorScheme.error)
        }
        if (state.updateSuccessMessage != null) {
            Text(state.updateSuccessMessage, color = MaterialTheme.colorScheme.primary)
        }

        Button(
            onClick = onUpdate,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Mettre a jour")
        }
    }
}

// ============================================================
//  Ecran 3 : Affichage des cours a une date
// ============================================================

@Composable
fun CurrencyRateAtDateScreen(
    state: CurrencyUiState,
    onDateChange: (String) -> Unit,
    onLoadRates: (String) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Cours des devises", style = MaterialTheme.typography.titleLarge)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = state.selectedDateText,
                onValueChange = onDateChange,
                label = { Text("Date (yyyy-MM-dd)") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                readOnly = true
            )

            val context = LocalContext.current
            OutlinedButton(onClick = {
                val cal = Calendar.getInstance()
                // Parse current date if possible
                try {
                    val parts = state.selectedDateText.split("-")
                    if (parts.size == 3) {
                        cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
                    }
                } catch (_: Exception) { }

                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        val picked = String.format("%04d-%02d-%02d", year, month + 1, day)
                        onDateChange(picked)
                        onLoadRates(picked)
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }) {
                Text("Calendrier")
            }

            Button(onClick = { onLoadRates(state.selectedDateText) }) {
                Text("Charger")
            }
        }

        if (state.dateError != null) {
            Text(state.dateError, color = MaterialTheme.colorScheme.error)
        }

        if (state.rateDisplayItems.isNotEmpty()) {
            // En-tete
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Devise",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.2f),
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    "Cours (TND)",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    "Variation",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Liste scrollable
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(state.rateDisplayItems) { item ->
                    RateItemRow(item)
                }
            }
        }
    }
}

@Composable
private fun RateItemRow(item: RateDisplayItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Drapeau + code
            Row(
                modifier = Modifier.weight(1.2f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(item.flag, fontSize = 22.sp)
                Column {
                    Text(item.code, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(item.name, fontSize = 11.sp, color = Color.Gray)
                }
            }

            // Cours
            Text(
                text = String.format("%.4f", item.rate),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )

            // Variation
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                if (item.variation != null) {
                    val isUp = item.variation > 0
                    val arrow = if (isUp) "\u2191" else if (item.variation < 0) "\u2193" else "\u2192"
                    val color = if (isUp) Color(0xFF2E7D32) else if (item.variation < 0) Color(0xFFC62828) else Color.Gray

                    Text(
                        text = "$arrow ${String.format("%.4f", kotlin.math.abs(item.variation))}",
                        color = color,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    if (item.variationPercent != null) {
                        Text(
                            text = "(${String.format("%+.2f", item.variationPercent)}%)",
                            color = color,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    Text("--", color = Color.Gray, fontSize = 13.sp)
                }
            }
        }
    }
}

// ============================================================
//  Ecran 4 : Conversion de devises
// ============================================================

@Composable
fun CurrencyConversionScreen(
    state: CurrencyUiState,
    onSelectionChange: (source: String?, target: String?) -> Unit,
    onAmountChange: (String) -> Unit,
    onConvert: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Conversion de devises", style = MaterialTheme.typography.titleLarge)

        if (state.currencies.isEmpty()) {
            Text(
                "Aucune devise enregistree. Veuillez d'abord creer une devise.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // TND est toujours disponible comme option de conversion
        val allOptions = buildList {
            // Ajouter TND en tant qu'option virtuelle
            add(Currency("TND", "Dinar Tunisien", "\uD83C\uDDF9\uD83C\uDDF3", 1.0))
            addAll(state.currencies)
        }

        CurrencyDropdown(
            label = "Devise source",
            currencies = allOptions,
            selectedCode = state.sourceCode,
            onSelect = { onSelectionChange(it, state.targetCode) }
        )

        CurrencyDropdown(
            label = "Devise cible",
            currencies = allOptions,
            selectedCode = state.targetCode,
            onSelect = { onSelectionChange(state.sourceCode, it) }
        )

        OutlinedTextField(
            value = state.amountInput,
            onValueChange = onAmountChange,
            label = { Text("Montant a convertir") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (state.conversionError != null) {
            Text(state.conversionError, color = MaterialTheme.colorScheme.error)
        }

        Button(
            onClick = onConvert,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Convertir")
        }

        if (state.conversionResult != null && state.sourceCode != null && state.targetCode != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Resultat",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${state.amountInput} ${state.sourceCode} = ${state.conversionResult} ${state.targetCode}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

// ============================================================
//  Composants reutilisables
// ============================================================

@Composable
fun CurrencyDropdown(
    label: String,
    currencies: List<Currency>,
    selectedCode: String?,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = currencies.find { it.code == selectedCode }

    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(4.dp))
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (selected != null) {
                Text("${selected.flag} ${selected.code} - ${selected.name}")
            } else {
                Text("Choisir...")
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { Text("${currency.flag} ${currency.code} - ${currency.name}") },
                    onClick = {
                        onSelect(currency.code)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Conteneur avec un menu de navigation entre les ecrans.
 */
@Composable
fun ScreenWithMenu(
    onNavigateCreate: () -> Unit,
    onNavigateUpdate: () -> Unit,
    onNavigateRateAtDate: () -> Unit,
    onNavigateConvert: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(
        Modifier.fillMaxSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateCreate,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
            ) {
                Text("Creer", fontSize = 11.sp, maxLines = 1)
            }
            OutlinedButton(
                onClick = onNavigateUpdate,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
            ) {
                Text("Maj cours", fontSize = 11.sp, maxLines = 1)
            }
            OutlinedButton(
                onClick = onNavigateRateAtDate,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
            ) {
                Text("Cours", fontSize = 11.sp, maxLines = 1)
            }
            OutlinedButton(
                onClick = onNavigateConvert,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
            ) {
                Text("Convertir", fontSize = 11.sp, maxLines = 1)
            }
        }
        HorizontalDivider()
        Box(
            Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            content()
        }
    }
}
