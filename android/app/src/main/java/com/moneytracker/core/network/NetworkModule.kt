package com.moneytracker.core.network

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.moneytracker.BuildConfig
import com.moneytracker.core.common.Constants
import com.moneytracker.core.network.api.AuthApi
import com.moneytracker.core.network.api.AccountsApi
import com.moneytracker.core.network.api.TransactionsApi
import com.moneytracker.core.network.api.CategoriesApi
import com.moneytracker.core.network.api.BudgetsApi
import com.moneytracker.core.network.api.GoalsApi
import com.moneytracker.core.network.api.AnalyticsApi
import com.moneytracker.core.network.api.ConsentsApi
import com.moneytracker.core.network.api.NotificationsApi
import com.moneytracker.core.network.interceptor.AuthInterceptor
import com.moneytracker.core.network.interceptor.LoggingInterceptor
import com.moneytracker.core.network.interceptor.NetworkInterceptor
import com.moneytracker.core.security.TokenManager
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.Singleton
import dagger.Module
import dagger.Provides
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.sentry.Sentry
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val CACHE_SIZE = 10 * 1024 * 1024L // 10 MB
    private const val CONNECT_TIMEOUT = 30L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        authInterceptor: AuthInterceptor,
        networkInterceptor: NetworkInterceptor,
        loggingInterceptor: LoggingInterceptor,
        tokenManager: TokenManager
    ): OkHttpClient {
        val cache = Cache(File(context.cacheDir, "http_cache"), CACHE_SIZE)

        val clientBuilder = OkHttpClient.Builder()
            .cache(cache)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(networkInterceptor)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .addNetworkInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("X-App-Version", BuildConfig.VERSION_NAME)
                    .header("X-Platform", "android")
                    .header("X-Device-ID", tokenManager.getDeviceId() ?: "")
                    .build()
                chain.proceed(request)
            }

        if (BuildConfig.DEBUG) {
            clientBuilder.addInterceptor(HttpLoggingInterceptor { message ->
                Sentry.addBreadcrumb("http", "OkHttp: $message")
            }.apply { level = HttpLoggingInterceptor.Level.BODY })
        }

        return clientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideAccountsApi(retrofit: Retrofit): AccountsApi = retrofit.create(AccountsApi::class.java)

    @Provides
    @Singleton
    fun provideTransactionsApi(retrofit: Retrofit): TransactionsApi = retrofit.create(TransactionsApi::class.java)

    @Provides
    @Singleton
    fun provideCategoriesApi(retrofit: Retrofit): CategoriesApi = retrofit.create(CategoriesApi::class.java)

    @Provides
    @Singleton
    fun provideBudgetsApi(retrofit: Retrofit): BudgetsApi = retrofit.create(BudgetsApi::class.java)

    @Provides
    @Singleton
    fun provideGoalsApi(retrofit: Retrofit): GoalsApi = retrofit.create(GoalsApi::class.java)

    @Provides
    @Singleton
    fun provideAnalyticsApi(retrofit: Retrofit): AnalyticsApi = retrofit.create(AnalyticsApi::class.java)

    @Provides
    @Singleton
    fun provideConsentsApi(retrofit: Retrofit): ConsentsApi = retrofit.create(ConsentsApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationsApi(retrofit: Retrofit): NotificationsApi = retrofit.create(NotificationsApi::class.java)

    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    fun initialize(context: Context) {
        // Module initialization if needed
    }
}