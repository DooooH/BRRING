package com.cookandroid.lowest_price_alert.board

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
    lateinit var selectedProductTv : TextView
    lateinit var selectedProductIdEt : EditText
    lateinit var selectedProductImgPathEt : EditText
    lateinit var selectedProductPriceEt : EditText

    lateinit var wishLv : ListView


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
        selectedProductTv = findViewById(R.id.selectedProductTv)
        selectedProductIdEt = findViewById(R.id.selectedProductIdEt)
        selectedProductImgPathEt = findViewById(R.id.selectedProductImgPathEt)
        selectedProductPriceEt = findViewById(R.id.selectedProductPriceEt)

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
            val whytv = view.findViewById<TextView>(R.id.whytv)
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
                                        var itemPrice = result["itemPrice"].toString()
                                        var option = result["option"].toString()
                                        val wish = Wish(
                                            wishId,
                                            itemId,
                                            "ipad",//itemPhoto,
                                            itemName,
                                            itemPrice,
                                            option
                                        )
                                        wishList.add(wish)
                                    }

                                    // connect location board list and list view via adapter

                                    wishLv.adapter = wishListAdapter
                                    wishLv.setOnItemClickListener { adapterView, view, i, l ->
                                        selectedProductTv.text = view.findViewById<TextView>(R.id.itemName).text
                                        selectedProductIdEt.setText(view.findViewById<TextView>(R.id.itemId).text)
                                        selectedProductPriceEt.setText(view.findViewById<TextView>(R.id.itemPrice).text)

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
/*
            var dialogView = View.inflate(this, R.layout.board_select_wish_activity)
            var dlg = AlertDialog.Builder(this)
            dlg.setTitle("공구할 상품 선택")
*/

            Toast.makeText(this, "APPLE 아이패드 미니 6세대 Wi-Fi 64GB (정품) 선택됨", Toast.LENGTH_SHORT).show()
            selectedProductTv.setText("APPLE 아이패드 미니 6세대 Wi-Fi 64GB (정품)")
            selectedProductIdEt.setText("15253217")
            selectedProductImgPathEt.setText("img.danawa.com/prod_img/500000/217/253/img/15253217_1.jpg?shrink=330:330&_v=20211026140920")
            selectedProductPriceEt.setText("160000")
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
                "writer_id" to currentUser?.uid.toString()
            )

            firestoredb.collection("location_board").document(boardId).collection("post")
                .add(post)
                .addOnSuccessListener {Toast.makeText(this,"성공",Toast.LENGTH_SHORT).show()}
                .addOnFailureListener {Toast.makeText(this,"성공의 어머니",Toast.LENGTH_SHORT).show()}

        }


    } // onCreate

}