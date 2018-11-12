package com.beer.depository.model

import org.json.JSONException
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable


// Model for beer can. Implement serializable interface
class Can : Serializable {
    //Fields to be displayed for each can
    private var imgurId: String? = null
    var title: String? = null
    var brand: String? = null
    var quantity: String? = null
    var ownership: String? = null
    var description: String? = null
    var info: String? = null
    var origin: String? = null
    var bought: String? = null
    var canColor: String? = null
    var album: String? = null

    // URL towards picture of the can
    val canPicture: String
        get() = "https://i.imgur.com/$imgurId.jpg"


    //function that converts JSON object to Can
    private fun fromJson(jsonObject: JSONObject): Can? {
        val can = Can()
        try {
            // Deserialize json into object fields
            can.imgurId =  if (jsonObject.has("id")) jsonObject.getString("id") else ""
            can.title =  if (jsonObject.has("title")) jsonObject.getString("title") else "NO TITLE"
            can.info =  if (jsonObject.has("description")) jsonObject.getString("description") else "NO INFORMATION"

            //We split the description attribute into multiple other fields, using ; as delimiter, and place them into a HashMap
            val hMap = hashMapOf<String, String>()
            can.info?.split(";")?.forEach {

                val textArray = it.split(":")
                if (textArray.size == 2) {

                    val (left, right) = it.split(":")
                    hMap[left.trim()] = right
                } else {
                }
            }

            //Hide information about the image album
            val publicInfo = can.info?.split("Album:")?.get(0)
            can.info = publicInfo


            //From the newly created HashMap, we place information into the fields of Can object
            if (hMap != null) {
                can.brand = if (hMap.containsKey("Brand")) hMap["Brand"] else "Not Specified"
                can.origin = if (hMap.containsKey("Country of Origin")) hMap["Country of Origin"] else "Unknown"
                can.bought = if (hMap.containsKey("Bought in")) hMap["Bought in"] else "Unknown"
                can.quantity = if (hMap.containsKey("Quantity")) hMap["Quantity"] else "Not Specified"
                can.ownership = if (hMap.containsKey("Ownership")) hMap["Ownership"] else "Both"
                can.canColor = if (hMap.containsKey("Color")) hMap["Color"] else "Not Specified"
                can.description = if (hMap.containsKey("Description")) hMap["Description"] else "Not Specified"
                can.album = if (hMap.containsKey("Album")) hMap["Album"] else "Not Existing"
            }

        } catch (e: JSONException) {
            //Invalid JSON format, show appropriate error.
            e.printStackTrace()
            return null
        }

        // Return new object
        return can
    }



    //Function that converts a JSON array of can image information into multiple JSONs containing information on only one can
    fun fromJson(jsonArray: JSONArray): ArrayList<Can> {
        val cans = ArrayList<Can>(jsonArray.length())
        // Process each result in json array, decode and convert to business
        // object
        for (i in 0 until jsonArray.length()) {
            var canJson: JSONObject? = null
            try {
                canJson = jsonArray.getJSONObject(i)
            } catch (e: Exception) {
                e.printStackTrace()
                continue
            }
            val can = this.fromJson(canJson)
            if (can != null) {
                cans.add(can)
            }
        }
        return cans
    }

}