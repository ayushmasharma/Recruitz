package com.example.recruitz.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlin.collections.ArrayList

@Parcelize
data class Company(
    var name: String = "",
    var cgpaCutOff : Double = 0.0,
    var backLogsAllowed : Int =0,
    var branchesAllowed : ArrayList<String> = ArrayList(),
    var ctcDetails : String ="",
    var location : String="",
    var deadlineToApply : Long = 0,
    var jobProfile : String ="",
    var roundsList : ArrayList<Round> = ArrayList(),
    var roundsOver : Int=0
) : Parcelable