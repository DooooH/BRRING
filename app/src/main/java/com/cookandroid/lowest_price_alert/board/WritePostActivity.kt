package com.cookandroid.lowest_price_alert.board

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.cookandroid.lowest_price_alert.LoginActivity
import com.cookandroid.lowest_price_alert.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.sql.Date
import java.sql.Timestamp

class WritePostActivity : AppCompatActivity() {
    // view components
    lateinit var titleEt : EditText
    lateinit var contentEt : EditText
    lateinit var selectProductBtn : Button
    lateinit var writeBtn : Button

    // firestore
    val firestoredb = FirebaseFirestore.getInstance() // firestore db

    // declare nullable object for Firebase auth
    private var auth: FirebaseAuth? = null

    // board info
    lateinit var boardId : String

    //onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.write_post_activity)

        // connect view components and variables
        titleEt = findViewById(R.id.titleEt)
        contentEt = findViewById(R.id.contentEt)
        selectProductBtn = findViewById(R.id.selectProductBtn)
        writeBtn = findViewById(R.id.writeBtn)

        // get boardId
        boardId = intent.getStringExtra("boardId").toString()

        //auth 객체 초기화, 인스턴스 get
        auth = FirebaseAuth.getInstance()
        val currentUser = auth?.currentUser

        // write function
        writeBtn.setOnClickListener {
            // get today time
            val today = java.util.Date()
            // get elements
            val productTitle = titleEt.text.toString()
            val productContent = contentEt.text.toString()

            // set post document
            val post = hashMapOf(
                "created_at" to Timestamp(today.time),
                "product_id" to "15253217",
                "product_image_url" to "img.danawa.com/prod_img/500000/217/253/img/15253217_1.jpg?shrink=330:330&_v=20211026140920",
                "product_name" to "APPLE 아이패드 미니 6세대 Wi-Fi 64GB (정품)",
                "product_price" to "160000",
                "title" to productTitle,
                "content" to productContent,
                "updated_at" to Timestamp(today.time),
                "writer_id" to currentUser?.uid.toString()
            )

            firestoredb.collection("location_board").document(boardId).collection("post")
                .add(post)
                .addOnSuccessListener {Toast.makeText(this,"성공",Toast.LENGTH_SHORT).show()}
                .addOnFailureListener {Toast.makeText(this,"성공의 어머니",Toast.LENGTH_SHORT).show()}

        }


    } // onCreate

}