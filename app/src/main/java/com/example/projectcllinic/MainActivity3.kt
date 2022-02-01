package com.example.projectcllinic

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.common.io.Files.append
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import org.jetbrains.anko.makeCall

class MainActivity3 : AppCompatActivity() {

    private lateinit var chatRecyclerView : RecyclerView
    private lateinit var messageBox : EditText
    private lateinit var sendButton : ImageView
    private lateinit var userAdapter : RecyclerView
    private lateinit var messageList: ArrayList<MessageModal>
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var mDbRef : DatabaseReference

    var receiverRoom:String?= null
    var senderRoom:String?= null
    var receivernumber:String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        val name = intent.getStringExtra("name")
        val receiverUid = intent.getStringExtra("uid")
        receivernumber = intent.getStringExtra("number")
        Log.d("JAI ", "$receivernumber ")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid
        supportActionBar?.title=name
        chatRecyclerView = findViewById(R.id.messageRecyclerView)
        messageBox = findViewById(R.id.etSendMessage)
        sendButton= findViewById(R.id.btSendMessage)
        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)
        chatRecyclerView.adapter=messageAdapter

        // logic for adding data to recycler view

        mDbRef.child("chats").child(senderRoom!!).child("messages").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for(postSnapshot in snapshot.children){
                    val message = postSnapshot.getValue(MessageModal::class.java)
                    messageList.add(message!!)
                }
                messageAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        // adding messsage to database
        sendButton.setOnClickListener {
      val message = messageBox.text.toString()
            if(message==""){
                messageBox.error="Message can not be empty!!!"
                messageBox.requestFocus()
           }
            else {
                val messageObject = MessageModal(message, senderUid)

                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                messageBox.setText("")
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_callmenu,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.nav_call -> {
                val mobile = receivernumber.toString().trim()
                val dialIntent = Intent(Intent.ACTION_DIAL)
                dialIntent.data = Uri.parse("tel:"+"$mobile")
                startActivity(dialIntent)
            }
        }
        return true
    }
}
