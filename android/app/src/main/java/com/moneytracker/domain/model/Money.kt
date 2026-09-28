package com.moneytracker.domain.model

import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@Serializable
data class Money(
    val amount: Double,
    val currency: String = Constants.DEFAULT_CURRENCY
) {
    companion object {
        fun zero(currency: String = Constants.DEFAULT_CURRENCY): Money = Money(0.0, currency)
        fun of(amount: Double, currency: String = Constants.DEFAULT_CURRENCY): Money = Money(amount, currency)
    }

    val isZero: Boolean
        get() = amount == 0.0

    val isPositive: Boolean
        get() = amount > 0

    val isNegative: Boolean
        get() = amount < 0

    val abs: Money
        get() = Money(Math.abs(amount), currency)

    val formatted: String
        get() = format()

    val formattedWithSign: String
        get() = format(showSign = true)

    private fun format(showSign: Boolean = false): String {
        val formatter = java.text.NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        formatter.currency = Currency.getInstance(currency)
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        
        val formattedAmount = formatter.format(Math.abs(amount))
        
        return when {
            showSign && isPositive -> "+$formattedAmount"
            showSign && isNegative -> "-$formattedAmount"
            isNegative -> "-$formattedAmount"
            else -> formattedAmount
        }
    }

    operator fun plus(other: Money): Money {
        require(currency == other.currency) { "Cannot add different currencies" }
        return Money(amount + other.amount, currency)
    }

    operator fun minus(other: Money): Money {
        require(currency == other.currency) { "Cannot subtract different currencies" }
        return Money(amount - other.amount, currency)
    }

    operator fun times(multiplier: Double): Money {
        return Money(amount * multiplier, currency)
    }

    operator fun div(divisor: Double): Money {
        require(divisor != 0.0) { "Cannot divide by zero" }
        return Money(amount / divisor, currency)
    }

    fun compareTo(other: Money): Int {
        require(currency == other.currency) { "Cannot compare different currencies" }
        return amount.compareTo(other.amount)
    }

    fun toBigDecimal(): BigDecimal = BigDecimal.valueOf(amount).setScale(2, RoundingMode.HALF_UP)

    override fun toString(): String = formatted
}

@Serializable
data class Percentage(
    val value: Double
) {
    companion object {
        fun zero(): Percentage = Percentage(0.0)
        fun of(value: Double): Percentage = Percentage(value.coerceIn(0.0, 100.0))
    }

    val formatted: String
        get() = String.format(Locale.getDefault(), "%.1f%%", value)

    val isZero: Boolean
        get() = value == 0.0

    val isComplete: Boolean
        get() = value >= 100.0
}