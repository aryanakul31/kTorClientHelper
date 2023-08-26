package com.nakul.ktorexample.api_handling

import com.nakul.ktorexample.ResponseModel
import com.nakul.ktorexample.api_helper.ApiUtil
import io.ktor.client.request.get
import io.ktor.client.request.parameter

object ApiInterface {
    suspend fun getUserData(): ResponseModel {
        return ApiUtil.getHttpClient().use {
            it.get("${ApiUrls.BASE_URL}${ApiUrls.NEWS}") {
                parameter("q", "Android")
                parameter("sortBy", "publishedAt")
                parameter("apiKey", "3e36f11979ac41178fe55d05b52516c9")
            }
        }
    }
}
