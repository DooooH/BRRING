package com.cookandroid.lowest_price_alert.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.cookandroid.lowest_price_alert.*
import com.google.firebase.firestore.FirebaseFirestore

class BoardActivity : AppCompatActivity() {
    // variables for board

    // firestore
    val firestoredb = FirebaseFirestore.getInstance() // firestore db

    var BoardList = arrayListOf<Board>()
    lateinit var BoardListAdapter : BaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_activity)

        // connect list view
        var boardListView = findViewById<ListView>(R.id.boardListView)



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

        var back_btn = findViewById<ImageButton>(R.id.back_button) // 뒤로가기
        back_btn.setOnClickListener{
            onBackPressed()
        }




        // get location boards from firestore
        firestoredb.collection("location_board")
            .get()
            .addOnSuccessListener { results ->
                if(results != null){
                    for(document in results){
                        var boardId = document.id
                        var boardTitle = document["title"].toString()
                        var lb = Board(boardTitle, boardId)
                        BoardList.add(lb)
                    }
                    // connect location board list and list view via adapter
                    BoardListAdapter = BoardListAdapter(this, BoardList)
                    boardListView.adapter = BoardListAdapter
                }
                else{
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: ", Toast.LENGTH_SHORT).show()
            }

    } // onCreate

}
