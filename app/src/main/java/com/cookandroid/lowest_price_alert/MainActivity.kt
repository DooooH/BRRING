package com.cookandroid.lowest_price_alert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    lateinit var emailEditText : EditText
    lateinit var passwordEditText: EditText
    lateinit var loginBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //nullable한 FirebaseAuth 객체 선언
        var auth: FirebaseAuth? = null
        //auth 객체 초기화
        auth = FirebaseAuth.getInstance()

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginBtn = findViewById(R.id.loginBtn)

        loginBtn.setOnClickListener{
            auth?.signInWithEmailAndPassword(emailEditText.text.toString(),passwordEditText.text.toString())
                ?.addOnCompleteListener{//통신 완료가 된 후 무슨일을 할지
                        task->
                    if(task.isSuccessful){
                        //로그인 처리를 해주면 됨!
                        Toast.makeText(this, "login", Toast.LENGTH_LONG).show()
                    }
                    else{
                        // 오류가 난 경우!
                        Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}