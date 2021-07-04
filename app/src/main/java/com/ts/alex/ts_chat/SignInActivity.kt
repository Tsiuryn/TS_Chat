package com.ts.alex.ts_chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ts.alex.ts_chat.ChatActivity.Companion.USER_NAME
import com.ts.alex.ts_chat.domain.models.User

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var vName: EditText
    private lateinit var vEmail: EditText
    private lateinit var vPassword: EditText
    private lateinit var vBtnLogIn: MaterialButton
    private lateinit var vToogleSignUp: TextView

    private var loginModeActive: Boolean = false

    private lateinit var db: FirebaseDatabase
    private lateinit var userReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        db = FirebaseDatabase.getInstance()
        userReference = db.reference.child("users")

        auth = FirebaseAuth.getInstance()
        vName = findViewById(R.id.vName)
        vEmail = findViewById(R.id.vEmail)
        vPassword = findViewById(R.id.vPassword)
        vBtnLogIn = findViewById(R.id.vBtnSignUp)
        vToogleSignUp = findViewById(R.id.vTabToLogIn)

        vBtnLogIn.setOnClickListener {
            loginSignUpUser(
                email = vEmail.text.toString().trim(),
                password = vPassword.text.toString().trim()
            )
        }

        if(auth.currentUser != null){
            startActivity(Intent(this, UserListActivity::class.java))
        }
    }

    private fun loginSignUpUser(email: String, password: String) {

        if (loginModeActive) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG11", "signInWithEmail:success")
                        val user = auth.currentUser
                        val intent = Intent(this, UserListActivity::class.java)
                        intent.putExtra(USER_NAME, vName.text.toString().trim())
                        startActivity(intent)                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG11", "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("TAG11", "createUserWithEmail:success")
                        val user = auth.currentUser
                        createUser(user)
                        val intent = Intent(this, UserListActivity::class.java)
                        intent.putExtra(USER_NAME, vName.text.toString().trim())
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("TAG11", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }


    }

    private fun createUser(user: FirebaseUser?) {
        userReference.push().setValue(
        User(
            name = vName.text.toString().trim(),
            email = user?.email?: "",
            id =  user?.uid?: ""
        )
        )
    }

    fun toogleLoginMode(view: View) {
        if (loginModeActive) {
            loginModeActive = false
            vBtnLogIn.text = "Sign Up"
            vToogleSignUp.text = "Or, log in"

        } else {
            loginModeActive = true
            vBtnLogIn.text = "Log In"
            vToogleSignUp.text = "Or, Sign Up"
        }
    }
}