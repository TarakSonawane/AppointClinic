package com.example.projectcllinic

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.projectcllinic.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    //view binding
    private lateinit var binding: ActivityLoginBinding
    //Action Bar
    private lateinit var actionBar: ActionBar

    private lateinit var progressDialog: ProgressDialog

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fstore: FirebaseFirestore
    private lateinit var db: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog= ProgressDialog(this)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Logging In...")
        progressDialog.setCanceledOnTouchOutside(false)

        //init firebase
        firebaseAuth= FirebaseAuth.getInstance()
        checkuser()



        //handle click open signup activity
        binding.reg.setOnClickListener{

            startActivity(Intent(this,RegistrationActivity::class.java))
        }
        binding.login.setOnClickListener{
            validateData()

        }

    }

    private fun validateData() {
        var name=binding.username.text.toString().trim()
        var pass=binding.password.text.toString().trim()
        if(name.isEmpty()){
            binding.username.error="Email Required!!!"
            binding.username.requestFocus()
        }
        else if(pass.isEmpty()){
            binding.password.error="Password Required!!!"
            binding.password.requestFocus()
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(name).matches()){
            binding.username.error="Invalid Email Format"
            binding.username.requestFocus()

        }
        else {
            firebaselogin()
        }
    }

    private fun firebaselogin() {
        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(binding.username.text.toString(),binding.password.text.toString())
            .addOnSuccessListener{
                progressDialog.dismiss()
                val firebaseUser=firebaseAuth.currentUser
                val email=firebaseUser!!.email
                val intent = (Intent(this@LoginActivity,MainActivity::class.java))
                startActivity(intent)
            }
            .addOnFailureListener{e->
                progressDialog.dismiss()
                Toast.makeText(this,"Log In Failed Due To${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun checkuser() {
        val firebaseUser=firebaseAuth.currentUser
        if(firebaseUser!=null){

            val intent = (Intent(this@LoginActivity,MainActivity::class.java))
            startActivity(intent)

        }
    }
    override fun onBackPressed() {
        //  super.onBackPressed();
        moveTaskToBack(true)
    }
}