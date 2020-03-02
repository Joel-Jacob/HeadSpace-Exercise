package com.example.hsexercise.common

import com.example.hsexercise.BuildConfig
import com.example.hsexercise.feature.database.FeatureModel
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier

object NetworkProvider {
    private var picturesService : PicturesService

    init {
        picturesService = createService(provideRestClient().createRetrofitAdapter())
    }

    fun provideRestClient() =
        RestClient(RestClientConfig(
            provideGsonConverterFactory(),
            provideRxJava2CallAdapterFactory()).apply {
            addInterceptor(provideHttpLoggingInterceptor())
        })

    fun getPictures(): Observable<List<FeatureModel>>{
        return picturesService.getPicturesList()
    }

    fun getPicturesPage(page:Int):Observable<List<FeatureModel>>{
        return picturesService.getPicturesPage(page)
    }

    private fun createService(retrofit: Retrofit):PicturesService{
        return retrofit.create(PicturesService::class.java)
    }

    private fun provideGson(): Gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .serializeNulls()
        .create()

    private fun provideHttpLoggingInterceptor() =
        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT)
            .apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.BASIC
                }
            }

    private fun provideGsonConverterFactory(): GsonConverterFactory =
        GsonConverterFactory.create(provideGson())

    private fun provideRxJava2CallAdapterFactory(): RxJava2CallAdapterFactory =
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())
}

class RestClient(private val restClientConfig: RestClientConfig) {
    fun createRetrofitAdapter(hostUrl: String = "https://picsum.photos"): Retrofit = Retrofit.Builder()
        .addCallAdapterFactory(restClientConfig.callAdapterFactory)
        .addConverterFactory(restClientConfig.converterFactory)
        .client(okHttpClient())
        .baseUrl(hostUrl)
        .build()

    private fun okHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .readTimeout(restClientConfig.readTimeOutValue, TimeUnit.SECONDS)
            .writeTimeout(restClientConfig.writeTimeOutValue, TimeUnit.SECONDS)
            .connectTimeout(restClientConfig.connectTimeOutValue, TimeUnit.SECONDS)

        restClientConfig.interceptors().forEach { builder.addInterceptor(it) }

        if (BuildConfig.DEBUG) builder.hostnameVerifier(HostnameVerifier { _, _ -> true })

        return builder.build()
    }
}

val API_TIME_OUT = if (BuildConfig.DEBUG) 60L else 20L

data class RestClientConfig(
    val converterFactory: Converter.Factory,
    val callAdapterFactory: CallAdapter.Factory,
    val readTimeOutValue: Long = API_TIME_OUT,
    val writeTimeOutValue: Long = API_TIME_OUT,
    val connectTimeOutValue: Long = API_TIME_OUT) {
    private var interceptors: MutableList<Interceptor> = mutableListOf()

    fun addInterceptor(interceptor: Interceptor) = interceptors.add(interceptor)

    fun interceptors() = interceptors.asIterable()
}
