package com.example.recruitz.activities
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recruitz.R
import com.example.recruitz.adapters.CompanyItemsAdapter
import com.example.recruitz.firebase.Firestore
import com.example.recruitz.models.Company
import com.example.recruitz.models.Student
import com.example.recruitz.models.TPO
import com.example.recruitz.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var isFABOpen = false
    private var isStudent = true

    private lateinit var mTPO: TPO
    private lateinit var mStudent: Student
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var collegeCode : String
    private var companyLastRoundHashMap : HashMap<String,Int> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        Log.i("main","oncreate")
        handleIntent(intent)

        rb_ongoing.setOnClickListener {
            tv_no_companies_available.text = "Ongoing"
        }
        rb_previous.setOnClickListener {
            tv_no_companies_available.text = "Previous"
        }
        rb_upcoming.setOnClickListener {
            tv_no_companies_available.text = "Upcoming"
        }
    }

    private fun showFABMenu() {
        isFABOpen = true

        tv_block_view.visibility = View.VISIBLE

        fab.setImageResource(R.drawable.ic_wrong)
        fab.scaleType = ImageView.ScaleType.FIT_XY

        ll_add_new_company.animate().translationY(-resources.getDimension(R.dimen.standard_55))

        tv_add_new_company.visibility= View.VISIBLE
    }

    private fun closeFABMenu() {
        isFABOpen = false

        tv_block_view.visibility = View.GONE

        fab.setImageResource(R.drawable.ic_fab)
        fab.scaleType = ImageView.ScaleType.FIT_XY

        ll_add_new_company.animate().translationY(0F)
        tv_add_new_company.visibility= View.GONE

    }

    private fun toggleDrawer() {

        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    override fun onNewIntent(intent: Intent) {
        Log.i("main","onnew")
        setIntent(intent)
        handleIntent(intent)
        super.onNewIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        when {
            intent.hasExtra(Constants.STUDENT_DETAILS) -> {
                isStudent = true
                mStudent = intent.getParcelableExtra<Student>(Constants.STUDENT_DETAILS)!!
                collegeCode = mStudent.collegeCode
                setForStudent()

                // Create array of companyNames to fetch data from database
                var companiesList : ArrayList<String> = ArrayList()

                for((companyName,lastRound) in mStudent.companiesListAndLastRound){
                    companyLastRoundHashMap[companyName]=lastRound
                    companiesList.add(companyName)
                }
                showProgressDialog(getString(R.string.please_wait))
                Firestore().getCompaniesListFromDatabase(companiesList,collegeCode,this@MainActivity)

            }
            intent.hasExtra(Constants.TPO_DETAILS) -> {
                isStudent = false
                mTPO = intent.getParcelableExtra<TPO>(Constants.TPO_DETAILS)!!
                collegeCode = mTPO.collegeCode
                setForTPO()
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setForTPO() {
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }

//        nav_view.menu(0).isVisible = false
        nav_view.setNavigationItemSelectedListener(this)
        nav_view.getHeaderView(0).findViewById<TextView>(R.id.tv_username).text =
            "Hi ${mTPO.firstName}"

        //TODO get list of companies

        //TODO FAB listener
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

        tv_block_view.setOnClickListener {
            closeFABMenu()
        }
    }

    private fun setForStudent() {
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }


        nav_view.setNavigationItemSelectedListener(this)
        nav_view.getHeaderView(0).findViewById<TextView>(R.id.tv_username).text =
            "Hi ${mStudent.firstName}"

        fab.visibility = View.GONE
        fab_add_new_company.visibility = View.GONE
    }

    fun populateRecyclerView(companiesList: ArrayList<Company>) {
        Log.i("rv","yes")
        hideProgressDialog()

        if (companiesList.size > 0) {
            rv_companies_list.visibility = View.VISIBLE
            tv_no_companies_available.visibility = View.GONE

            rv_companies_list.layoutManager = LinearLayoutManager(this@MainActivity)
            rv_companies_list.setHasFixedSize(true)

            val adapter = CompanyItemsAdapter(this,companiesList)
            rv_companies_list.adapter = adapter
            adapter.setOnClickListener(object : CompanyItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Company) {
                    val intent = Intent(this@MainActivity, RoundDetailsActivity::class.java)
                    startActivity(intent)
                }
            })
        }
        else {
            rv_companies_list.visibility = View.GONE
            tv_no_companies_available.visibility = View.VISIBLE
        }

        if(isStudent){
            mSharedPreferences =
                this.getSharedPreferences(Constants.RECRUITZ_PREFERENCES, Context.MODE_PRIVATE)

            val tokenUpdated = mSharedPreferences.getBoolean(Constants.FCM_TOKEN_UPDATED, false)

            if (tokenUpdated) {
                Log.i("no","no")
                showProgressDialog(getString(R.string.please_wait))
                Firestore().loadStudentData(this)

            }
            else {
                FirebaseMessaging.getInstance().token
                    .addOnSuccessListener(this@MainActivity) { token ->
                        updateFcmTokenInDatabase(token)
                    }
            }
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_my_profile -> {
                startActivity(Intent(this, UpdateProfileActivity::class.java))
                menuItem.isChecked=false
            }
            R.id.nav_sign_out -> {
                showAlertDialog(this@MainActivity, getString(R.string.sign_out_alert_text))
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun tokenUpdateSuccess() {
        hideProgressDialog()
        val editor: SharedPreferences.Editor = mSharedPreferences.edit()
        editor.putBoolean(Constants.FCM_TOKEN_UPDATED, true)
        editor.apply()
        showProgressDialog(getString(R.string.please_wait))
        Firestore().loadStudentData(this)
    }

    private fun updateFcmTokenInDatabase(token: String) {
        val studentHashMap = HashMap<String, Any>()
        studentHashMap[Constants.FCM_TOKEN] = token

        showProgressDialog(getString(R.string.please_wait))
        Firestore().updateStudentProfileData(this, studentHashMap)
    }

    fun clearSharedPreferences(){
        if(isStudent)  mSharedPreferences.edit().clear().apply()
    }

    fun loadStudentDataSuccess(student : Student){
        Log.i("main","lsds")
        hideProgressDialog()
    }
}