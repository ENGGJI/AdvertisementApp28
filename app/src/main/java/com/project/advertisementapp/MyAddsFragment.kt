package com.project.advertisementapp

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class MyAddsFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    private lateinit var adRecyclerView : RecyclerView
    var adItemList = mutableListOf<AdvertiseData>()
    var itemId :String =""
    var listOfItemList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        arguments?.let {
            listOfItemList = it.getStringArrayList("id")!!
            Log.d("MyAddsFragment" , "Item list Received  : $listOfItemList")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_adds, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth
        adRecyclerView = view.findViewById(R.id.recyclerViewMyAdd)
        adRecyclerView.layoutManager = LinearLayoutManager(context)
        adRecyclerView.setHasFixedSize(true)

        getAdData()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getAdData(){
        val ref = FirebaseDatabase.getInstance().getReference("AddDetails").child("Details")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                Log.d("myAddsFragment", "Get snapshot value : ${snapshot.value}")

                if(snapshot.exists()){
                    adItemList.clear()
                    for (item in snapshot.children){
                        val itemIs = item.getValue(AdvertiseData::class.java)
                        for ( i  in listOfItemList){
                            Log.d("MyAddFragment" , i)
                            if(i == itemIs?.itemId){
                                adItemList.add(itemIs)
                            }
                        }
                    }
                }
                adRecyclerView.adapter = MyAdAdapter(adItemList as ArrayList<AdvertiseData>)
                listOfItemList.clear()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}




