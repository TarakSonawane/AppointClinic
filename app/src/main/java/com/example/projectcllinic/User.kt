package com.example.projectcllinic

import android.os.Parcelable
import android.widget.ImageView
import androidx.versionedparcelable.ParcelField
import androidx.versionedparcelable.VersionedParcelize


data class User (
    var ClinicName : String?=null,
    var ClinicAddress : String?=null,
    var ClinicTiming : String?=null,
    var ClinicDescription : String?=null,
    var Name : String?= null,
    var Profile_Pic:String?=null,
    var Number : String?=null,
    var Email : String?=null,
    var user_UID :String?=null)
