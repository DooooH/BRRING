package com.cookandroid.lowest_price_alert

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cookandroid.lowest_price_alert.board.BoardActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    // variables for product test
    lateinit var boardBtn: Button
    lateinit var loginBtn: ImageButton
    lateinit var searchBtn: Button
    lateinit var searchItem: EditText
    lateinit var mypageBtn: Button
    lateinit var recom_text: TextView
    lateinit var alarm_Btn: ImageButton

    // declare nullable object for Firebase auth
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardBtn = findViewById(R.id.boardBtn)
        loginBtn = findViewById(R.id.login_Btn)
        searchBtn = findViewById(R.id.searchBtn)
        searchItem = findViewById(R.id.search_txt)
        mypageBtn = findViewById(R.id.mypage_btn)
        recom_text = findViewById(R.id.recom_text)
        alarm_Btn = findViewById(R.id.alarm_btn)

        boardBtn.setOnClickListener {
            val intent = Intent(this, BoardActivity::class.java)
            startActivity(intent)
        }
        loginBtn.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } // loginBtn onclick listener
        searchBtn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("item", searchItem.text.toString())
            startActivity(intent)
        }
        mypageBtn.setOnClickListener {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
        alarm_Btn.setOnClickListener {
            val intent = Intent(this, RecentAlarmActivity::class.java)
            startActivity(intent)
        }

        // keyboard option action
        searchItem.setOnEditorActionListener{ textView, action, event ->
            var handled = false

            if (action == EditorInfo.IME_ACTION_SEARCH) {
                // go to search intent
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra("item", searchItem.text.toString())
                startActivity(intent)
            }

            handled
        }
        // keyboard option action done

        val firestoredb = FirebaseFirestore.getInstance()
        var recomm_product_List = arrayListOf<recomm_Product>()
        firestoredb.collection("recommendation_list").get().addOnSuccessListener { result ->

            for (document in result) {
                val item = recomm_Product(
                    document["name"] as String,
                    document["image_url"] as String,
                    "sub1",
                    "sub2",
                    document["no"] as String,
                    document.id as String,
                    document["price"].toString()
                )

                recomm_product_List.add(item)

                var recom_list_view = findViewById<RecyclerView>(R.id.recom_recycler_view)

                val recomAdapter = RecomRvAdapter(this, recomm_product_List) { recommProduct ->
                    val number = recommProduct.no
                    var code = ""
                    firestoredb.collection("product_list").get().addOnSuccessListener { result ->

                        for (document in result) {
                            if (document["no"]?.equals(number) == true) {
                                code = document.id
                            }
                        }
                        val intent = Intent(this, ChartActivity::class.java)
                        intent.putExtra("product_code", code.toString())
                        intent.putExtra("product_no", number.toString())
                        startActivity(intent)
                    }
                }
                recom_list_view.adapter = recomAdapter
            }

        }

        var search_product_List = arrayListOf<search_Product>()
        firestoredb.collection("product_list").get().addOnSuccessListener { product ->
            firestoredb.collection("user").document("nhUKsEBop5beTg2c4jT4vZtYj842").get()
                .addOnSuccessListener { result ->
                    val search_list = result["search_list"] as ArrayList<String>
                    if (search_list.size == 0) {



                    } else {

                        for (i: Int in 0..search_list.size - 1) {
                            search_list[i] = search_list[i].replace(" ", "")
                        }
                        for (i: Int in 0..search_list.size - 1) {
                            for (document in product) {
                                val item = search_Product(
                                    document["name"] as String,
                                    document["image_url"] as String,
                                    "sub1",
                                    "sub2",
                                    document["no"] as String,
                                    document.id as String,
                                    document["price"].toString()
                                )

                                if (document.id.equals(search_list[i])) {
                                    search_product_List.add(item)
                                }
                            }
                        }
                        var search_list_view = findViewById<RecyclerView>(R.id.search_recycler_view)

                        val searchAdapter =
                            SearchRvAdapter(this, search_product_List) { searchProduct ->
                                val number = searchProduct.no
                                var code = ""
                                firestoredb.collection("product_list").get()
                                    .addOnSuccessListener { result ->

                                        for (document in result) {
                                            if (document["no"]?.equals(number) == true) {
                                                code = document.id
                                            }
                                        }
                                        val intent = Intent(this, ChartActivity::class.java)
                                        intent.putExtra("product_code", code.toString())
                                        intent.putExtra("product_no", number.toString())
                                        startActivity(intent)
                                    }
                            }
                        search_list_view.adapter = searchAdapter
                    }

                }
        }


    } // onCreate

    class recomm_Product(
        val name: String,
        val photo: String,
        val sub1: String,
        val sub2: String,
        val no: String,
        val product_code: String,
        val price: String
    )

    class search_Product(
        val name: String,
        val photo: String,
        val sub1: String,
        val sub2: String,
        val no: String,
        val product_code: String,
        val price: String
    )

    class RecomRvAdapter(
        val context: Context,
        val List: ArrayList<recomm_Product>,
        val itemClick: (recomm_Product) -> Unit
    ) :

        RecyclerView.Adapter<RecomRvAdapter.Holder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view =
                LayoutInflater.from(context).inflate(R.layout.recomm_list_item, parent, false)
            return Holder(view, itemClick)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder?.bind(List[position], context)
        }

        override fun getItemCount(): Int {
            return List.size
        }

        inner class Holder(itemView: View?, itemClick: (recomm_Product) -> Unit) :
            RecyclerView.ViewHolder(itemView!!) {

            val Photo = itemView?.findViewById<ImageView>(R.id.recom_item_Img)
            val name = itemView?.findViewById<TextView>(R.id.recom_item_name)
            val sub1 = itemView?.findViewById<TextView>(R.id.recom_sub1_text)
            val sub2 = itemView?.findViewById<TextView>(R.id.recom_sub2_text)


            fun bind(product: recomm_Product, context: Context) {
                var url = "http:" + product.photo
                if (Photo != null) {
                    Glide.with(itemView).load(url)
                        .into(Photo)
                } //이미지 url로 사진 불러오기


                name?.text = product.name
                sub1?.text = product.price + "원"
                //sub2?.text = product.sub2
                sub2?.text = "제품 확인하기 >"

                itemView.setOnClickListener {
                    itemClick(product)
                }
            }
        }


    }

    class SearchRvAdapter(
        val context: Context,
        val List: ArrayList<search_Product>,
        val itemClick: (search_Product) -> Unit
    ) :

        RecyclerView.Adapter<SearchRvAdapter.Holder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view =
                LayoutInflater.from(context).inflate(R.layout.recomm_list_item, parent, false)
            return Holder(view, itemClick)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder?.bind(List[position], context)
        }

        override fun getItemCount(): Int {
            return List.size
        }

        inner class Holder(itemView: View?, itemClick: (search_Product) -> Unit) :
            RecyclerView.ViewHolder(itemView!!) {

            val Photo = itemView?.findViewById<ImageView>(R.id.recom_item_Img)
            val name = itemView?.findViewById<TextView>(R.id.recom_item_name)
            val sub1 = itemView?.findViewById<TextView>(R.id.recom_sub1_text)
            val sub2 = itemView?.findViewById<TextView>(R.id.recom_sub2_text)


            fun bind(product: search_Product, context: Context) {
                var url = "http:" + product.photo
                if (Photo != null) {
                    Glide.with(itemView).load(url)
                        .into(Photo)
                } //이미지 url로 사진 불러오기


                name?.text = product.name
                sub1?.text = product.price + "원"
                //sub2?.text = product.sub2
                sub2?.text = "제품 확인하기 >"

                itemView.setOnClickListener {
                    itemClick(product)
                }
            }
        }


    }


}
