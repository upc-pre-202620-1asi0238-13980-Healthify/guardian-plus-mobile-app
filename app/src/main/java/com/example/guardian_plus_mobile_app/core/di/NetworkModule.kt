package com.example.guardian_plus_mobile_app.core.di

import com.example.guardian_plus_mobile_app.BuildConfig
import com.example.guardian_plus_mobile_app.core.time.ServerClock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.Locale
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(serverClock: ServerClock): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                // The platform localizes its error messages (messages_es.properties) from this header
                val request = chain.request().newBuilder()
                    .header("Accept-Language", Locale.getDefault().toLanguageTag())
                    .build()
                val response = chain.proceed(request)
                response.headers.getDate("Date")?.let(serverClock::sync)
                response
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
