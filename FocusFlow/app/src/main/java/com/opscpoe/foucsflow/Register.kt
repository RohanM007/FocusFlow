package com.opscpoe.foucsflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException

class Register : AppCompatActivity() {

    //variables
    lateinit var RegEmail: EditText
    lateinit var regPassword: EditText
    lateinit var RegConPass: EditText
    lateinit var btnRegister: Button
    lateinit var btnBackToLogin: Button
    lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        //typecasting
        RegEmail = findViewById(R.id.RegEmail)
        regPassword = findViewById(R.id.regPassword)
        RegConPass = findViewById(R.id.RegConPass)
        mAuth = FirebaseAuth.getInstance()
        btnRegister = findViewById(R.id.btnRegister)
        btnBackToLogin = findViewById(R.id.btnBackToLogin)

        //initializing Firebase
        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()

        // Handle registration
        btnRegister.setOnClickListener {
            SignUp()
        }

        // Navigate back to the login page
        btnBackToLogin.setOnClickListener {
            val intent = Intent(this@Register, Login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun SignUp() {
        val email = RegEmail.text.toString().trim()
        val pass = regPassword.text.toString().trim()
        val confirmPassword = RegConPass.text.toString().trim()

        // Validate inputs
        if (email.isBlank() || pass.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(this, "Email and Password cannot be blank!", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate password match
        if (pass != confirmPassword) {
            Toast.makeText(this, "Password and Confirm Password do not match", Toast.LENGTH_SHORT).show()
            return
        }

        // Create user in Firebase
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Successfully Signed Up", Toast.LENGTH_SHORT).show()

                    // Navigate to login screen after successful sign up
                    val intent = Intent(this@Register, Login::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Handle specific errors during sign up
                    handleSignUpError(task.exception)
                }
            }
    }

    private fun handleSignUpError(exception: Exception?) {
        when (exception) {
            is FirebaseAuthWeakPasswordException -> {
                // Weak password
                Toast.makeText(this, "Weak password. Password should be at least 6 characters", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthInvalidCredentialsException -> {
                // Invalid email format
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            }
            is FirebaseAuthUserCollisionException -> {
                // Email already registered
                Toast.makeText(this, "This email is already registered", Toast.LENGTH_SHORT).show()
            }
            else -> {
                // General error
                Toast.makeText(this, "Sign Up failed: ${exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
