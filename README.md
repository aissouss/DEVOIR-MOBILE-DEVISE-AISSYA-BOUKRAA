# 💱 Android Currency Manager

> **Academic Project** — A native Android application to manage foreign currency exchange rates against the Tunisian Dinar (TND), with real-time conversion and historical rate tracking.

---


## ✨ Features

### ➕ Create a Currency
- Add a new currency with its ISO code (e.g. USD, EUR), full name, flag emoji, and initial rate in TND
- Input validation: 3-letter code, minimum name length, valid emoji, positive rate
- Duplicate code detection

### 🔄 Update Exchange Rates
- Select any saved currency and update its rate for the current date
- Rate history is automatically saved (one entry per currency per day)

### 📅 View Rates by Date
- Pick any date from a calendar picker
- View all currencies' rates for that date in a sortable table
- Shows **rate variation** (absolute and percentage) compared to the previous recorded date
- Color-coded arrows: 🟢 up / 🔴 down / ⚪ stable

### 🔁 Currency Conversion
- Convert any amount between two currencies (including TND)
- Real-time result displayed instantly

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| [Kotlin](https://kotlinlang.org/) | Primary language |
| [Jetpack Compose](https://developer.android.com/jetpack/compose) | Declarative UI |
| [Room Database](https://developer.android.com/training/data-storage/room) | Local persistence |
| [ViewModel + State](https://developer.android.com/topic/libraries/architecture/viewmodel) | MVVM architecture |
| [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) | Async data operations |
| [Material Design 3](https://m3.material.io/) | UI components & theming |
| Android SDK 35 (min SDK 24) | Target platform |

---

## 🏗️ Architecture

This app follows the **MVVM** (Model-View-ViewModel) pattern with a clean layered structure:

```
app/src/main/java/com/example/applidevise_aissyaboukraa/
├── model/                  # Room entities
│   ├── Currency.kt         # Currency (code, name, flag, currentRate)
│   └── ExchangeRate.kt     # Historical rate (currencyCode, rate, date)
├── data/                   # Data layer
│   ├── AppDatabase.kt
│   ├── CurrencyDao.kt
│   ├── ExchangeRateDao.kt
│   └── CurrencyRepository.kt
└── ui/                     # Presentation layer
    ├── CurrencyViewModel.kt
    ├── Screens.kt           # All Compose screens
    └── theme/
```

---

## 🗄️ Data Model

```
Currency
├── code (PK)       → "EUR"
├── name            → "Euro"
├── flag            → "🇪🇺"
└── currentRate     → 3.3500 (vs TND)

ExchangeRate
├── id (PK, auto)
├── currencyCode (FK → Currency)
├── rate            → 3.3500
└── date            → "2025-01-15"
```

A unique index on `(currencyCode, date)` ensures one rate entry per currency per day.

---

## 🚀 Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Hedgehog or later)
- Android device or emulator with API 24+

### Installation

```bash
# 1. Clone the repository
git clone https://github.com/aissouss/android-currency-manager.git

# 2. Open in Android Studio
File → Open → select the cloned folder

# 3. Sync Gradle
Click "Sync Now" when prompted

# 4. Run the app
Click ▶ Run or press Shift + F10
```

---

## 🎓 Academic Context

This project was developed as a mobile development assignment (Devoir Mobile) during the L3 Software Engineering program.

**Authors:** Aissya Boukraa  
[GitHub](https://github.com/aissouss)

---

## 📄 License

This project is for educational use only.
