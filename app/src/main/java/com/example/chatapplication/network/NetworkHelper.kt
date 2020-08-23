package com.example.chatapplication.network

import com.example.chatapplication.inerfaces.AuthAPI
import com.example.chatapplication.inerfaces.NotificationAPI
import com.example.chatapplication.inerfaces.UserAPI
import com.example.chatapplication.util.Constants.Companion.BASE_URL
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


@Singleton
class NetworkHelper {

    private lateinit var retrofit: Retrofit

    @Inject
    constructor() {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(httpClient)
            .build()
    }


    private val httpClient = OkHttpClient.Builder() //here we can add Interceptor for dynamical adding headers
            .addNetworkInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val request: Request = chain.request().newBuilder()
                        //.addHeader("Content-Encoding", "gzip")
                        .build()
                    return chain.proceed(request)
                }
            }) //here we adding Interceptor for full level logging
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(3, TimeUnit.SECONDS)
            //.sslSocketFactory(getSslContext().getSocketFactory(), MyManager())
            //.hostnameVerifier(HostnameVerifier { hostname, session -> true })
            .build()

    fun getSslContext():SSLContext{
        val trustAllCerts: Array<TrustManager> = arrayOf(MyManager())
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        return sslContext
    }

    class MyManager : X509TrustManager {

        override fun checkServerTrusted(
            p0: Array<out java.security.cert.X509Certificate>?,
            p1: String?) {
            //allow all
        }

        override fun checkClientTrusted(
            p0: Array<out java.security.cert.X509Certificate>?,
            p1: String?) {
            //allow all
        }

        override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
            return arrayOf()
        }
    }

    val authApi by lazy {
        retrofit.create(AuthAPI::class.java)
    }

    val userAPI by lazy {
        retrofit.create(UserAPI::class.java)
    }

    val updateUserProfileAPI by lazy {
        retrofit.create(UserAPI::class.java)
    }

    val notificationAPI by lazy {
        retrofit.create(NotificationAPI::class.java)
    }
}
