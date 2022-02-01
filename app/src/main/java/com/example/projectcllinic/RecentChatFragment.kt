package com.example.projectcllinic

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectcllinic.databinding.FragmentHomeBinding
import com.example.projectcllinic.databinding.FragmentRecentChatBinding
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase



class RecentChatFragment : Fragment() {
    private lateinit var binding : FragmentRecentChatBinding
    lateinit var recyclerView: RecyclerView
    lateinit var userRecyclerAdapter : UserRecyclerAdapter
    private lateinit var firebaseFirestore : FirebaseFirestore
    lateinit var userList: ArrayList<User>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecentChatBinding.inflate(layoutInflater)
        recyclerView = binding.recentHomeRv
        val layoutManager= LinearLayoutManager(context)
        layoutManager.orientation= LinearLayoutManager.VERTICAL
        recyclerView.layoutManager= layoutManager

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseFirestore = FirebaseFirestore.getInstance()
        recyclerView = binding.recentHomeRv
        val layoutManager= LinearLayoutManager(context)
        layoutManager.orientation= LinearLayoutManager.VERTICAL
        recyclerView.layoutManager= layoutManager
        initRecyclerView()
    }

    private fun initRecyclerView() {
        val query : Query = FirebaseFirestore.getInstance()
            .collection("users").whereEqualTo("flag",false).whereNotEqualTo("user_UID", FirebaseAuth.getInstance().currentUser?.uid.toString())
        val options : FirestoreRecyclerOptions<User> = FirestoreRecyclerOptions.Builder<User>()
            .setQuery(query,User::class.java)
            .build()
        userRecyclerAdapter= UserRecyclerAdapter(requireContext(),options)
        recyclerView.adapter= userRecyclerAdapter

        userRecyclerAdapter.startListening()
    }
}

