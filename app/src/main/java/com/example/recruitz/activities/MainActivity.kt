package com.example.recruitz.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recruitz.R
import com.example.recruitz.adapters.CompanyItemsAdapter
import com.example.recruitz.firebase.FirebaseAuthentication
import com.example.recruitz.firebase.Firestore
import com.example.recruitz.models.Company
import com.example.recruitz.models.Student
import com.example.recruitz.models.TPO
import com.example.recruitz.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    /** Stores if the Floating Action Button is open or closed */
    private var isFABOpen: Boolean = false

    /** Stores if the user is student or TPO */
    private var isStudent: Boolean = true

    /**A variable to store details of current student*/
    private lateinit var mStudent: Student

    /** A SharedPreference object points to a file containing key-value pairs */
    /** Here it is used to store Firebase Cloud Messaging Token in the device */
    private lateinit var mSharedPreferences: SharedPreferences

    /**The college code to which the student or tpo belongs*/
    lateinit var collegeCode: String

    /** A hashmap to store the last round till
    which the student has made progress for each company*/
    private var companyLastRoundHashMap: HashMap<String, Int> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        handleIntent(intent)
    }

    private fun clickListenerForBottomNavigationView() {
        /** Set OnClick listeners for bottom navigation view options */
        bottomNavigationView.setOnItemSelectedListener {
            /**Create array of company names to fetch data from database*/
            var companiesList: ArrayList<String> = ArrayList()
            if(isStudent){
                for ((companyName, lastRound) in mStudent.companiesListAndLastRound) {
                    companyLastRoundHashMap[companyName] = lastRound
                    companiesList.add(companyName)
                }
            }
            when(it.itemId){
                /** Shows list of Ongoing companies */
                R.id.bn_ongoing -> {
                    showProgressDialog(getString(R.string.please_wait))
                    if (isStudent) {
                        Firestore().getSpecificCompaniesDetailsFromDatabase(
                            companyNames = companiesList,
                            collegeCode = collegeCode, roundsOver = 0, this@MainActivity
                        )
                    } else {
                        Firestore().getAllCompaniesDetailsFromDatabase(
                            collegeCode = collegeCode,
                            roundsOver = 0,
                            this@MainActivity
                        )
                    }
                }

                /** Shows list of previous companies whose process is over */
                R.id.bn_previous -> {
                    showProgressDialog(getString(R.string.please_wait))
                    if (isStudent) {
                        Firestore().getSpecificCompaniesDetailsFromDatabase(
                            companyNames = companiesList,
                            collegeCode = collegeCode, roundsOver = 1, this@MainActivity
                        )
                    } else {
                        Firestore().getAllCompaniesDetailsFromDatabase(
                            collegeCode = collegeCode,
                            roundsOver = 1,
                            this@MainActivity
                        )
                    }
                }
            }
            true
        }
    }

    override fun onNewIntent(intent: Intent) {
        setIntent(intent)
        handleIntent(intent)
        super.onNewIntent(intent)
    }

    private fun setUpNavigationView() {
        setSupportActionBar(toolbar_main_activity)
        /** 3 white lines Icon in the upper left corner for opening drawer layout */
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
        /**Assign the NavigationView.OnNavigationItemSelectedListener to navigation view.*/
        nav_view.setNavigationItemSelectedListener(this)
    }

    private fun toggleDrawer() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    /** Depending on the user is student or tpo, this function will load the screen*/

    private fun handleIntent(intent: Intent) {
        when {
            intent.hasExtra(Constants.STUDENT_DETAILS) -> {
                isStudent = true
                mStudent = intent.getParcelableExtra<Student>(Constants.STUDENT_DETAILS)!!
                collegeCode = mStudent.collegeCode
                clickListenerForBottomNavigationView()
                setForStudent()
            }
            intent.hasExtra(Constants.TPO_DETAILS) -> {
                isStudent = false
                var tpo: TPO = intent.getParcelableExtra<TPO>(Constants.TPO_DETAILS)!!
                collegeCode = tpo.collegeCode
                clickListenerForBottomNavigationView()
                setForTPO(tpo)
            }
        }
    }

    /** Setup UI for TPO */
    private fun setForTPO(tpo: TPO) {

        /**Disable "My Profile" option for TPO*/

        //nav_view.menu(0).isVisible = false
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.getHeaderView(0).findViewById<TextView>(R.id.tv_username).text = "Hi ${tpo.firstName}"

        /** Floating Action Button listener*/
        fab.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }

        fab_add_new_company.setOnClickListener {
            val intent : Intent = Intent(this, NewCompanyDetailsActivity::class.java)
            intent.putExtra(Constants.COLLEGE_CODE,collegeCode)
            startActivity(intent)
            closeFABMenu()
        }

        fab_add_new_pr.setOnClickListener {
            val intent: Intent = Intent(this, AddPrActivity::class.java)
            intent.putExtra(Constants.COLLEGE_CODE, collegeCode)
            startActivity(intent)
            closeFABMenu()
        }

        fab_enable_or_disable_update_profile.setOnClickListener {
            val tvText =tv_enable_or_disable_update_profile.text.toString()
            if (tvText ==
                getString(R.string.disable_update_profile_button_fab))
                showAlertDialog(this,getString(R.string.disable_update_profile_button))
            else
                showAlertDialog(this,getString(R.string.enable_update_profile_button))
        }

        tv_block_view.setOnClickListener {
            closeFABMenu()
        }

        loadCompaniesForTPO()
    }

    private fun loadCompaniesForTPO() {
        /**Load TPO data to screen from database*/
        showProgressDialog(getString(R.string.please_wait))
        Firestore().getAllCompaniesDetailsFromDatabase(
            collegeCode,
            roundsOver = 0,
            this@MainActivity
        )
    }

    /** Setup UI for Student */

    private fun setForStudent() {
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.getHeaderView(0).findViewById<TextView>(R.id.tv_username).text =
            "Hi ${mStudent.firstName}"

        /**Floating Action Button is only visible to TPO*/
        fab.visibility = View.GONE
        fab_add_new_company.visibility = View.GONE
        fab_add_new_pr.visibility = View.GONE
        fab_enable_or_disable_update_profile.visibility = View.GONE

        loadCompaniesForStudent()
    }

    private fun updateStudentDetails(){
        showProgressDialog(getString(R.string.please_wait))
        Firestore().loadStudentData(this)
    }

    fun updateStudentDetailsSuccess(student: Student){
        mStudent = student
        hideProgressDialog()
        loadCompaniesForStudent()
    }

    private fun loadCompaniesForStudent() {
        /** To load student's data to screen */
        // Create array of companyNames to fetch data from database
        var companiesList: ArrayList<String> = ArrayList()

        for ((companyName, lastRound) in mStudent.companiesListAndLastRound) {
            companyLastRoundHashMap[companyName] = lastRound
            companiesList.add(companyName)
        }
        showProgressDialog(getString(R.string.please_wait))
        Firestore().getSpecificCompaniesDetailsFromDatabase(
            companiesList,
            collegeCode,
            roundsOver = 0,
            this@MainActivity
        )
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        val addItem: MenuItem = menu.findItem(R.id.add)
        addItem.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> {
                refresh()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun refresh(){
        if (isStudent) {
            updateStudentDetails()
        } else {
            loadCompaniesForTPO()
        }
    }

    private fun showFABMenu() {
        isFABOpen = true

        tv_block_view.visibility = View.VISIBLE
        fab.setImageResource(R.drawable.ic_wrong)
        fab.scaleType = ImageView.ScaleType.FIT_XY
        ll_add_new_company.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        ll_add_new_pr.animate().translationY(-resources.getDimension(R.dimen.standard_105))
//        ll_enable_or_disable_update_profile.animate().translationY(-resources.getDimension(R.dimen.standard_155))
        tv_add_new_company.visibility = View.VISIBLE
        tv_add_new_pr.visibility = View.VISIBLE
//        tv_enable_or_disable_update_profile.visibility = View.VISIBLE
    }

    private fun closeFABMenu() {
        isFABOpen = false

        tv_block_view.visibility = View.GONE
        fab.setImageResource(R.drawable.ic_add)
        fab.scaleType = ImageView.ScaleType.FIT_XY
        ll_add_new_company.animate().translationY(0F)
        ll_add_new_pr.animate().translationY(0F)
        ll_enable_or_disable_update_profile.animate().translationY(0F)

        tv_add_new_company.visibility = View.GONE
        tv_add_new_pr.visibility = View.GONE
        tv_enable_or_disable_update_profile.visibility = View.GONE
    }

    /** Shows items in recycler view */
    fun populateRecyclerView(companiesList: ArrayList<Company>) {
        hideProgressDialog()

        if (companiesList.size > 0) {
            /** Setting up recycler view */
            rv_companies_list.visibility = View.VISIBLE
            tv_no_companies_available.visibility = View.GONE

            rv_companies_list.layoutManager = LinearLayoutManager(this@MainActivity)
            rv_companies_list.setHasFixedSize(true)

            val adapter = CompanyItemsAdapter(this,companiesList)
            rv_companies_list.adapter = adapter

            /** On click listener for each item of recycler view */
            adapter.setOnClickListener(object : CompanyItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Company) {
                    val intent = Intent(this@MainActivity, RoundDetailsActivity::class.java)
                    if (isStudent)
                        intent.putExtra(Constants.STUDENT_DETAILS, mStudent)
                    intent.putExtra(Constants.COMPANY_DETAIL, model)
                    intent.putExtra(Constants.COLLEGE_CODE, collegeCode)
                    startActivity(intent)
                    refresh()
                }
            })
        }
        else {
            rv_companies_list.visibility = View.GONE
            tv_no_companies_available.visibility = View.VISIBLE
        }

        /** Check changes in Firebase Cloud Messaging token */
        if (isStudent) {
            checkFCMToken()
        }
    }
    private fun checkFCMToken(){
        /** Using shared preference in private mode so that other apps cannot access it */
        mSharedPreferences = this.getSharedPreferences(Constants.RECRUITZ_PREFERENCES, Context.MODE_PRIVATE)

        /** Variable is used get the value either token is updated in the database or not.*/
        val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)

        /** Here if the token is already updated than we don't need to update it every time. */
        /** If the token is not updated, get a new token and update it in database*/
        if (!tokenUpdated) {
            FirebaseMessaging.getInstance().token
            .addOnSuccessListener(this@MainActivity) { token ->
                updateFcmTokenInDatabase(token)
            }
        }
    }

    /** Update Firebase Cloud Messaging Token in Database */
    private fun updateFcmTokenInDatabase(token: String) {
        val studentHashMap = HashMap<String, Any>()
        studentHashMap[Constants.FCM_TOKEN] = token
        showProgressDialog(getString(R.string.please_wait))
        Firestore().updateStudentProfileData(this, studentHashMap)
    }

    /** Successfully Updated Firebase Cloud Messaging Token */

    fun tokenUpdateSuccess() {
        hideProgressDialog()
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
    }

    /** Listeners for each option inside drawer layout */
    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
                menuItem.isCheckable = false
                /** Takes user to UpdateProfile activity */
                startActivity(Intent(this, UpdateProfileActivity::class.java))
            }
            R.id.nav_sign_out -> {
                menuItem.isCheckable = false
                /**Here sign outs the user from firebase in this device.*/
                showAlertDialog(this,getString(R.string.sign_out_alert_text))
            }
        }

        /** Close the drawer*/
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /** clears the Firebase Cloud Messaging token stored in device */
    fun clearSharedPreferences() {
        if (isStudent) {
            mSharedPreferences.edit().clear().apply()
        }
    }

    fun updateCollegeSuccess(){
        hideProgressDialog()
    }
}