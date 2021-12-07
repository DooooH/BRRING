package com.cookandroid.lowest_price_alert.board

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.cookandroid.lowest_price_alert.LoginActivity
import com.cookandroid.lowest_price_alert.MainActivity
import com.cookandroid.lowest_price_alert.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.*
import java.sql.Date
import java.sql.Timestamp

class WritePostActivity : AppCompatActivity() {
    // view components
    lateinit var titleEt : EditText
    lateinit var contentEt : EditText
    lateinit var selectProductBtn : Button
    lateinit var writeBtn : Button
    lateinit var selectedProductTv : TextView
    lateinit var selectedProductIdEt : EditText
    lateinit var selectedProductImgPathEt : EditText
    lateinit var selectedProductPriceEt : EditText

    lateinit var wishLv : ListView


    // firestore
    val firebaseDatabase = FirebaseDatabase.getInstance() // 실시간 데이터 db
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
        selectedProductTv = findViewById(R.id.selectedProductTv)
        selectedProductIdEt = findViewById(R.id.selectedProductIdEt)
        selectedProductImgPathEt = findViewById(R.id.selectedProductImgPathEt)
        selectedProductPriceEt = findViewById(R.id.selectedProductPriceEt)


        var back_btn = findViewById<ImageButton>(R.id.back_button) // 뒤로가기
        back_btn.setOnClickListener{
            onBackPressed()
        }


        // get boardId
        boardId = intent.getStringExtra("boardId").toString()

        //auth 객체 초기화, 인스턴스 get
        auth = FirebaseAuth.getInstance()
        val currentUser = auth?.currentUser

        // select item function
        selectProductBtn.setOnClickListener {
            var wishList = arrayListOf<Wish>()
            var wishListAdapter = WishListAdapter(this, wishList)
            val view: View = LayoutInflater.from(this)
                .inflate(R.layout.board_select_wish_activity, null)
            wishLv = view.findViewById(R.id.wishLv)
            var dialogView = view
            var dlg = AlertDialog.Builder(this)
            dlg.setTitle("공구 상품 선택")
            dlg.setView(dialogView)
            dlg.setPositiveButton("확인", null)
            dlg.setNegativeButton("취소", null)


            // get wish items
            firestoredb.collection("user").document(currentUser?.uid.toString())
                .get()
                .addOnSuccessListener { document ->
                    val wishlist = document["wish_list"] as ArrayList<String>?
                    if (wishlist != null) {
                        for(productId in wishlist) {
                            firestoredb.collection("product_list").whereEqualTo("no", productId)
                                .get()
                                .addOnSuccessListener { results ->

                                    for (result in results) {
                                        var wishId = document.id.toString()
                                        var itemId = result["no"].toString()
                                        var itemPhoto = result["image_url"].toString()
                                        var itemName = result["name"].toString()
                                        var itemPrice = ""
                                        var option = result["option"].toString()

                                        val path = "product_list/$itemId" // 실시간 db에 접근하기 위한 경로.
                                        val myRef: DatabaseReference = firebaseDatabase.getReference(path) // 실시간 db에 접근

                                        myRef.addValueEventListener(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                val snapshot_info = snapshot.child("price")

                                                for (item in snapshot_info.children) {
                                                    itemPrice = item.value.toString()
                                                }


                                                val wish = Wish(
                                                    wishId,
                                                    itemId,
                                                    itemPhoto,//itemPhoto,
                                                    itemName,
                                                    itemPrice,
                                                    option
                                                )
                                                wishList.add(wish)


                                                // connect location board list and list view via adapter

                                                wishLv.adapter = wishListAdapter
                                                wishLv.setOnItemClickListener { adapterView, view, i, l ->
                                                    selectedProductTv.text = view.findViewById<TextView>(R.id.itemName).text
                                                    selectedProductIdEt.setText(view.findViewById<TextView>(R.id.itemId).text)
                                                    selectedProductPriceEt.setText(view.findViewById<TextView>(R.id.itemPrice).text)
                                                    selectedProductImgPathEt.setText(view.findViewById<TextView>(R.id.itemPhotoTv).text)
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) { // 실시간 db 접근을 실패하면
                                                println("Failed to read value.")
                                            }
                                        })
                                    }

                                }
                        }
                        dlg.show()

                    }
                    else{
                        Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show()
                    }

                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error getting documents: ", Toast.LENGTH_SHORT).show()
                }
        }

        // write function
        writeBtn.setOnClickListener {
            // get today time
            val today = java.util.Date()
            // get elements
            val productTitle = titleEt.text.toString()
            val productContent = contentEt.text.toString()
            val productId = selectedProductIdEt.text.toString()
            val productImageUrl = selectedProductImgPathEt.text.toString()
            val productName = selectedProductTv.text.toString()
            val productPrice = selectedProductPriceEt.text.toString()

            // if there are no title or no content
            if (productContent == "" || productTitle == "") {
                Toast.makeText(this, "글 제목과 내용을 작성해주세요.", Toast.LENGTH_SHORT).show()
            }
            else if (productName == "선택상품 없음"){
                Toast.makeText(this, "공동구매 할 상품을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
            else {
                // get current user username, profile image
                val uid = auth!!.uid.toString()
                var username = ""
                var profile_image = ""
                firestoredb.collection("user").document(uid)
                    .get()
                    .addOnSuccessListener { document ->
                        username =
                            if (document != null && document["username"].toString() != "null") {
                                document["username"].toString()
                            } else {
                                // unset username -> use anonymous name
                                "익명"
                            }
                        profile_image =
                            if (document != null && document["profile_image"].toString() != "null") {
                                document["profile_image"].toString()
                            } else {
                                "user-profile/no_profile.png"
                            }
                        // set post document
                        val post = hashMapOf(
                            "created_at" to Timestamp(today.time),
                            "product_id" to productId,
                            "product_image_url" to productImageUrl,
                            "product_name" to productName,
                            "product_price" to productPrice,
                            "title" to productTitle,
                            "content" to productContent,
                            "updated_at" to Timestamp(today.time),
                            "writer_id" to uid,
                            "writer_username" to username,
                            "writer_profile_image" to profile_image
                        )

                        firestoredb.collection("location_board").document(boardId)
                            .collection("post")
                            .add(post)
                            .addOnSuccessListener {
                                Toast.makeText(this, "게시글 작성 완료", Toast.LENGTH_SHORT).show()

                                var outIntent = Intent(applicationContext, PostActivity::class.java)
                                setResult(Activity.RESULT_OK, outIntent)
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "게시글 작성 실패", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, "Error getting documents: ", Toast.LENGTH_SHORT).show()
                    }

            }
        }

    } // onCreate

}
