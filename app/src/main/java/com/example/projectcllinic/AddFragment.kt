package com.example.projectcllinic

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.projectcllinic.databinding.FragmentAddBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class AddFragment : Fragment() {

    private lateinit var binding: FragmentAddBinding
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val db = FirebaseFirestore.getInstance()
        val docRef: DocumentReference = db.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid!!.toString())
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null) {

                    val cname = document.getString("ClinicName")
                    val cadd = document.getString("ClinicAddress")
                    val ctim = document.getString("ClinicTiming")
                    val cdes = document.getString("ClinicDescription")

                    binding.nameAdd.setText(cname)
                    binding.addressAdd.setText(cadd)
                    binding.timingAdd.setText(ctim)
                    binding.descriptionAdd.setText(cdes)
                    Log.d("LOGGER", "$cname ")
                }
            } else {
                Log.d("LOGGER", "get failed with ", task.exception)
            }


        binding.savebuttonAdd.setOnClickListener {




                val Cname = binding.nameAdd.text.toString()
                val Caddress = binding.addressAdd.text.toString()
                val Ctiming = binding.timingAdd.text.toString()
                val Cdescription = binding.descriptionAdd.text.toString()
                val db = FirebaseFirestore.getInstance()
                val user: MutableMap<String, Any> = HashMap()
                user["ClinicName"] = Cname
                user["ClinicAddress"] = Caddress
                user["ClinicTiming"] = Ctiming
                user["ClinicDescription"] = Cdescription
                user["flag"] = true
                if (Cname.isEmpty()) {
                    binding.nameAdd.error = "Clinic name required!!!"
                    binding.nameAdd.requestFocus()
                } else if (Caddress.isEmpty()) {
                    binding.addressAdd.error = "Clinic address required!!!"
                    binding.addressAdd.requestFocus()
                } else if (Ctiming.isEmpty()) {
                    binding.timingAdd.error = "Clinic timing required!!!"
                    binding.timingAdd.requestFocus()
                } else if (Cdescription.isEmpty()) {
                    binding.descriptionAdd.error = "Clinic description required!!!"
                    binding.descriptionAdd.requestFocus()
                } else {

                    Firebase.auth.uid?.let { it1 ->
                        db.collection("users").document(it1).set(
                            user,
                            SetOptions.merge()
                        )
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Data uploaded successfully ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(
                                    context,
                                    "Data uploading failed ",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()

                            }
                        startActivity(Intent(context, MainActivity::class.java))


                    }
                }

            }

        }
    }
}
