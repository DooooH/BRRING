package com.cookandroid.lowest_price_alert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    // variables for product test
    lateinit var productBtn : Button
    lateinit var loginBtn : Button
    lateinit var logoutBtn : Button
    lateinit var signupText : TextView

    // declare nullable object for Firebase auth
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        productBtn = findViewById(R.id.product_infoBtn)
        loginBtn = findViewById(R.id.loginBtn)
        logoutBtn = findViewById(R.id.logoutBtn)
        signupText = findViewById(R.id.signupText)

        productBtn.setOnClickListener{
            val intent = Intent(this, ChartActivity::class.java)
            startActivity(intent)
        } // productoBtn onclick listener
        loginBtn.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        } // loginBtn onclick listener

    } // onCreate

}