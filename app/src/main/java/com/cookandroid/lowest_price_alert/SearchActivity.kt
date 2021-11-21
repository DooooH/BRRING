package com.cookandroid.lowest_price_alert

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.util.Assert
import com.google.gson.Gson
import com.squareup.okhttp.*
import org.json.JSONObject
import org.w3c.dom.Text
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class SearchActivity : AppCompatActivity() {
    lateinit var result_item : TextView
    lateinit var result_price : TextView
    lateinit var inputItem : String
    lateinit var item : List<Items>

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val api = API.create()
        val search_view = findViewById<LinearLayout>(R.id.search_item_view)


        result_item = findViewById<TextView>(R.id.search_name)
        result_price = findViewById<TextView>(R.id.search_price)

        inputItem = intent.getStringExtra("item").toString()

        // 검색창에서 받아온 item 이름으로 api 요청보냄.
        api.getSearchItems(inputItem).enqueue(object: Callback<GetData> {
            override fun onResponse(
                call: Call<GetData>,
                response: Response<GetData>
            ) {
                Log.d(TAG, "성공 : ${response.body()}")

                // 받아온 JSON을 data로 parsing
                item = response.body()!!.component1()

                for (i in 0 until item.size) {
                    val c = item[i]
                    val name = c.component1()
                    val price = c.component2()
                    Log.d(TAG, "성공 : ${name}")
                    Log.d(TAG, "성공 : ${price}")
                    // 받아온 요소들로 layout 동적으로 만들기
                }
            }

            override fun onFailure(call: Call<GetData>, t: Throwable) {
                Log.d(TAG, "실패 : $t")
            }
        })

//        val llOUterParams: LinearLayout.LayoutParams=LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.MATCH_PARENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//        val linearLayout = LinearLayout(this)
//        linearLayout.layoutParams = llOUterParams
//
//        val llParams = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        )
//
//        for (i in 0..3) {
//            val ll = LinearLayout(this)
//            ll.orientation  = LinearLayout.VERTICAL
//        }
    }
}