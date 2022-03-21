package com.example.recruitz.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.example.recruitz.activities.*
import com.example.recruitz.models.College
import com.example.recruitz.models.TPO
import com.example.recruitz.models.Company
import com.example.recruitz.models.Student
import com.example.recruitz.utils.Constants
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class Firestore {
    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerStudent(activity : SignUpActivity, studentInfo: Student) {
        mFireStore.collection(Constants.STUDENTS)
            // Here the document id is the Student ID.
            .document(studentInfo.id)
            // Here the studentInfo are field values and the SetOption is set to merge. It is for if we want to merge
            .set(studentInfo, SetOptions.merge())
            .addOnSuccessListener {
                // Here call a function of base activity for transferring the result to it.
                activity.studentRegisteredSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error registering student",
                    e
                )
            }
    }

    fun registerCollege(college : College, tpo: TPO, password: String, activity: SignUpActivity){
        mFireStore.collection((Constants.COLLEGES))
            .add(college)
            .addOnSuccessListener {document ->
                tpo.collegeCode=document.id
                FirebaseAuthentication().signUpTPO(tpo,password,activity)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error registering college",
                    e
                )
            }
    }

    fun registerTPO(activity : SignUpActivity, tpoInfo: TPO) {
        mFireStore.collection(Constants.TPO)
            .document(tpoInfo.id)
            .set(tpoInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.tpoRegisteredSuccess(tpoInfo)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error registering tpo",
                    e
                )
            }
    }

    fun loadStudentData(activity: Activity) {
        // Here we pass the collection name from which we wants the data.
        mFireStore.collection(Constants.STUDENTS)
            // The document id to get the Fields of user.
            .document(FirebaseAuthentication().getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i("detail", document.toString())

                // Here we have received the document snapshot which is converted into the Student Data model object.
                val loggedInUser = document.toObject(Student::class.java)!!

                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is SplashActivity ->{
                        activity.signInSuccessByStudent(loggedInUser)
                    }
                    is SignInActivity -> {
                        activity.signInSuccessByStudent(loggedInUser)
                    }
                    is UpdateProfileActivity -> {
                        activity.setStudentDataInUI(loggedInUser)
                    }
                }
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is UpdateProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user details",
                    e
                )
            }
    }

    fun loadStudentOrTPOData(activity: Activity) {
        mFireStore.collection(Constants.TPO)
            // The document id to get the Fields of user.
            .document(FirebaseAuthentication().getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                if(document.exists()){
                    val loggedInTPO : TPO = document.toObject(TPO::class.java)!!
                    when(activity){
                        is SplashActivity ->{
                            activity.signInSuccessByTPO(loggedInTPO)
                        }
                        is SignInActivity ->{
                            activity.signInSuccessByTPO(loggedInTPO)
                        }
                    }
                }else{
                    Log.i("tag","student")
                    loadStudentData(activity)
                }
            }
            .addOnFailureListener { e ->
                // Here call a function of base activity for transferring the result to it.
                when(activity){
                    is SignInActivity ->{
                        activity.hideProgressDialog()                            }
                }
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting loggedIn user or admin details",
                    e
                )
            }
    }

    fun updateUserProfileData(activity: UpdateProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.STUDENTS) // Collection Name
            .document(FirebaseAuthentication().getCurrentUserID()) // Document ID
            .update(userHashMap) // A hashmap of fields which are to be updated.
            .addOnSuccessListener {
                // Profile data is updated successfully.
                Log.i(activity.javaClass.simpleName, "Profile Data updated successfully!")
                Toast.makeText(activity, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                // Notify the success result.
                activity.profileUpdateSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating profile.",
                    e
                )
            }
    }
    fun getCollegeCode(company : Company,tpoId:String,activity : NewCompanyDetailsActivity){
        mFireStore.collection(Constants.TPO)
            .document(tpoId)
            .get()
            .addOnSuccessListener { TPODocument->
                val collegeCode:String = TPODocument.toObject(TPO::class.java)!!.collegeCode
                addCompanyInCollege(company,collegeCode,activity)
            }
            .addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting college code.",
                    e
                )
            }
    }

    private fun addCompanyInCollege(company: Company, collegeCode : String, activity: NewCompanyDetailsActivity){
        var companyHashMap = HashMap<String,Company>()
        companyHashMap[company.name]=company

        mFireStore.collection(Constants.COLLEGES)
            .document(collegeCode)
            .collection(Constants.COMPANIES)
            .document(company.name)
            .set(company)
            .addOnSuccessListener {
                activity.companyRegisteredSuccess()
            }
            .addOnFailureListener {e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding company in college.",
                    e
                )
            }
    }
}