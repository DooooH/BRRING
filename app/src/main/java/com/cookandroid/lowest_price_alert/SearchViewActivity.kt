package com.cookandroid.lowest_price_alert

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class SearchViewActivity : AppCompatActivity() {
    lateinit var searchBtn : Button
    lateinit var backBtn : Button
    lateinit var searchText : EditText

    val firebaseDatabase = FirebaseDatabase.getInstance() // 실시간 데이터 db
    val firestoredb = FirebaseFirestore.getInstance() // firestore db

    val now_user = "nhUKsEBop5beTg2c4jT4vZtYj842" // 하드코딩

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val TAG:String = "SearchViewActivity : "

        searchBtn = findViewById<Button>(R.id.searchBtn)
        backBtn = findViewById<Button>(R.id.backBtn)


        searchBtn.setOnClickListener(){
            searchText = findViewById<EditText>(R.id.search_txt)

            firestoredb.collection("user").document(now_user).get().addOnSuccessListener { result ->
                val search_history = result["search_history"] as ArrayList<String>
                search_history.add(searchText.text.toString())
                var res=searchText.text.toString()

                firestoredb.collection("user").document(now_user)
                    .update("search_history", search_history)
            }

            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra("item", searchText.text.toString())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        backBtn.setOnClickListener(){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}