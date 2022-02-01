package com.example.projectcllinic

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.projectcllinic.databinding.ActivityMainBinding
import com.example.projectcllinic.databinding.ActivityProfileUpdateBinding
import com.example.projectcllinic.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*
import com.google.firebase.firestore.DocumentSnapshot

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnCompleteListener

import com.google.firebase.firestore.DocumentReference
import com.squareup.picasso.Picasso


class profileUpdate : AppCompatActivity() {

    private lateinit var actionBar: ActionBar
    private lateinit var progressDialog: ProgressDialog
    private lateinit var fstore: FirebaseFirestore
    private lateinit var binding : ActivityProfileUpdateBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var imageUri: Uri
    private lateinit var dialog: Dialog

    companion object {
        val IMAGE_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.profileimage.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type="image/*"
            startActivityForResult(intent,0)
        }


        val uid = firebaseAuth.currentUser?.uid

        val db = FirebaseFirestore.getInstance()
        val docRef: DocumentReference = db.collection("users").document(uid!!)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {


                    val image_url = document.getString("Profile_Pic")
                    if(image_url!=null){
                        Picasso.get().load(image_url).error(R.drawable.profile_pic)
                            .into(binding.profileimage)

                    }
                    val name = document.getString("Name")
                    val number = document.getString("Number")
                    binding.etUserName.setText(name)
                    binding.etMobileNumber.setText(number.toString())

                }
            } else {
                Log.d("LOGGER", "get failed with ", task.exception)
            }
        }



        binding.saveBtn.setOnClickListener {

            val username = binding.etUserName.text.toString()
            val mobilenumber = binding.etMobileNumber.text.toString()


            if (username.isEmpty()) {
                binding.etUserName.error = "Username required!!!"
                binding.etUserName.requestFocus()
            } else if (mobilenumber.isEmpty()) {
                binding.etMobileNumber.error = "Phone number required!!"
                binding.etMobileNumber.requestFocus()
            }
            else if(mobilenumber.length<10){
                binding.etMobileNumber.error="Number must be of at least 10 digits!!!"
                binding.etMobileNumber.requestFocus()
            }else {
                showProgressBar()
                uploadProfilePic()
                val db = FirebaseFirestore.getInstance()
                val user:MutableMap<String,Any> = HashMap()
                user["Name"]=binding.etUserName.text.toString()
                user["Number"]=binding.etMobileNumber.text.toString()

                Firebase.auth.uid?.let { it1 ->
                    db.collection("users").document(it1).set(user,
                        SetOptions.merge())
                }
                hideProgressBar()
                Toast.makeText(this,"Profile updated successfully ",Toast.LENGTH_SHORT).show()
                startActivity(Intent(this,MainActivity::class.java))

            }


        }
    }

    var selectedPhotoUri: Uri?=null
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==0 && resultCode== Activity.RESULT_OK && data!= null){
            selectedPhotoUri =data.data
            val bitmap= MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            binding.profileimage.setImageBitmap(bitmap)
        }

    }
    private fun uploadProfilePic(){

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


    private fun showProgressBar(){

        dialog = Dialog( this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_wait)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()


    }
    private fun hideProgressBar(){
        dialog.dismiss()
    }
}
