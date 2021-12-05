package com.cookandroid.lowest_price_alert

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text

class SearchViewActivity : AppCompatActivity() {
    lateinit var searchBtn: Button
    lateinit var backBtn: Button
    lateinit var searchText: EditText

    val firebaseDatabase = FirebaseDatabase.getInstance() // 실시간 데이터 db
    val firestoredb = FirebaseFirestore.getInstance() // firestore db

    val now_user = "nhUKsEBop5beTg2c4jT4vZtYj842" // 하드코딩

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val TAG: String = "SearchViewActivity : "

        searchBtn = findViewById<Button>(R.id.searchBtn)
        backBtn = findViewById<Button>(R.id.backBtn)


        searchBtn.setOnClickListener() {
            searchText = findViewById<EditText>(R.id.search_txt)

            firestoredb.collection("user").document(now_user).get().addOnSuccessListener { result ->
                val search_history = result["search_history"] as ArrayList<String>
                search_history.add(searchText.text.toString())
                var res = searchText.text.toString()

                firestoredb.collection("user").document(now_user)
                    .update("search_history", search_history)
            }

            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("item", searchText.text.toString())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        backBtn.setOnClickListener() {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        var search_List = arrayListOf<search_item>()
        firestoredb.collection("user").document(now_user).get()
            .addOnSuccessListener { result ->
                val search_list = result["search_history"] as ArrayList<String>
                if (search_list.size == 0) {


                } else {

                    for (i: Int in 0..search_list.size - 1) {
                        search_list[i] = search_list[i].replace(" ", "")
                    }
                    for (i: Int in 0..search_list.size - 1) {
                        val item = search_item(
                            search_list[i] as String,
                        )

                        search_List.add(item)

                    }
                    var search_list_view = findViewById<RecyclerView>(R.id.recent_search_recycler_view)

                    val searchAdapter =
                        SearchRvAdapter(this, search_List) { searchProduct ->
                            
                            val intent = Intent(this, SearchActivity::class.java)
                            intent.putExtra("item", searchProduct.name)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    search_list_view.adapter = searchAdapter
                }

            }
    }

    class search_item(
        val name: String
    )

    class SearchRvAdapter(
        val context: Context,
        val List: ArrayList<search_item>,
        val itemClick: (search_item) -> Unit
    ) :

        RecyclerView.Adapter<SearchRvAdapter.Holder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            val view =
                LayoutInflater.from(context).inflate(R.layout.recent_search_item, parent, false)
            return Holder(view, itemClick)
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder?.bind(List[position], context)
        }

        override fun getItemCount(): Int {
            return List.size
        }

        inner class Holder(itemView: View?, itemClick: (search_item) -> Unit) :
            RecyclerView.ViewHolder(itemView!!) {

            val textview = itemView?.findViewById<TextView>(R.id.recent_search_item_text)


            fun bind(item: search_item, context: Context) {

                textview!!.text = "    " +  item.name + "    "

                itemView.setOnClickListener {
                    itemClick(item)
                }
            }
        }


    }
}
