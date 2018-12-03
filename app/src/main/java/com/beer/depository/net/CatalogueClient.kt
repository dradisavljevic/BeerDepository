package com.beer.depository.net

import com.loopj.android.http.JsonHttpResponseHandler
import com.loopj.android.http.AsyncHttpClient
import java.io.*


class CatalogueClient {
    private val client: AsyncHttpClient

    init {
        this.client = AsyncHttpClient()
    }

    //append album hash to base URL
    private fun getApiUrl(relativeUrl: String): String {
        return API_BASE_URL + relativeUrl
    }

    // Method for accessing the imgur API in order to get all iamges from the album
    fun getCans(handler: JsonHttpResponseHandler, clientId : String, albumHash: String) {
        try {
            val url = getApiUrl(albumHash)
            //Authorize in order to use API
            client.addHeader("Authorization", "Client-ID $clientId")
            client.get(url , handler)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

    }

    fun getImages(handler: JsonHttpResponseHandler, clientId : String, albumHash: String) {
        try {
            val imagesHash = albumHash + "/images"
            val url = getApiUrl(imagesHash)
            //Authorize in order to use API
            client.addHeader("Authorization", "Client-ID $clientId")
            client.get(url , handler)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

    }

    //Base URL for shooting imgur API and fetching images of an album
    companion object {
        private const val API_BASE_URL = "https://api.imgur.com/3/album/"
    }
}