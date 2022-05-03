package protemplate.di

import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import protemplate.data.prefs.AppPreferences
import protemplate.data.remote.BaseApiService
import protemplate.util.AppConstants.KEY_ACCESS_TOKEN
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.BuildConfig
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by promasterguru on 03/05/2022.
 */
object KoinModules {
    val prefModule = module { single { AppPreferences.instance } }
    val retrofitModule = module {
        fun provideGson(): Gson {
            return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create()
        }

        fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }
        }

        fun provideHttpNetworkInterceptor(preferences: AppPreferences) = Interceptor { chain ->
            val originalRequest = chain.request()
            val newRequest = originalRequest.newBuilder()
                .header("Authorization", "Bearer " + preferences.getString(KEY_ACCESS_TOKEN))
                //.addHeader("key", Value)
                .build()
            chain.proceed(newRequest)
        }

        fun provideHttpClient(
            loggingInterceptor: HttpLoggingInterceptor,
            networkInterceptor: Interceptor
        ): OkHttpClient {
            val okHttpClient = OkHttpClient.Builder().apply {
                addInterceptor(loggingInterceptor)
                    .addInterceptor(networkInterceptor)
            }.build()
            return okHttpClient
        }

        fun provideRetrofit(factory: Gson, okHttpClient: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://your_base_url.com/")
                .addConverterFactory(GsonConverterFactory.create(factory))
                .client(okHttpClient)
                .build()
        }

        single { provideGson() }
        single { provideHttpLoggingInterceptor() }
        single { provideHttpNetworkInterceptor(get()) }
        single { provideHttpClient(get(), get()) }
        single { provideRetrofit(get(), get()) }
    }
    val apiModule = module {
        fun provideApi(retrofit: Retrofit): BaseApiService {
            return retrofit.create(BaseApiService::class.java)
        }
        single { provideApi(get()) }
    }
}
