package com.example.recruitz.activities

import android.content.Intent
import android.os.Bundle
import com.example.recruitz.R
import com.example.recruitz.utils.Constants
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : BaseActivity() {
    lateinit var isStudent : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        fullScreenMode()
        customFont(tv_app_name_intro)

        btn_sign_up_tpo_intro.setOnClickListener {
            introToSignUpIntent(false)
        }

        btn_sign_up_student_intro.setOnClickListener {
            introToSignUpIntent(true)
        }

        tv_sign_in.setOnClickListener{
            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }
    }
    private fun introToSignUpIntent(isStudent: Boolean) {
        intent = Intent(this, SignUpActivity::class.java)
        // Passing the boolean variable "isStudent" to next activity
        intent.putExtra(Constants.IS_STUDENT, isStudent)
        startActivity(intent)
    }
}