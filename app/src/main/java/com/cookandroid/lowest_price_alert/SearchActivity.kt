package com.cookandroid.lowest_price_alert

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.okhttp.*
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    lateinit var result_item : TextView
    lateinit var result_price : TextView
    lateinit var inputItem : String
    lateinit var item : List<Items>
    lateinit var gridView : GridView
    lateinit var searchBtn : Button
    lateinit var backBtn : Button
    lateinit var searchText : EditText

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_item)

        searchBtn = findViewById<Button>(R.id.searchBtn)
        backBtn = findViewById<Button>(R.id.backBtn)


        searchBtn.setOnClickListener(){
            searchText = findViewById<EditText>(R.id.search_txt)

            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("item", searchText.text.toString())
            startActivity(intent)
        }

        backBtn.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val api = API.create()

        inputItem = intent.getStringExtra("item").toString()
        gridView = findViewById(R.id.gridView)

        // 검색창에서 받아온 item 이름으로 api 요청보냄.
        api.getSearchItems(inputItem).enqueue(object: Callback<GetData> {
            override fun onResponse(
                call: Call<GetData>,
                response: Response<GetData>
            ) {
                var imgs = arrayOf<String>()
                var names = arrayOf<String>()
                var prices = arrayOf<String>()
                var specs = arrayOf<String>()

                // 받아온 JSON을 data로 parsing
                item = response.body()!!.component1()

                for (i in 0 until item.size) {
                    val c = item[i]
                    val img = c.component1()
                    val name = c.component2()
                    val price = c.component3()
                    val spec = c.component4()
                    Log.d(TAG, "성공 : ${img}")
                    Log.d(TAG, "성공 : ${name}")
                    Log.d(TAG, "성공 : ${price}")
                    Log.d(TAG, "성공 : ${spec}")

                    imgs = append(imgs, img)
                    names = append(names, name)
                    prices = append(prices, price)
                    specs = append(specs, spec)

                    // 받아온 요소들로 layout 동적으로 만들기
                }
                val mainAdapter = SearchAdapter(this@SearchActivity, imgs, names, prices, specs)
                gridView.adapter = mainAdapter
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

    fun append(arr: Array<String>, element: String): Array<String> {
        val list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }
}