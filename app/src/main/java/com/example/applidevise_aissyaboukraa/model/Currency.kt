package com.example.applidevise_aissyaboukraa.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entité Room représentant une devise.
 */
@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey
    val code: String,        // ex: "EUR"
    val name: String,        // ex: "Euro"
    val flag: String,        // emoji drapeau ex: "🇪🇺"
    val currentRate: Double  // dernier taux connu par rapport au TND
)
