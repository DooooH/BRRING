package com.cookandroid.lowest_price_alert


import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.cookandroid.lowest_price_alert.board.BoardActivity
import com.cookandroid.lowest_price_alert.board.WritePostActivity

class MyPageActivity : AppCompatActivity() {

    lateinit var zzim_list_Btn : ImageButton
    lateinit var notice_Btn : Button
    lateinit var userguide_Btn : Button
    lateinit var write_post_Btn : ImageButton
    lateinit var document_manage_Btn : ImageButton

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mypage_activity)

        var back_btn = findViewById<ImageButton>(R.id.back_button) // 뒤로가기
        back_btn.setOnClickListener{
            onBackPressed()
        }

        var userImage = findViewById<ImageView>(R.id.userImage)
        Glide.with(this).load(R.drawable.junha).circleCrop().into(userImage)

        zzim_list_Btn = findViewById(R.id.zzim_list_Btn)
        notice_Btn = findViewById(R.id.notice_Btn)
        userguide_Btn = findViewById(R.id.userguide_Btn)
        write_post_Btn = findViewById(R.id.write_post_Btn)
        document_manage_Btn = findViewById(R.id.document_manage_Btn)


        zzim_list_Btn.setOnClickListener {
            val intent = Intent(this, ZzimActivity::class.java)
            startActivity(intent)
        }

        write_post_Btn.setOnClickListener{
            val intent = Intent(this, WritePostActivity::class.java)
            startActivity(intent)
        }

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

    }
}
