package com.cookandroid.lowest_price_alert.board

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.cookandroid.lowest_price_alert.LoginActivity
import com.cookandroid.lowest_price_alert.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class PostActivity : AppCompatActivity() {
    // variables for board

    lateinit var postListView : ListView

    // firebase
    val firestoredb = FirebaseFirestore.getInstance() // firestore db
    val firebaseDatabase = FirebaseDatabase.getInstance() // 실시간 데이터 db
    val storage = Firebase.storage // firebase storage

    lateinit var boardId : String

    // declare nullable object for Firebase auth
    private var auth: FirebaseAuth? = null

    // posts
    var postList = arrayListOf<Post>()
    lateinit var postListAdapter : BaseAdapter

    // view components
    lateinit var writeBtn : FloatingActionButton

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
        postListView = findViewById(R.id.postListView)

        // get boardId
        boardId = intent.getStringExtra("boardId").toString()

        // get posts from firestore
        getPosts()

        // add on click listener to button
        writeBtn.setOnClickListener {
            if(currentUser?.uid.toString() == "null"){
                Toast.makeText(this, "로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                val intent = Intent(this, WritePostActivity::class.java)
                intent.putExtra("boardId", boardId)
                startActivityForResult(intent, 0)
            }
        }

    } // onCreate

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            // 글쓰기 성공 success to write post
            // ui update
            getPosts()
        }
        else{
            // 글쓰기 실패 fail to write post
        }
    }

    fun getPosts(){
        firestoredb.collection("location_board").document(boardId).collection("post")
            .get()
            .addOnSuccessListener { documents ->
                if(documents != null){
                    for(document in documents){
                        //Toast.makeText(this, "${document.id} : ${document.data}", Toast.LENGTH_LONG).show()
                        var postId = document.id
                        var product_image_url = document["product_image_url"].toString()
                        var product_id = document["product_id"].toString()
                        var product_name = document["product_name"].toString()
                        var product_price = document["product_price"].toString()
                        var title = document["title"].toString()
                        var writer_id = document["writer_id"].toString()
                        var writer_username = document["writer_username"].toString()
                        var writer_profile_image = document["writer_user_image"].toString()

                        // get realtime price from realtime database
                        val path = "product_list/$product_id" // 실시간 db에 접근하기 위한 경로.
                        val myRef: DatabaseReference = firebaseDatabase.getReference(path) // 실시간 db에 접근

                        myRef.addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val snapshot_info = snapshot.child("price")

                                for (item in snapshot_info.children) {
                                    product_price = item.value.toString() + "원"
                                    //Toast.makeText(this@PostActivity,"$product_name 가격정보 : $product_price", Toast.LENGTH_SHORT).show()
                                }

                                // get comment count from firestore
                                var commentCount = ""
                                firestoredb
                                    .collection("location_board").document(boardId)
                                    .collection("post").document(postId)
                                    .collection("comment")
                                    .get()
                                    .addOnSuccessListener { comments ->
                                        commentCount = comments.size().toString()

                                        var lb = Post(product_image_url, title, product_name, "한 달 중 최저가", product_price, commentCount, boardId, postId)
                                        postList.add(lb)

                                        // connect location board list and list view via adapter
                                        postListAdapter = PostListAdapter(this@PostActivity, postList)
                                        postListView.adapter = postListAdapter
                                    }


                            }

                            override fun onCancelled(error: DatabaseError) { // 실시간 db 접근을 실패하면
                                println("Failed to read value.")
                            }
                        })
                    }


                }
                else{
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: ", Toast.LENGTH_SHORT).show()
            }
    }

}