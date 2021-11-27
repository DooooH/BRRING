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
import org.w3c.dom.Text
import java.sql.Timestamp

class PostContentActivity : AppCompatActivity() {
    // variables for board

    // firestore
    val firestoredb = FirebaseFirestore.getInstance() // firestore db
    lateinit var boardId : String
    lateinit var postId : String

    // declare nullable object for Firebase auth
    private var auth: FirebaseAuth? = null

    // comments
    var commentList = arrayListOf<Comment>()
    lateinit var commentListAdapter : BaseAdapter

    // view components
    lateinit var productImageIv : ImageView
    lateinit var productNameTv : TextView
    lateinit var postTitleTv : TextView
    lateinit var postContentTv : TextView
    lateinit var commentLv : ListView
    lateinit var commentContentEt : EditText
    lateinit var commentSubmitBtn : Button

    //onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_post_content_activity)

        //auth 객체 초기화, 인스턴스 get
        auth = FirebaseAuth.getInstance()
        val currentUser = auth?.currentUser

        // connect view components to variables
        productImageIv = findViewById(R.id.productImageIv)
        productNameTv = findViewById(R.id.productNameTv)
        postTitleTv = findViewById(R.id.postTitleTv)
        postContentTv = findViewById(R.id.postContentTv)
        commentContentEt = findViewById(R.id.commentContentEt)
        commentSubmitBtn = findViewById(R.id.commentSubmitBtn)

        // connect list view
        commentLv = findViewById(R.id.commentLv)

        // get postId
        boardId = intent.getStringExtra("boardId").toString()
        postId = intent.getStringExtra("postId").toString()
        Toast.makeText(this, "postId : " + postId, Toast.LENGTH_SHORT).show()

        // get post content from firestore
        firestoredb.collection("location_board").document(boardId)
            .collection("post").document(postId)
            .get()
            .addOnSuccessListener { document ->
                if(document != null){
                    Toast.makeText(this, "${document.id} : ${document.data}", Toast.LENGTH_LONG).show()
                    //var postId = document.id
                    productImageIv.setImageResource(R.drawable.ipad) // have to change to url
                    productNameTv.text = document["product_name"].toString()
                    postTitleTv.text = document["title"].toString()
                    postContentTv.text = document["content"].toString()
                }
                else{
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: ", Toast.LENGTH_SHORT).show()
            }

        // get comments from firestore
        firestoredb.collection("location_board").document(boardId)
            .collection("post").document(postId)
            .collection("comment")
            .get()
            .addOnSuccessListener { documents ->
                if(documents != null){
                    for(document in documents){
                        Toast.makeText(this, "${document.id} : ${document.data}", Toast.LENGTH_LONG).show()
                        var commentId = document.id
                        var uid = document["uid"].toString()
                        var username = document["username"].toString()
                        var commentContent = document["comment_content"].toString()
                        var lb = Comment(commentId, uid, username, commentContent)
                        commentList.add(lb)
                    }

                    // connect location board list and list view via adapter
                    commentListAdapter = CommentListAdapter(this, commentList)
                    commentLv.adapter = commentListAdapter

                }
                else{
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: ", Toast.LENGTH_SHORT).show()
            }

        // add on click listener to button
        commentSubmitBtn.setOnClickListener {
            if(currentUser?.uid.toString() == "null"){
                Toast.makeText(this, "로그인 후 이용해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if(commentContentEt.text.toString() == ""){
                Toast.makeText(this, "댓글 내용을 작성해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                // insert comment
                // get elements
                val commentContent = commentContentEt.text.toString()
                var username = ""
                val uid = auth!!.uid.toString()
                firestoredb.collection("user").document(uid)
                    .get()
                    .addOnSuccessListener { document ->
                        if(document != null){
                                username = document["username"].toString()
                        }
                        else{
                            username = "익명이"
                        }
                        // set post document
                        val comment = hashMapOf(
                            "comment_content" to commentContent,
                            "username" to username,
                            "uid" to uid
                        )

                        firestoredb
                            .collection("location_board").document(boardId)
                            .collection("post").document(postId)
                            .collection("comment")
                            .add(comment)
                            .addOnSuccessListener {Toast.makeText(this,"댓글 작성 완료",Toast.LENGTH_SHORT).show()}
                            .addOnFailureListener {Toast.makeText(this,"댓글 작성 성공의 어머니",Toast.LENGTH_SHORT).show()}
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error getting documents: ", Toast.LENGTH_SHORT).show()
                    }


            }
        }

        /*
        // connect location board list and list view via adapter
        commentListAdapter = CommentListAdapter(this, commentList)
        commentLv.adapter = commentListAdapter
         */

    } // onCreate

}