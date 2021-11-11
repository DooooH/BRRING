package com.cookandroid.lowest_price_alert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    // variables for login
    lateinit var emailEditText : EditText
    lateinit var passwordEditText: EditText
    lateinit var loginBtn : Button
    lateinit var google_sign_in_button : SignInButton

    // declare nullable object for Firebase auth
    var auth: FirebaseAuth? = null

    // declare nullable object for google login
    var googleSignInClient : GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginBtn = findViewById(R.id.loginBtn)
        google_sign_in_button = findViewById(R.id.google_sign_in_button)

        //auth 객체 초기화, 인스턴스 get
        auth = FirebaseAuth.getInstance()

        // login with email
        loginBtn.setOnClickListener {
            emailLogin()
        }
    } // onCreate

    fun emailLogin() {
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
        //email, password null인 경우 예외 처리 해주
    }
    fun googleLogin() {

    }
}