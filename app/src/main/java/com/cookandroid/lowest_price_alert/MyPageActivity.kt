package com.cookandroid.lowest_price_alert

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MyPageActivity : AppCompatActivity() {

    lateinit var zzim_list_Btn : Button
    lateinit var notice_Btn : Button
    lateinit var userguide_Btn : Button
    
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mypage_activity)

        zzim_list_Btn = findViewById(R.id.zzim_list_Btn)
        notice_Btn = findViewById(R.id.notice_Btn)
        userguide_Btn = findViewById(R.id.userguide_Btn)

        zzim_list_Btn.setOnClickListener {
            val intent = Intent(this, ZzimActivity::class.java)
            startActivity(intent)
        }
    }
}
