package com.opscpoe.foucsflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.Executor

class LockDiary : AppCompatActivity() {

    //variables
    private lateinit var btnUnlockWithBiometric: Button
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_diary)


        //typecasting
        btnUnlockWithBiometric = findViewById(R.id.btnUnlockWithBiometric)

        //initalize firebase
        firebaseAuth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        //set up executor for biometric prompt
        executor = ContextCompat.getMainExecutor(this)

        //create the biometric prompts instance
        biometricPrompt = BiometricPrompt(this,executor,object: BiometricPrompt.AuthenticationCallback(){
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                //successful biometric authentication unlock diary
                Toast.makeText(applicationContext, "Biometric authentication successful", Toast.LENGTH_SHORT).show()

                saveAuthenticationToFirebase(true)

                //redirect user to diary
                val intent = Intent(this@LockDiary, Diary::class.java)
                startActivity(intent)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(applicationContext, "Biometric authentication failed", Toast.LENGTH_SHORT).show()
            }
        })

        //setting up the biometric prompt info

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock diary")
            .setSubtitle("Use your fingerprint to unlock")
            .setNegativeButtonText("Cancel")
            .build()

        //biometric unlock button
        btnUnlockWithBiometric.setOnClickListener{
            // check if biometric is available and enabled
            if(BiometricManager.from(this).canAuthenticate() ==BiometricManager.BIOMETRIC_SUCCESS){
                biometricPrompt.authenticate(promptInfo)
            }else
            {
                Toast.makeText(this, "Biometric authentication is not available", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //function to save authentication into firebase
    private fun saveAuthenticationToFirebase(isBiometricSuccess: Boolean) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val userData = hashMapOf(
                "userId" to user.uid,
                "timestamp" to System.currentTimeMillis(),
                "biometricSuccess" to isBiometricSuccess
            )
            firestore.collection("authEvents")
                .document(user.uid)
                .set(userData)

        }
    }
}