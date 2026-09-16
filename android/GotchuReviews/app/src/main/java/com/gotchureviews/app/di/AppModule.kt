package com.gotchureviews.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.gotchureviews.app.data.remote.ApiService
import com.gotchureviews.app.data.remote.AuthInterceptor
import com.gotchureviews.app.data.remote.FirebaseAuthApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "gotchu_prefs")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val BASE_URL = "https://xduzpwxmsp.us-east-1.awsapprunner.com/api/v1/"
    private const val FIREBASE_AUTH_URL = "https://identitytoolkit.googleapis.com/"
    private const val FIREBASE_TOKEN_URL = "https://securetoken.googleapis.com/"

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(okHttpClient: OkHttpClient, json: Json): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthApi(json: Json): FirebaseAuthApi {
        // Firebase APIs don't need the auth interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        // We need two base URLs: identitytoolkit for signInWithIdp, securetoken for refresh.
        // Since Retrofit needs one base URL, we use identitytoolkit as the base
        // and create a separate Retrofit instance for token refresh.
        return Retrofit.Builder()
            .baseUrl(FIREBASE_AUTH_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(FirebaseAuthApi::class.java)
    }

    @Provides
    @Singleton
    @Named("firebaseTokenApi")
    fun provideFirebaseTokenApi(json: Json): FirebaseAuthApi {
        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(FIREBASE_TOKEN_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(FirebaseAuthApi::class.java)
    }
}
