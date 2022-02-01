package com.example.projectcllinic
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.nfc.tech.TagTechnology
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.example.projectcllinic.databinding.LatestMessageRowBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.core.Tag
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.File


class UserRecyclerAdapter (private var context : Context,options : FirestoreRecyclerOptions<User>):
        FirestoreRecyclerAdapter<User, UserRecyclerAdapter.UserViewHolder>(options){


            inner class UserViewHolder(val binding: LatestMessageRowBinding): RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val bind = LatestMessageRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return UserViewHolder(bind)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position : Int, model : User) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        //holder.binding.imageRv.setImageResource(model.image)
        val storageref = FirebaseStorage.getInstance().reference.child("Users/$uid.jpg")
        val localFile = File.createTempFile("temp","jpg")
        storageref.getFile(localFile)
        val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
        //holder.binding.imageRv.setImageBitmap(bitmap)
        if(model.Profile_Pic!=null){
                Picasso.get().load(model.Profile_Pic).error(R.drawable.profile_pic)
                    .into(holder.binding.imageRv)

            }
       // mContext?.let { Glide.with(it).load(model.Profile_Pic).into(holder.binding.imageRv) }
        if (model.ClinicName==""){
            holder.binding.nameRv.text=model.Name
            holder.binding.addressRv.text=model.Email
            holder.binding.root.setOnClickListener {
                val it = Intent(context,MainActivity3::class.java)
                it.putExtra("name",model.Name)
                it.putExtra("number",model.Number)
                it.putExtra("uid",model.user_UID)

                context.startActivity(it)

            }
        }
        else{
            holder.binding.nameRv.text=model.ClinicName
            holder.binding.addressRv.text=model.ClinicAddress
            holder.binding.descriptionRv.text=model.ClinicDescription
            holder.binding.timingRv.text=model.ClinicTiming
            holder.binding.root.setOnClickListener {
                val it = Intent(context,MainActivity3::class.java)
                it.putExtra("name",model.ClinicName)
                it.putExtra("uid",model.user_UID)
                it.putExtra("number",model.Number)
                context.startActivity(it)

            }
        }


    }
}

