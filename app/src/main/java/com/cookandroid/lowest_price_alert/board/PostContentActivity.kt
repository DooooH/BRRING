package com.cookandroid.lowest_price_alert.board

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.w3c.dom.Text
import java.sql.Timestamp
import com.bumptech.glide.Glide
import com.cookandroid.lowest_price_alert.*
import com.cookandroid.lowest_price_alert.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter

class PostContentActivity : AppCompatActivity() {
    // variables for board

    // firestore
    val firestoredb = FirebaseFirestore.getInstance() // firestore db
    val firebaseDatabase = FirebaseDatabase.getInstance() // 실시간 데이터 db
    val storage = Firebase.storage // firebase cloud storage for profile image
    lateinit var boardId : String
    lateinit var postId : String

    // declare nullable object for Firebase auth
    private var auth: FirebaseAuth? = null
    lateinit var currentUser : FirebaseUser

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
    lateinit var productDetailBtn : Button
    lateinit var writerUsernameTv : TextView
    lateinit var writerProfileImageIv : ImageView
    lateinit var updatedAtTv : TextView
    lateinit var productPriceTv : TextView

    //onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.board_post_content_activity)

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

        //auth 객체 초기화, 인스턴스 get
        auth = FirebaseAuth.getInstance()

        // connect view components to variables
        productImageIv = findViewById(R.id.productImageIv)
        productNameTv = findViewById(R.id.productNameTv)
        postTitleTv = findViewById(R.id.postTitleTv)
        postContentTv = findViewById(R.id.postContentTv)
        commentContentEt = findViewById(R.id.commentContentEt)
        commentSubmitBtn = findViewById(R.id.commentSubmitBtn)
        productDetailBtn = findViewById(R.id.productDetailBtn)
        writerUsernameTv = findViewById(R.id.writerUsernameTv)
        writerProfileImageIv = findViewById(R.id.writerProfileImageIv)
        updatedAtTv = findViewById(R.id.updatedAtTv)
        productPriceTv = findViewById(R.id.productPriceTv)

        // connect list view
        commentLv = findViewById(R.id.commentLv)

        // get postId
        boardId = intent.getStringExtra("boardId").toString()
        postId = intent.getStringExtra("postId").toString()

        // get post content from firestore
        getPost()

        // get comments from firestore
        getComment()

        // add on click listener to button
        commentSubmitBtn.setOnClickListener {
            writeComment()
        }
        commentContentEt.setOnEditorActionListener{ textView, action, event ->
            var handled = false

            if (action == EditorInfo.IME_ACTION_DONE) {
                // write comment
                writeComment()
                handled = true
            }

            handled
        }

    } // onCreate

    fun getPost(){
        firestoredb.collection("location_board").document(boardId)
            .collection("post").document(postId)
            .get()
            .addOnSuccessListener { document ->
                if(document != null){

                    val sdf = SimpleDateFormat("게시일 : yyyy년 MM월 dd일")

                    //Toast.makeText(this, "${document.id} : ${document.data}", Toast.LENGTH_LONG).show()
                    //var postId = document.id
                    Glide.with(this).load("http:"+document["product_image_url"].toString())
                        .into(productImageIv) //이미지 url로 사진 불러오기
                    writerUsernameTv.text = document["writer_username"].toString()
                    updatedAtTv.text = sdf.format((document["updated_at"] as com.google.firebase.Timestamp).toDate()).toString()
                    productNameTv.text = document["product_name"].toString()
                    postTitleTv.text = document["title"].toString()
                    postContentTv.text = document["content"].toString()

                    // set profile image from firebase cloud storage
                    var storageRef = storage.reference
                    var imagesRef : StorageReference? = storageRef.child(document["writer_profile_image"].toString())
                    imagesRef?.downloadUrl?.addOnSuccessListener { uri ->
                        Glide.with(this)
                            .load(uri)
                            .into(writerProfileImageIv) //이미지 url로 사진 불러오기
                    }

                    // get realtime price from realtime database
                    val path = "product_list/" + document["product_id"].toString() // 실시간 db에 접근하기 위한 경로.
                    val myRef: DatabaseReference = firebaseDatabase.getReference(path) // 실시간 db에 접근

                    myRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val snapshot_info = snapshot.child("price")

                            for (item in snapshot_info.children) {
                                productPriceTv.text = item.value.toString() + "원"
                                //Toast.makeText(this@PostActivity,"$product_name 가격정보 : $product_price", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) { // 실시간 db 접근을 실패하면
                            println("Failed to read value.")
                        }
                    })

                    // set product detail link button by product id
                    productDetailBtnSetOnClickListener(document["product_id"].toString())
                }
                else{
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: ", Toast.LENGTH_SHORT).show()
            }

    }

    fun getComment(){
        commentList.clear()
        firestoredb.collection("location_board").document(boardId)
            .collection("post").document(postId)
            .collection("comment")
            .get()
            .addOnSuccessListener { documents ->
                if(documents != null){
                    for(document in documents){
                        //Toast.makeText(this, "${document.id} : ${document.data}", Toast.LENGTH_LONG).show()
                        var commentId = document.id
                        var uid = document["uid"].toString()
                        var username = document["username"].toString()
                        var profile_image = document["profile_image"].toString()
                        var commentContent = document["comment_content"].toString()
                        var lb = Comment(commentId, uid, profile_image, username, commentContent)
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

    }

    fun writeComment() {
        currentUser = auth?.currentUser!!

        if(currentUser?.uid.toString() == "null" || currentUser == null){
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
            var profile_image = ""
            val uid = auth!!.uid.toString()
            firestoredb.collection("user").document(uid)
                .get()
                .addOnSuccessListener { document ->
                    username = if(document != null && document["username"].toString() != "null"){
                        document["username"].toString()
                    } else{
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
                    val comment = hashMapOf(
                        "comment_content" to commentContent,
                        "username" to username,
                        "profile_image" to profile_image,
                        "uid" to uid
                    )

                    firestoredb
                        .collection("location_board").document(boardId)
                        .collection("post").document(postId)
                        .collection("comment")
                        .add(comment)
                        .addOnSuccessListener {
                            Toast.makeText(this,"댓글 작성 완료",Toast.LENGTH_SHORT).show()
                            getComment()
                            commentContentEt.setText("")
                            var handled = false
                            // hide keyboard
                            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            inputMethodManager.hideSoftInputFromWindow(commentContentEt.windowToken, 0)
                            handled = true
                        }
                        .addOnFailureListener {
                            Toast.makeText(this,"댓글 작성 실패",Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error getting documents: ", Toast.LENGTH_SHORT).show()
                }


        }

    }

    fun productDetailBtnSetOnClickListener(no : String){

        // get item code from firestore
        lateinit var productCode : String

        firestoredb.collection("product_list").whereEqualTo("no", no)
            .get()
            .addOnSuccessListener { results ->
                for (result in results) {
                    productCode = result.id
                    }

                productDetailBtn.setOnClickListener {
                    val intent = Intent(this, ChartActivity::class.java)
                    intent.putExtra("product_no", no)
                    intent.putExtra("product_code", productCode)
                    startActivity(intent)
                }

            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error getting documents: ", Toast.LENGTH_SHORT).show()
            }

    }

}
