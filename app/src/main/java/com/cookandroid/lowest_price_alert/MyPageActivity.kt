package com.cookandroid.lowest_price_alert

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop

class MyPageActivity : AppCompatActivity() {

    lateinit var zzim_list_Btn : ImageButton
    lateinit var notice_Btn : Button
    lateinit var userguide_Btn : Button
    
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mypage_activity)

        var userImage = findViewById<ImageView>(R.id.userImage)
        Glide.with(this).load(R.drawable.ipad).circleCrop().into(userImage)

        zzim_list_Btn = findViewById(R.id.zzim_list_Btn)
        notice_Btn = findViewById(R.id.notice_Btn)
        userguide_Btn = findViewById(R.id.userguide_Btn)

        zzim_list_Btn.setOnClickListener {
            val intent = Intent(this, ZzimActivity::class.java)
            startActivity(intent)
        }
    }
}
