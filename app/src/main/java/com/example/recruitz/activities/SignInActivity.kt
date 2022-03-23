package com.example.recruitz.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.example.recruitz.R
import com.example.recruitz.firebase.FirebaseAuthentication
import com.example.recruitz.models.Student
import com.example.recruitz.models.TPO
import com.example.recruitz.utils.Constants
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        fullScreenMode()

        setupActionBar(toolbar_sign_in_activity)

        btn_sign_in.setOnClickListener{
            signInRegisteredUser()
        }
    }

    private fun signInRegisteredUser() {
        // Here we get the text from editText and trim the space
        val email: String = et_email_sign_in.text.toString().trim { it <= ' ' }
        val password: String = et_password_sign_in.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))

            // Sign-In using FirebaseAuth
            FirebaseAuthentication().signIn(email, password,this)
        }
    }

    /** A function to validate the entries of a user. **/
    private fun validateForm(email: String, password: String): Boolean {
        return when {
            (TextUtils.isEmpty(email)) -> {
                showErrorSnackBar(getString(R.string.enter_email))
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar(getString(R.string.enter_password))
                false
            }
            else -> true
        }
    }

    /** After successful sign-in, sends the student to Main activity **/

    fun signInSuccessByStudent(student: Student) {
        hideProgressDialog()
        Toast.makeText(this, "${student.firstName} signed in successfully.", Toast.LENGTH_LONG).show()
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.STUDENT_DETAILS, student)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        this.finish()
    }

    /** After successful sign-in, sends the TPO to Main activity **/
    fun signInSuccessByTPO(tpo: TPO) {
        hideProgressDialog()
        Toast.makeText(this, "${tpo.firstName} signed in successfully.", Toast.LENGTH_LONG).show()
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.TPO_DETAILS, tpo)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        this.finish()
    }
}