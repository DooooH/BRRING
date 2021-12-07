package com.cookandroid.lowest_price_alert

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.squareup.okhttp.*
import retrofit2.Callback
import retrofit2.Call
import retrofit2.Response
import android.widget.AdapterView

import android.widget.AdapterView.OnItemClickListener
import com.cookandroid.lowest_price_alert.board.BoardActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class SearchActivity : AppCompatActivity() {
    lateinit var result_item : TextView
    lateinit var result_price : TextView
    lateinit var inputItem : String
    lateinit var item : List<Items>
    lateinit var gridView : ExpandableHeightGridView
    lateinit var searchBtn : Button
    lateinit var backBtn : Button
    lateinit var searchText : EditText

    var imgs = arrayOf<String>()
    var names = arrayOf<String>()
    var prices = arrayOf<String>()
    var specs = arrayOf<String>()
    var nos = arrayOf<String>()

    val firebaseDatabase = FirebaseDatabase.getInstance() // 실시간 데이터 db
    val firestoredb = FirebaseFirestore.getInstance() // firestore db

    val now_user = "nhUKsEBop5beTg2c4jT4vZtYj842" // 하드코딩

    @SuppressLint("WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_item)


        //네비게이션 바
        lateinit var board_Btn: ImageButton
        lateinit var home_Btn : ImageButton
        lateinit var zzim_Btn : ImageButton
        lateinit var search_Btn : ImageButton
        lateinit var mypage_Btn: ImageButton

        board_Btn = findViewById(R.id.board_Btn)
        mypage_Btn = findViewById(R.id.mypage_Btn)
        home_Btn = findViewById(R.id.home_Btn)
        zzim_Btn = findViewById(R.id.zzim_Btn)
        search_Btn = findViewById(R.id.search_Btn)

        board_Btn.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            startActivity(intent)
        }
        mypage_Btn.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
        home_Btn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        zzim_Btn.setOnClickListener {
            val intent = Intent(this, ZzimActivity::class.java)
            startActivity(intent)
        }
        search_Btn.setOnClickListener {
            val intent = Intent(this, SearchViewActivity::class.java)
            startActivity(intent)
        }

        searchBtn = findViewById<Button>(R.id.searchBtn)
        backBtn = findViewById<Button>(R.id.backBtn)
        searchText = findViewById<EditText>(R.id.search_txt)



        searchBtn.setOnClickListener(){
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("item", searchText.text.toString())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        backBtn.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val api = API.create()

        searchText.text = intent.getStringExtra("item").toString().toEditable()
        inputItem = intent.getStringExtra("item").toString()
        gridView = findViewById(R.id.gridView)

        // 검색창에서 받아온 item 이름으로 api 요청보냄.
        api.getSearchItems(inputItem).enqueue(object: Callback<GetData> {
            override fun onResponse(
                call: Call<GetData>,
                response: Response<GetData>
            ) {
                // 받아온 JSON을 data로 parsing
                item = response.body()!!.component1()

                for (i in 0 until item.size) {
                    val c = item[i]
                    val img = c.component1()
                    val name = c.component2()
                    val no = c.component3()
                    val price = c.component4()
                    val spec = c.component5()
                    Log.d(TAG, "성공 : ${img}")
                    Log.d(TAG, "성공 : ${name}")
                    Log.d(TAG, "성공 : ${no}")
                    Log.d(TAG, "성공 : ${price}")
                    Log.d(TAG, "성공 : ${spec}")

                    imgs = append(imgs, img)
                    names = append(names, name)
                    prices = append(prices, price)
                    specs = append(specs, spec)
                    nos = append(nos, no)

                    // 받아온 요소들로 layout 동적으로 만들기
                }
                val mainAdapter = SearchAdapter(this@SearchActivity, imgs, names, prices, specs)
                gridView.adapter = mainAdapter
            }

            override fun onFailure(call: Call<GetData>, t: Throwable) {
                Log.d(TAG, "실패 : $t")
            }
        })

        gridView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val product_id : String = nos[position]
            val intent = Intent(this, ChartActivity::class.java)

            api.getProductNums(product_id).enqueue( // product list에 없을 경우 새로 업데이트 시키기
                object: Callback<String> {
                override fun onResponse(
                    call: Call<String>,
                    response: Response<String>
                ) {
                    Log.d(TAG, "get Product num 성공: $response")

                    firestoredb.collection("product_list").get().addOnSuccessListener { result ->
                        var code: String = ""
                        for (document in result) {
                            if (document["no"]?.equals(product_id) == true) {
                                code = document.id
                            }
                        }
                        firestoredb.collection("user").document(now_user).get().addOnSuccessListener { result ->
                            val search_list = result["search_list"] as ArrayList<String>
                            search_list.add(code)

                            firestoredb.collection("user").document(now_user)
                                .update("search_list", search_list)
                        }

                        intent.putExtra("product_code", code)
                        intent.putExtra("product_no", product_id)
                        startActivity(intent)
                    }
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d(TAG, "get Product num 실패 : $t")
                }
            })
        })
    }

    fun append(arr: Array<String>, element: String): Array<String> {
        val list: MutableList<String> = arr.toMutableList()
        list.add(element)
        return list.toTypedArray()
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}
