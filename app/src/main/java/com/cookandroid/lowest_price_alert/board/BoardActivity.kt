package com.cookandroid.lowest_price_alert.board

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import com.cookandroid.lowest_price_alert.LoginActivity
import com.cookandroid.lowest_price_alert.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class BoardActivity : AppCompatActivity() {
    // variables for board

    // firestore
    val firestoredb = FirebaseFirestore.getInstance() // firestore db

    var locationBoardList = arrayListOf<LocationBoard>()

    //var locationBoardList = arrayListOf<LocationBoard>()

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
                        var lb = LocationBoard("${document.data}", "${document.id}")
                        locationBoardList.add(lb)
                        Toast.makeText(this, "${document.id} : ${document.data}", Toast.LENGTH_SHORT).show()
                    }
                    // connect location board list and list view via adapter
                    val locationBoardListAdapter = LocationBoardListAdapter(this, locationBoardList)
                    boardListView.adapter = locationBoardListAdapter
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