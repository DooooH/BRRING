package com.cookandroid.lowest_price_alert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.cookandroid.lowest_price_alert.board.BoardActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    // variables for product test
//    lateinit var productBtn : Button
//    lateinit var boardBtn : Button
//    lateinit var loginBtn : Button
//    lateinit var signupText : TextView
//    lateinit var searchBtn : Button
//    lateinit var searchItem : EditText
//    lateinit var chartBtn : Button
//    lateinit var mypageBtn : Button
    lateinit var searchText : Button
<<<<<<< Updated upstream

    lateinit var uiBtn : Button
=======
>>>>>>> Stashed changes

    // declare nullable object for Firebase auth
//    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        productBtn = findViewById(R.id.product_infoBtn)
//        boardBtn = findViewById(R.id.boardBtn)
//        loginBtn = findViewById(R.id.loginBtn)
//        signupText = findViewById(R.id.signupText)
//        searchBtn = findViewById(R.id.searchBtn)
//        searchItem = findViewById(R.id.search_txt)
//        mypageBtn = findViewById(R.id.mypage_btn)
<<<<<<< Updated upstream

        uiBtn = findViewById(R.id.UIBtn)
=======
>>>>>>> Stashed changes


<<<<<<< Updated upstream
        uiBtn.setOnClickListener{
            val intent = Intent(this, UIActivity::class.java)
            startActivity(intent)
        }


        searchText = findViewById<Button>(R.id.search_txt)

=======
        searchText = findViewById<Button>(R.id.search_txt)

>>>>>>> Stashed changes
        searchText.setOnClickListener(){
            val intent = Intent(this, SearchViewActivity::class.java)
            startActivity(intent)
        }
//        productBtn.setOnClickListener{
//            val intent = Intent(this, ChartActivity::class.java)
//            startActivity(intent)
//        } // productoBtn onclick listener
//        boardBtn.setOnClickListener {
//            val intent = Intent(this, BoardActivity::class.java)
//            startActivity(intent)
//        }
//        loginBtn.setOnClickListener{
//            val intent = Intent(this, LoginActivity::class.java)
//            startActivity(intent)
//        } // loginBtn onclick listener
//        searchBtn.setOnClickListener{
//            val intent = Intent(this, SearchActivity::class.java)
//            intent.putExtra("item", searchItem.text.toString())
//            startActivity(intent)
//        }
//        mypageBtn.setOnClickListener {
//            val intent = Intent(this, MyPageActivity::class.java)
//            startActivity(intent)
//        }
    } // onCreate
}
