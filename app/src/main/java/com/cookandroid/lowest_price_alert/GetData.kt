package com.cookandroid.lowest_price_alert

import android.media.Image
import android.provider.BaseColumns

// API response GetData({"contents":{"name":, "price":}})
data class GetData(
    val contents: List<Items>
)

data class Items(
    val img: String = "",
    val name: String = "",
    val no: String = "",
    val price: String = "",
    val spec : String = ""
)