package com.opscpoe.foucsflow

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import android.net.ConnectivityManager

class Login : AppCompatActivity() {

    // Variables
    private lateinit var logEmail: EditText
    private lateinit var logPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignUp: TextView
    private lateinit var forgotPassword: TextView
    private lateinit var authentication: FirebaseAuth
    private lateinit var btnSignInGoogle: Button
    private lateinit var userDao: UserDao

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Room and DAO
        val db = UserDatabase.getInstance(this)
        userDao = db.userDao()

        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        authentication = FirebaseAuth.getInstance()

        // Find views by ID
        logEmail = findViewById(R.id.logEmail)
        logPassword = findViewById(R.id.logPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignUp = findViewById(R.id.btnSignUp)
        forgotPassword = findViewById(R.id.forgotPassword)
        btnSignInGoogle = findViewById(R.id.btnSignInGoogle)

        // Set up listeners
        btnLogin.setOnClickListener {
            val email = logEmail.text.toString().trim()
            val pass = logPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Email or password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (isNetworkAvailable(this)) {
                loginUserOnline(email, pass)
            } else {
                loginUserOffline(email, pass)
            }

            loginUser(email, pass)
        }

        btnSignUp.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
        }

        forgotPassword.setOnClickListener {
            val email = logEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email to reset password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            resetPassword(email)
        }

        // Google Sign-In setup
        btnSignInGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun loginUserOnline(email: String, password: String) {
        authentication.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "You are now logged in", Toast.LENGTH_SHORT).show()

                    // Save user locally
                    CoroutineScope(Dispatchers.IO).launch {
                        userDao.insertUser(User(email = email, password = password))
                    }

                    // Proceed to the main screen
                    startActivity(Intent(this, FocusFlow::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginUserOffline(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val user = userDao.getUser(email, password)
            withContext(Dispatchers.Main) {
                if (user != null) {
                    Toast.makeText(this@Login, "Logged in offline", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@Login, FocusFlow::class.java))
                    finish()
                } else {
                    Toast.makeText(this@Login, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    private fun loginUser(email: String, password: String) {
        authentication.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "You are now logged in", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@Login, FocusFlow::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resetPassword(email: String) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error sending password reset email", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        authentication.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = authentication.currentUser
                    Toast.makeText(this, "Signed in as ${user?.displayName}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, FocusFlow::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
