package com.example.applidevise_aissyaboukraa.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entité Room pour l'historique des taux de change.
 * Chaque enregistrement représente le taux d'une devise à une date donnée.
 */
@Entity(
    tableName = "exchange_rates",
    foreignKeys = [
        ForeignKey(
            entity = Currency::class,
            parentColumns = ["code"],
            childColumns = ["currencyCode"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("currencyCode", "date", unique = true)]
)
data class ExchangeRate(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val currencyCode: String,   // code de la devise (FK)
    val rate: Double,           // taux par rapport au TND
    val date: String            // date au format "yyyy-MM-dd"
)
