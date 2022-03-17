package com.example.recruitz.firebase

import android.util.Log
import android.widget.Toast
import com.example.recruitz.models.TPO
import com.example.recruitz.activities.SignInActivity
import com.example.recruitz.activities.SignUpActivity
import com.example.recruitz.models.Student
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class FirebaseAuthentication() {
    private lateinit var auth: FirebaseAuth

    fun signUpStudent(student : Student, password: String, activity : SignUpActivity){
        val email = student.email
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                // If the registration is successfully done
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    val registeredEmail = firebaseUser.email!!
                    student.id = firebaseUser.uid
                    // call the registerUser function of FirestoreClass to make an entry in the database.
                    Firestore().registerStudent(activity, student)

                } else {
                    Toast.makeText(
                        activity,
                        task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("signuperror", "${task.exception!!.message}")
                    activity.hideProgressDialog()
                }

            }
    }

    fun signUpTPO(tpo : TPO, password: String, activity : SignUpActivity){
        val email = tpo.email
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->

                // If the registration is successfully done
                if (task.isSuccessful) {
                    val firebaseUser: FirebaseUser = task.result!!.user!!
                    //val registeredEmail = firebaseUser.email!!
                    tpo.id = firebaseUser.uid
                    // call the registerUser function of FirestoreClass to make an entry in the database.
                    Firestore().registerTPO(activity, tpo)
                } else {
                    Toast.makeText(
                        activity,
                        task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("signuperror", "${task.exception!!.message}")
                    activity.hideProgressDialog()
                }

            }
    }

    fun signIn(email: String, password: String, activity : SignInActivity) {
        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Firestore().loadStudentData(activity)
                } else {
                    Toast.makeText(
                        activity,
                        task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                    Log.e("signinerror", "${task.exception!!.message}")
                    activity.hideProgressDialog()
                }
            }
    }

    fun signOut(){
        auth = Firebase.auth
        auth.signOut()
    }

    fun getCurrentUserID(): String {
        auth = Firebase.auth
        val user = auth.currentUser
        if(user != null){
            return user.uid
        }
        return ""
    }

    fun getCurrentUserMailId(): String? {
        auth = Firebase.auth
        var user = auth.currentUser
        if(user != null) return user.email
        return ""
    }
}