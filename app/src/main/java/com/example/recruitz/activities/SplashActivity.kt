package com.example.recruitz.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.recruitz.R
import com.example.recruitz.firebase.FirebaseAuthentication
import com.example.recruitz.firebase.Firestore
import com.example.recruitz.models.Student
import com.example.recruitz.models.TPO
import com.example.recruitz.utils.Constants
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        fullScreenMode()
        customFont(tv_app_name)

        // 3 SECOND DELAY BEFORE SWITCHING TO NEXT ACTIVITY
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, IntroActivity::class.java))
            finish()
        }, 300)
    }
    fun signInSuccessByStudent(loggedInStudent: Student) {
        Log.i("student Main","Student Main")
        Toast.makeText(this, "${loggedInStudent.firstName} signed in successfully.", Toast.LENGTH_LONG).show()
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.STUDENT_DETAILS, loggedInStudent)
        startActivity(intent)
        this.finish()
    }

    fun signInSuccessByTPO(loggedInTPO: TPO) {
        Toast.makeText(this, "${loggedInTPO.email} signed in successfully.", Toast.LENGTH_SHORT).show()
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.TPO_DETAILS, loggedInTPO)
        startActivity(intent)
        this.finish()
    }
}