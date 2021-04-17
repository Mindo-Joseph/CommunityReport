package com.example.communityreport

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_o_t_pvalidation.*


class OTPvalidation : AppCompatActivity() {
    private var VerificationId: String? = null
    private var Auth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_o_t_pvalidation)
        val mobileNumber = intent.getStringExtra("mobileNumber")
        Auth = FirebaseAuth.getInstance()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+$mobileNumber",
            60,
            java.util.concurrent.TimeUnit.SECONDS,
            this,
            callbacks
        )
        verifyButton.setOnClickListener{
            if(!otpTextField.text.isNullOrEmpty()){
                verifyVerificationCode(otpTextField.text.toString())
            }
        }

    }
    private val callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            val code = p0.smsCode
            if (code != null){
                verifyVerificationCode(code)
            }
        }
        override fun onVerificationFailed(e: FirebaseException) {
            // This callback is invoked in an invalid request for verification is made,
            // for instance if the the phone number format is not valid.
            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(this@OTPvalidation, e.message, Toast.LENGTH_LONG).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(this@OTPvalidation, e.message, Toast.LENGTH_LONG).show()
            }
        }
        override fun onCodeSent(
            s: String,
            forceResendingToken: PhoneAuthProvider.ForceResendingToken
        ) {
            super.onCodeSent(s, forceResendingToken)
            VerificationId = s
            //mResendToken = forceResendingToken
        }
    }
    private fun verifyVerificationCode(code: String) {
        //creating the credential
        val credential = PhoneAuthProvider.getCredential(VerificationId!!, code)

        //signing the user
        signInWithPhoneAuthCredential(credential)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Auth!!.signInWithCredential(credential)
            .addOnCompleteListener(
                this,
                OnCompleteListener<AuthResult?> { task ->
                    if (task.isSuccessful) {
                        //verification successful we will start the profile activity
                        Toast.makeText(this,"Success", Toast.LENGTH_LONG).show()
                        val i = Intent(this, Success::class.java)
                        startActivity(i)
                    } else {
                        Toast.makeText(this,"Failed", Toast.LENGTH_LONG).show()
                    }
                })
    }
}