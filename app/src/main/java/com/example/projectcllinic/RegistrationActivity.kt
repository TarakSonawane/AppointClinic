package com.example.projectcllinic

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.projectcllinic.databinding.ActivityRegistrationBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.Tag
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*
import java.util.jar.Attributes
import kotlin.collections.HashMap

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    private lateinit var actionBar: ActionBar

    private lateinit var progressDialog: ProgressDialog

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var storageReference: StorageReference

    private lateinit var fstore: FirebaseFirestore



    private lateinit var imageuri : Uri

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding= ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        actionBar=supportActionBar!!
        actionBar.title="Sign Up"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setDisplayShowHomeEnabled(true)

        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Creating An Account")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth= FirebaseAuth.getInstance()

        binding.register.setOnClickListener{
            validateregis()
        }
        binding.profilePic.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }
    }
    var selectedPhotoUri: Uri?=null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode==Activity.RESULT_OK && data!= null){
            selectedPhotoUri =data.data
            val bitmap=MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            binding.profilePic.setImageBitmap(bitmap)
        }

    }
    private fun validateregis() {
        var email=binding.email.text.toString().trim()
        var name=binding.username.text.toString().trim()
        var number=binding.number.text.toString().trim()
        var pass1=binding.password.text.toString().trim()
        var pass2=binding.password2.text.toString().trim()
        if(name.isEmpty()){
            binding.username.error="Username required!!!"
            binding.username.requestFocus()
        }
        else if(email.isEmpty()){
            binding.email.error="Email required!!!"
            binding.email.requestFocus()
        }
        else if(number.isEmpty()){
            binding.number.error="Phone number required!!!"
            binding.number.requestFocus()
        }
        else if(pass1.isEmpty()){
            binding.password.error="Password required!!!"
            binding.password.requestFocus()
        }
        else if(pass2.isEmpty()){
            binding.password2.error="Password required!!!"
            binding.password2.requestFocus()
        }
        else if(pass1!=pass2){
            binding.password2.error="Password mismatched!!!"
            binding.password2.requestFocus()
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.email.error="Invalid email format!!!"
            binding.email.requestFocus()
        }
        else if(pass1.length<6){
            binding.password.error="Password must be at least 6 characters long!!!"
            binding.password.requestFocus()
        }
        else if(number.length<10){
            binding.number.error="Number must be of at least 10 digits!!!"
            binding.number.requestFocus()
        }
        else{
            firebaseSignUp()
        }

    }

    private fun firebaseSignUp() {
        progressDialog.show()

        //create account
        firebaseAuth.createUserWithEmailAndPassword(binding.email.text.toString(),binding.password.text.toString())
            .addOnSuccessListener {

                progressDialog.dismiss()
                uploadImageToFirebaseStorage()
                val db = FirebaseFirestore.getInstance()
                val user:MutableMap<String,Any> = HashMap()
                user["Name"]=binding.username.text.toString()
                user["Email"]=binding.email.text.toString()
                user["Number"]=binding.number.text.toString()
                user["ClinicName"]= ""
                user["ClinicAddress"]= ""
                user["ClinicTiming"]= ""
                user["ClinicDescription"]= ""
                user["user_UID"]= FirebaseAuth.getInstance().currentUser?.uid.toString()
                user["flag"]=false

                Firebase.auth.uid?.let { it1 ->
                    db.collection("users").document(it1).set(user,
                        SetOptions.merge())
                }
                Toast.makeText(this,"Created Account Successfully !!! ", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MainActivity::class.java))
                finish()

            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this,"Registration Failed Due To ${e.message} !!! ", Toast.LENGTH_SHORT).show()

            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("Tag", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    saveUserToFirebaseDatabase(it.toString())
                    Log.d("Tag", "File Location: $it")
                }
            }
            .addOnFailureListener {
                Log.d("Tag", "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val user:MutableMap<String,Any> = HashMap()
        user["Profile_Pic"]=profileImageUrl
        val db = FirebaseFirestore.getInstance()
        Firebase.auth.uid?.let { it1 ->
            db.collection("users").document(it1).set(user,
                SetOptions.merge())
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
