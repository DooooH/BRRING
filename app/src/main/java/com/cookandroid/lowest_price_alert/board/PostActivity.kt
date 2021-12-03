package com.cookandroid.lowest_price_alert.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat
import com.cookandroid.lowest_price_alert.LoginActivity
import com.cookandroid.lowest_price_alert.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostActivity : AppCompatActivity() {
    // variables for board

    // firestore
    val firestoredb = FirebaseFirestore.getInstance() // firestore db
    lateinit var boardId : String

    // declare nullable object for Firebase auth
    private var auth: FirebaseAuth? = null

    // posts
    var postList = arrayListOf<Post>()
    lateinit var postListAdapter : BaseAdapter

    // view components
    lateinit var writeBtn : Button

    //onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_post_activity)

        //auth 객체 초기화, 인스턴스 get
        auth = FirebaseAuth.getInstance()
        val currentUser = auth?.currentUser

        // connect view components to variables
        writeBtn = findViewById(R.id.writeBtn)

        // connect list view
        var postListView = findViewById<ListView>(R.id.postListView)

        // get boardId
        boardId = intent.getStringExtra("boardId").toString()

        // get posts from firestore
        firestoredb.collection("location_board").document(boardId).collection("post")
            .get()
            .addOnSuccessListener { documents ->
                if(documents != null){
                    for(document in documents){
                        //Toast.makeText(this, "${document.id} : ${document.data}", Toast.LENGTH_LONG).show()
                        var postId = document.id
                        //var product_image_url = document["product_image_url"].toString()
                        var product_image_url = "ipad"
                        var product_id = document["product_id"].toString()
                        var product_name = document["product_name"].toString()
                        var product_price = document["product_price"].toString()
                        var title = document["title"].toString()
                        var writer_id = document["writer_id"].toString()
                        var lb = Post(product_image_url, title, product_name, "한 달 중 최저가", product_price, "", boardId, postId)
                        postList.add(lb)
                    }

                    // connect location board list and list view via adapter
                    postListAdapter = PostListAdapter(this, postList)
                    postListView.adapter = postListAdapter

                }
                else{
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: ", Toast.LENGTH_SHORT).show()
            }

        // add on click listener to button
        writeBtn.setOnClickListener {
            if(currentUser?.uid.toString() == "null"){
                Toast.makeText(this, "로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(this, WritePostActivity::class.java)
                intent.putExtra("boardId", boardId)
                startActivity(intent)
            }
        }

        // connect location board list and list view via adapter
        postListAdapter = PostListAdapter(this, postList)
        postListView.adapter = postListAdapter

    } // onCreate

}