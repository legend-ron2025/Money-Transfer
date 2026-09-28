package com.moneytracker.core.common

object Constants {

    object TransactionType {
        const val DEBIT = "DEBIT"
        const val CREDIT = "CREDIT"
        const val TRANSFER = "TRANSFER"
    }

    object TransactionStatus {
        const val PENDING = "PENDING"
        const val COMPLETED = "COMPLETED"
        const val FAILED = "FAILED"
        const val CANCELLED = "CANCELLED"
    }

    object TransactionSource {
        const val BANK = "BANK"
        const val AA = "AA"
        const val MANUAL = "MANUAL"
        const val UPI = "UPI"
        const val CARD = "CARD"
    }

    object CategoryType {
        const val INCOME = "INCOME"
        const val EXPENSE = "EXPENSE"
        const val TRANSFER = "TRANSFER"
    }

    object BudgetPeriod {
        const val WEEKLY = "WEEKLY"
        const val MONTHLY = "MONTHLY"
        const val QUARTERLY = "QUARTERLY"
        const val YEARLY = "YEARLY"
        const val CUSTOM = "CUSTOM"
    }

    object BudgetStatus {
        const val NORMAL = "NORMAL"
        const val WATCH = "WATCH"
        const val NEAR_LIMIT = "NEAR_LIMIT"
        const val EXCEEDED = "EXCEEDED"
    }

    object GoalStatus {
        const val ACTIVE = "ACTIVE"
        const val COMPLETED = "COMPLETED"
        const val PAUSED = "PAUSED"
        const val CANCELLED = "CANCELLED"
    }

    object ConsentStatus {
        const val PENDING = "PENDING"
        const val ACTIVE = "ACTIVE"
        const val EXPIRED = "EXPIRED"
        const val REVOKED = "REVOKED"
        const val FAILED = "FAILED"
    }

    object SyncStatus {
        const val IDLE = "IDLE"
        const val SYNCING = "SYNCING"
        const val SUCCESS = "SUCCESS"
        const val ERROR = "ERROR"
        const val PARTIAL = "PARTIAL"
    }

    object AccountSyncStatus {
        const val SYNCED = "SYNCED"
        const val SYNCING = "SYNCING"
        const val ERROR = "ERROR"
        const val PENDING = "PENDING"
        const val DISCONNECTED = "DISCONNECTED"
    }

    object NotificationType {
        const val TRANSACTION = "TRANSACTION"
        const val BUDGET = "BUDGET"
        const val SUBSCRIPTION = "SUBSCRIPTION"
        const val GOAL = "GOAL"
        const val SYNC = "SYNC"
        const val SECURITY = "SECURITY"
        const val MARKETING = "MARKETING"
        const val INSIGHT = "INSIGHT"
    }

    object NotificationPriority {
        const val LOW = "LOW"
        const val NORMAL = "NORMAL"
        const val HIGH = "HIGH"
        const val URGENT = "URGENT"
    }

    object AnalyticsPeriod {
        const val TODAY = "today"
        const val THIS_WEEK = "this_week"
        const val THIS_MONTH = "this_month"
        const val LAST_MONTH = "last_month"
        const val THIS_QUARTER = "this_quarter"
        const val THIS_YEAR = "this_year"
        const val CUSTOM = "custom"
    }

    object SyncFrequency {
        const val REALTIME = "realtime"
        const val FIVE_MIN = "5min"
        const val FIFTEEN_MIN = "15min"
        const val THIRTY_MIN = "30min"
        const val HOURLY = "hourly"
        const val DAILY = "daily"
    }

    const val DEFAULT_CURRENCY = "INR"
    const val DEFAULT_LOCALE = "en_IN"
    const val MAX_TRANSACTION_AMOUNT = 999999999.99
    const val MIN_TRANSACTION_AMOUNT = 0.01
    const val MAX_NOTE_LENGTH = 500
    const val MAX_TAGS_COUNT = 10
    const val MAX_SEARCH_HISTORY = 20

    object IntentExtras {
        const val TRANSACTION_ID = "transaction_id"
        const val ACCOUNT_ID = "account_id"
        const val CATEGORY_ID = "category_id"
        const val BUDGET_ID = "budget_id"
        const val GOAL_ID = "goal_id"
        const val CONSENT_ID = "consent_id"
    }

    object SharedPrefsKeys {
        const val AUTH_PREFS = "auth_prefs"
        const val USER_PREFS = "user_prefs"
        const val SETTINGS_PREFS = "settings_prefs"
        const val ONBOARDING_COMPLETE = "onboarding_complete"
        const val FIRST_LAUNCH = "first_launch"
        const val LAST_SYNC_TIME = "last_sync_time"
        const val APP_VERSION = "app_version"
    }

    object DatabaseConstants {
        const val DB_NAME = "moneytracker.db"
        const val DB_VERSION = 1
    }

    object NetworkConstants {
        const val BASE_URL_PROD = "https://api.moneytracker.app/v1/"
        const val BASE_URL_STAGING = "https://staging-api.moneytracker.app/v1/"
        const val BASE_URL_DEV = "https://dev-api.moneytracker.app/v1/"
        const val CONNECT_TIMEOUT = 30_000L
        const val READ_TIMEOUT = 30_000L
        const val WRITE_TIMEOUT = 30_000L
    }
}