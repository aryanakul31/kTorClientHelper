package com.nakul.ktorexample

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable

data class ResponseModel(
    @SerialName("articles")
    var articles: List<Article?>?,
    @SerialName("status")
    var status: String?,
    @SerialName("totalResults")
    var totalResults: Int?
) {
    @Serializable
    data class Article(
        @SerialName("author")
        var author: String?,
        @SerialName("content")
        var content: String?,
        @SerialName("description")
        var description: String?,
        @SerialName("publishedAt")
        var publishedAt: String?,
        @SerialName("source")
        var source: Source?,
        @SerialName("title")
        var title: String?,
        @SerialName("url")
        var url: String?,
        @SerialName("urlToImage")
        var urlToImage: String?
    ) {
        @Serializable

        data class Source(
            @SerialName("id")
            var id: String?,
            @SerialName("name")
            var name: String?
        )
    }
}