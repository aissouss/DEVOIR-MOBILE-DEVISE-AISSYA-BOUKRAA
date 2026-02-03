package com.example.applidevise_aissyaboukraa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.applidevise_aissyaboukraa.ui.theme.APPLIDEVISEAISSYABOUKRAATheme
import com.example.applidevise_aissyaboukraa.ui.CreateCurrencyScreen
import com.example.applidevise_aissyaboukraa.ui.CurrencyConversionScreen
import com.example.applidevise_aissyaboukraa.ui.CurrencyRateAtDateScreen
import com.example.applidevise_aissyaboukraa.ui.CurrencyViewModel
import com.example.applidevise_aissyaboukraa.ui.ScreenWithMenu
import com.example.applidevise_aissyaboukraa.ui.UpdateCurrencyRateScreen

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val viewModel: CurrencyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            APPLIDEVISEAISSYABOUKRAATheme {
                val state = viewModel.state
                var currentScreen by remember { mutableStateOf(AppScreen.Create) }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Convertisseur de devises (TND)",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        ScreenWithMenu(
                            onNavigateCreate = { currentScreen = AppScreen.Create },
                            onNavigateUpdate = { currentScreen = AppScreen.Update },
                            onNavigateRateAtDate = {
                                currentScreen = AppScreen.RateAtDate
                                viewModel.loadRatesForToday()
                            },
                            onNavigateConvert = { currentScreen = AppScreen.Convert }
                        ) {
                            when (currentScreen) {
                                AppScreen.Create -> {
                                    CreateCurrencyScreen(
                                        state = state,
                                        onFieldChange = { code, name, flag, rate ->
                                            viewModel.onCreateFieldChange(code, name, flag, rate)
                                        },
                                        onCreate = { viewModel.createCurrency() }
                                    )
                                }

                                AppScreen.Update -> {
                                    UpdateCurrencyRateScreen(
                                        state = state,
                                        onSelectCurrency = { viewModel.selectCurrencyForUpdate(it) },
                                        onRateChange = { viewModel.onUpdateRateChange(it) },
                                        onUpdate = { viewModel.updateCurrencyRate() }
                                    )
                                }

                                AppScreen.RateAtDate -> {
                                    CurrencyRateAtDateScreen(
                                        state = state,
                                        onDateChange = { viewModel.onDateChange(it) },
                                        onLoadRates = { viewModel.loadRatesForDate(it) }
                                    )
                                }

                                AppScreen.Convert -> {
                                    CurrencyConversionScreen(
                                        state = state,
                                        onSelectionChange = { src, dst ->
                                            viewModel.onConversionChange(
                                                sourceCode = src,
                                                targetCode = dst
                                            )
                                        },
                                        onAmountChange = { viewModel.onConversionChange(amount = it) },
                                        onConvert = { viewModel.convert() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class AppScreen {
    Create,
    Update,
    RateAtDate,
    Convert
}
