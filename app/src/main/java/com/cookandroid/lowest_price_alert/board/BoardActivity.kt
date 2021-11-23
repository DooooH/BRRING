package com.cookandroid.lowest_price_alert.board

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.cookandroid.lowest_price_alert.R
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