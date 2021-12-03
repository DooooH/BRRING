package com.cookandroid.lowest_price_alert

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity

class SearchViewActivity : AppCompatActivity() {
    lateinit var searchBtn : Button
    lateinit var backBtn : Button
    lateinit var searchText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

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
    }
}