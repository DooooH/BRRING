package com.cookandroid.lowest_price_alert

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    // variables for login
    private lateinit var emailEditText : EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginBtn : Button
    private lateinit var google_sign_in_button : SignInButton

    // declare nullable object for Firebase auth
    private var auth: FirebaseAuth? = null

    // declare nullable object for google login
    private var googleSignInClient : GoogleSignInClient? = null

    // firestore
    val firestoredb = FirebaseFirestore.getInstance() // firestore db

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginBtn = findViewById(R.id.loginBtn)
        google_sign_in_button = findViewById(R.id.google_sign_in_button)

        //auth 객체 초기화, 인스턴스 get
        auth = FirebaseAuth.getInstance()

        // login with email
        loginBtn.setOnClickListener{
            emailLogin()
        }

        google_sign_in_button.setOnClickListener {
            googleLogin()
        }
        var back_btn = findViewById<ImageButton>(R.id.back_button) // 뒤로가기
        back_btn.setOnClickListener{
            onBackPressed()
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)) // 빌드 해주면 문제 없음
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

    } // onCreate
    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth?.currentUser
//        Toast.makeText(this, "사용자 로그인정보 있음", Toast.LENGTH_SHORT).show()
        //updateUI(currentUser)
    }
    private fun emailLogin() {
        auth?.signInWithEmailAndPassword(emailEditText.text.toString(),passwordEditText.text.toString())
            ?.addOnCompleteListener{//통신 완료가 된 후 무슨일을 할지
                    task->
                if(task.isSuccessful){
                    //로그인 처리를 해주면 됨!
                    Toast.makeText(this, "브링에 오신것을 환영합니다!", Toast.LENGTH_LONG).show()
                    val user = auth?.currentUser
                    updateUI(user)
                }
                else{
                    // 오류가 난 경우!
                    Toast.makeText(this,task.exception?.message, Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
        //email, password null인 경우 예외 처리 해주기
    }
    private fun googleLogin() {
        val signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.d(TAG, "Google sign in failed", e)
            }
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth?.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        //Toast.makeText(this, user?.uid.toString(), Toast.LENGTH_SHORT).show()

        // if there are user info
        // store user to firestore
        // Create a reference to the cities collection
        val userRef = firestoredb.collection("user").document(user?.uid.toString())
            .get()
            .addOnSuccessListener { document ->
                if(document == null || document.data == null){ // user not exist -> sign up
                    val currentUser = hashMapOf(
                        "email" to user?.email.toString()
                    )
                    firestoredb.collection("user").document(user?.uid.toString())
                        .set(currentUser)
                        .addOnSuccessListener {
                        //Toast.makeText(this, "DocumentSnapshot successfully written!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                        //Toast.makeText(this, "Error writing document", Toast.LENGTH_SHORT).show()
                         }
                }
                else{
                    Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}
