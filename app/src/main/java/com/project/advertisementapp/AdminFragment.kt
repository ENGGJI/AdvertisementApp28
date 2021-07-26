package com.project.advertisementapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import kotlin.math.log


class AdminFragment : Fragment() {
    lateinit var auth: FirebaseAuth
    private lateinit var newRecyclerview: RecyclerView
    val itemArrayList = mutableListOf<AdvertiseData>()
    lateinit var logoutButton : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



        auth = Firebase.auth
        logoutButton = view.findViewById(R.id.logoutAB)

        logoutButton.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_adminFragment_to_authenticationFragment)
        }

        newRecyclerview = view.findViewById(R.id.RecyclerViewAdmin)
        newRecyclerview.layoutManager = LinearLayoutManager(context)
        newRecyclerview.setHasFixedSize(true)

        getAdData()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun getAdData() {
        val ref = FirebaseDatabase.getInstance().getReference("AddDetails").child("Details")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Admin fragment", "Got : ${snapshot.value}")
                if(snapshot.exists()){
                    itemArrayList.clear()
                    for(itemSnapshot in snapshot.children){
                        val allUserItems = itemSnapshot.getValue(AdvertiseData::class.java)
                        if(allUserItems?.verified == "Not Verified"){
                            itemArrayList.add(allUserItems)
                        }
                        else{
                            Toast.makeText(context,"No More Post's Found", Toast.LENGTH_LONG).show()
                        }
                    }
                }
                newRecyclerview.adapter = AdminAdapter(itemArrayList as ArrayList<AdvertiseData>)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}