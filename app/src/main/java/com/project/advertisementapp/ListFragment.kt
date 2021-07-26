package com.project.advertisementapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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

class ListFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    private lateinit var newRecyclerview: RecyclerView
    val itemArrayList = mutableListOf<AdvertiseData>()
    val myArrayList = mutableListOf<AdvertiseData>()
    var listForItemID = mutableListOf<String>()
    var searchItemArrayList = mutableListOf<AdvertiseData>()
    lateinit var seachviewis : SearchView
    var newTextInSearchBar =""
    var flag =0

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
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // Add post button click listener
        val b = view.findViewById<BottomNavigationItemView>(R.id.addpost)
        b.setOnClickListener {
            // execute action
            findNavController().navigate(R.id.action_listFragment_to_addPostFragment)
        }

        // Accounts button click listener
        val a = view.findViewById<BottomNavigationItemView>(R.id.accounts)
        a.setOnClickListener {
            // execute action
            findNavController().navigate(R.id.action_listFragment_to_accountsFragment)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        auth = Firebase.auth

        seachviewis = view.findViewById(R.id.userSeachView)

        newRecyclerview = view.findViewById(R.id.recyclerView)
        newRecyclerview.layoutManager = LinearLayoutManager(context)
        newRecyclerview.setHasFixedSize(true)

        getAdData()

        seachviewis.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query : String?) : Boolean {
                return false
            }
            override fun onQueryTextChange(newText : String?) : Boolean {
                Log.d("UserActivity", "enterText is $newText")
                newTextInSearchBar = newText!!
                if (flag == 1) {
                    Log.d("Iamstupid" , " REached till here True searchHotelArrayList , ${searchItemArrayList.size}")
                    getAdData()
                }
                else if(newTextInSearchBar.length > 2){
                    flag = 1
                    getAdData()
                    Log.d("Iamstupid" , " REached till here True searchHotelArrayList , ${newTextInSearchBar}")
                }
                return false
            }
        })

        // My adds button click listener

        val i = view.findViewById<BottomNavigationItemView>(R.id.myadds)
        i.setOnClickListener {
            // execute action
            val ref = FirebaseDatabase.getInstance().getReference("AddDetails").child("Details")

            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("ListFragment", "Got : ${snapshot.value}")
                    if(snapshot.exists()){
                        itemArrayList.clear()
                        for(itemSnapshot in snapshot.children){
                            val item = itemSnapshot.getValue(AdvertiseData::class.java)
                            myArrayList.add(item!!)
                            Log.d("ListFrag" , " ${item.userID}")
                            if(auth.currentUser?.uid == item.userID){
                                listForItemID.add(item.itemId!!)
                            }
                        }
                        Log.d("ListFragment", "Getting the List Item ID's : $listForItemID")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

            //passing the list
            val itemListBundle = bundleOf("id" to listForItemID)
            findNavController().navigate(R.id.action_listFragment_to_myAddsFragment , itemListBundle)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getAdData() {
        itemArrayList.clear()
        searchItemArrayList.clear()

        val ref = FirebaseDatabase.getInstance().getReference("AddDetails").child("Details")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("ListFragment", "Got : ${snapshot.value}")
                if(snapshot.exists()){
                    for(itemSnapshot in snapshot.children){
                        val allUserItems = itemSnapshot.getValue(AdvertiseData::class.java)
                        if("${allUserItems?.itemName}".lowercase().contains(newTextInSearchBar.toLowerCase()) && allUserItems?.verified == "Verified"){
                            Log.d("ListFragment abc","${allUserItems.itemName.toString()}, $newTextInSearchBar")
                            searchItemArrayList.add(allUserItems)
                            Log.d("Iamstupid" , " REached till here True searchHotelArrayList , $searchItemArrayList")
                        }
                        else{
                            if(allUserItems?.verified == "Verified"){
                                itemArrayList.add(allUserItems)
                            }
                        }
                    }
                }
                if(searchItemArrayList.size != 0){
                    newRecyclerview.adapter = ListAdapter(searchItemArrayList as ArrayList<AdvertiseData>)
                }
                else {
                    newRecyclerview.adapter = ListAdapter(itemArrayList as ArrayList<AdvertiseData>)
                    flag =0
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}
