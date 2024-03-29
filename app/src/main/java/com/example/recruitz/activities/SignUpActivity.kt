package com.example.recruitz.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.example.recruitz.R
import com.example.recruitz.firebase.FirebaseAuthentication
import com.example.recruitz.firebase.Firestore
import com.example.recruitz.models.College
import com.example.recruitz.models.Student
import com.example.recruitz.models.TPO
import com.example.recruitz.utils.Constants
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : BaseActivity() {
    private var isStudent: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        /** Initialize "isStudent" */
        var extras : Bundle? = intent.extras

        isStudent = extras!!.getBoolean(Constants.IS_STUDENT)

        /** disables collegeName text view for student in layout */
        if(isStudent) til_college_name_sign_up.visibility= View.GONE
        else til_college_name_sign_up.visibility= View.VISIBLE

        fullScreenMode()
        setupActionBar(toolbar_sign_up)

        btn_sign_up.setOnClickListener {
            registerUser()
        }
    }

    /** A function to register a user to the app using the Firebase. **/
    private fun registerUser() {
        // Here we get the text from editText and trim the space
        val firstName: String = et_first_name_sign_up.text.toString().trim { it <= ' ' }
        val lastName: String = et_last_name_sign_up.text.toString().trim { it <= ' ' }
        val email: String = et_email_sign_up.text.toString().trim { it <= ' ' }
        val password: String = et_password_sign_up.text.toString().trim { it <= ' ' }
        val collegeName: String = et_college_name_sign_up.text.toString().trim { it <= ' ' }

        if (validateForm(firstName,email, password)) {
            if(isStudent){
                val student = Student()
                student.firstName=firstName
                student.lastName=lastName
                student.email=email

                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuthentication().signUpStudent(student,password,this)
            }
            else{
                val tpo = TPO()
                tpo.firstName=firstName
                tpo.lastName=lastName
                tpo.collegeName = collegeName
                tpo.email=email

                val college = College()
                college.collegeName = collegeName

                // Show the progress dialog.
                showProgressDialog(resources.getString(R.string.please_wait))
                Firestore().registerCollege(college,tpo,password,this)
            }
        }
    }

    /** A function to validate the entries of a new user. **/
    private fun validateForm(firstName: String,email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(firstName) -> {
                showErrorSnackBar(getString(R.string.enter_first_name))
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar(getString(R.string.enter_email))
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar(getString(R.string.enter_password))
                false
            }
            else -> {
                true
            }
        }
    }

    /**
     * A function to be called the user is registered successfully and entry is made in the firestore database.
     */
    fun studentRegisteredSuccess() {
        hideProgressDialog()
        Toast.makeText(this,
            "${FirebaseAuthentication().getCurrentUserMailId()} has successfully registered with Recruitz!",
            Toast.LENGTH_LONG).show()

        /** Sends the student to Update Profile activity after sign-up */
        val intent = Intent(this,UpdateProfileActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /**
     * A function to be called after the TPO is registered successfully
     * and entry is made in the firestore database.
     */
    fun tpoRegisteredSuccess(tpo : TPO){
        hideProgressDialog()
        Toast.makeText(this,
            "${FirebaseAuthentication().getCurrentUserMailId()} has successfully registered with Recruitz!",
            Toast.LENGTH_LONG).show()

        /** TPO is sent to Main activity after sign-up */
        intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.TPO_DETAILS, tpo)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        this.finish()
    }
}