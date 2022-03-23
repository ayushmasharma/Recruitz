package com.example.recruitz.utils

object Constants {
    // Firebase Constants
    // This  is used for the collection names used in firestore

    const val STUDENTS: String = "students"
    const val COLLEGES : String = "colleges"
    const val TPO : String = "tpo"
    const val COMPANIES : String = "companies"

    // Firebase database field names
    const val ID  = "id"
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val COLLEGE_CODE :String = "collegeCode"
    const val COLLEGE_NAME = "collegeName"
    const val BRANCH : String ="branch"
    const val CGPA : String="cgpa"
    const val ROLL_NUMBER ="rollNumber"
    const val BACKLOGS_ALLOWED = "backLogsAllowed"
    const val PLACED_ABOVE_THRESHOLD ="placedAboveThreshold"
    const val LAST_ROUND ="lastRound"
    const val NAME="name"
    const val EMAIL ="email"
    const val COMPANY_NAME_AND_LAST_ROUND = "companiesListAndLastRound"
    const val NUMBER_OF_BACKLOGS = "numberOfBacklogs"
    const val ROUNDS_OVER = "roundsOver"

    const val STUDENT_DETAILS : String ="studentDetails"
    const val TPO_DETAILS : String ="tpoDetails"
    const val IS_STUDENT: String = "isStudent"
    const val RECRUITZ_PREFERENCES = "RecruitzPreferences"
    const val FCM_TOKEN_UPDATED = "fcmTokenUpdated"
    const val FCM_TOKEN = "fcmToken"

    const val FCM_BASE_URL: String = "https://fcm.googleapis.com/fcm/send"
    const val FCM_AUTHORIZATION: String = "authorization"
    const val FCM_KEY: String = "key"
    const val FCM_SERVER_KEY: String = "AAAAgLp3ENk:APA91bFiYb1G-yM8wNY1q-S6SG_pWAntsKixp77FJJaoS8LeJizKymvMslo1PACgSpEia-JBlFLuBktXUEiMqCchAheTp9IOZsR_XTCHt7JuLtLrli1zXycsChx-n-kt805Cy-B7NQKA"
    const val FCM_KEY_TITLE: String = "title"
    const val FCM_KEY_MESSAGE: String = "message"
    const val FCM_KEY_DATA: String = "data"
    const val FCM_KEY_TO: String = "to"
    const val POST: String = "POST"

    // Branches list
    val ALL_BRANCHES = arrayOf(
        "Computer Science", "Information Technology", "Electronics and Communication", "Electrical",
        "Mechanical", "Chemical"
    )}