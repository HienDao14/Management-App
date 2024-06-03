package com.example.managementapp.util

import android.content.SharedPreferences
import android.util.Log
import com.example.managementapp.auth.data.remote.api.TokenApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class MyInterceptor @Inject constructor(
    private val prefs: SharedPreferences,
//    private val tokenApi: TokenApi
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = prefs.getString("accessToken", null)
        if (!accessToken.isNullOrEmpty() ) {
            val request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
            return chain.proceed(request)
//             if (response.code == 401) {
//                response.close()
//                return refreshToken(
//                    chain, request, accessToken, refreshToken
//                )
//            } else {
//                return response
//            }
        }
        return chain.proceed(chain.request())
    }
//
//    private fun refreshToken(
//        chain: Interceptor.Chain,
//        request: Request,
//        accessToken: String,
//        refreshToken: String
//    ): Response {
//        val headerMap = mutableMapOf<String, String>()
//        headerMap["Authorization"] = accessToken
//        val response = tokenApi.refreshAccessToken(
//            refreshToken,
//            headerMap
//        ).execute()
//        return if (response.isSuccessful) {
//            Log.d("test", "refresh token success: ${response.body()?.accessToken}")
//            val token = response.body()?.accessToken
//            val newRequest = chain.request()
//                .newBuilder()
//                .addHeader("Authorization", "Bearer $token")
//                .build()
//            chain.proceed(newRequest)
//        } else {
//             chain.proceed(request)
//        }
//    }
}