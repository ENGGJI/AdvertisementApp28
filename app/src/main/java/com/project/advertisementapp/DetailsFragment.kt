package com.project.advertisementapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.renderscript.Sampler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import kotlin.concurrent.thread

class DetailsFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var itemRef : DatabaseReference
    lateinit var userRef: DatabaseReference
    lateinit var subRef: DatabaseReference

    lateinit var itemImageUrl: ImageView
    lateinit var itemName: TextView
    lateinit var itemPrice: TextView
    lateinit var userContact: TextView
    lateinit var postedByUser: TextView
    lateinit var itemDescription : TextView

    lateinit var imageButton: ImageView
    lateinit var message : TextView

    var gotItemId = ""
    var gotUserId =""
    var userSubbedList = mutableListOf<String>()
    var subCount =0

    var itemList = mutableListOf<AdvertiseData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gotItemId = it.getString("itemId")!!
            Log.d("DetailsFragment","Current itemId fetched : $gotItemId")
            gotUserId = it.getString("userId")!!
            Log.d("DetailsFragment","Current userId fetched : $gotUserId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth

        itemImageUrl = view.findViewById(R.id.itemImage)
        itemName = view.findViewById(R.id.itemName)
        itemPrice = view.findViewById(R.id.itemPrice)
        itemDescription = view.findViewById(R.id.itemDescription)
        postedByUser = view.findViewById(R.id.postedByUser)
        userContact = view.findViewById(R.id.userContact)

        message = view.findViewById(R.id.messageTv)
        imageButton = view.findViewById(R.id.subscribeB)

        val db = FirebaseDatabase.getInstance()
        itemRef = db.reference.child("AddDetails").child("Details")
        userRef = FirebaseDatabase.getInstance().reference.child("UserData").child(gotUserId)

        //fetching the details of subbed users
        subRef = FirebaseDatabase.getInstance().getReference("SubscriberData").child(auth.currentUser?.uid.toString())
        subRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    subCount = snapshot.childrenCount.toInt()
                    Log.d("Details Fragment", "SubCount value : $subCount")
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        if (auth.currentUser?.uid != gotUserId) {
            for (i in 1..(subCount + 1)) {
                val subscriberData = SubscriberData(gotUserId)
                val ref = FirebaseDatabase.getInstance().getReference("SubscriberData").child(auth.currentUser?.uid.toString())

                message.visibility = View.VISIBLE
                imageButton.visibility = View.VISIBLE

                imageButton.setOnClickListener {
                    subCount += 1
                    ref.child(subCount.toString()).setValue(subscriberData)

                    val subRef = FirebaseDatabase.getInstance().getReference("SubscriberData")
                    subRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (userAd in snapshot.children){
                                    if (userAd.key == auth.currentUser?.uid.toString()) {
                                        for (i in 1..(subCount+1)) {
                                            Log.d("DetailsFragment", snapshot.child(auth.currentUser?.uid.toString()).child(i.toString()).child("subscriberUserID").value.toString())
                                            val subbedUserId = subRef.child(auth.currentUser?.uid.toString()).child(i.toString())
                                            subbedUserId.addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if (snapshot.exists()) {
                                                        for (user in snapshot.children) {
                                                            userSubbedList.add(snapshot.child("subscriberID").value.toString())
                                                            Log.d("DetailsFragment", "Subscriber List : $userSubbedList")
                                                        }
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    TODO("Not yet implemented")
                                                }
                                            })
                                        }
                                    }
                                }
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                    })

                    Toast.makeText(context,"User has been Subscribed",Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_detailsFragment_to_listFragment)
                }
            }
        }
        else{
            message.visibility = View.INVISIBLE
            imageButton.visibility = View.INVISIBLE
        }

        getData()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun getData() {
        itemRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(itemSnapshot in snapshot.children){
                        val item = itemSnapshot.getValue(AdvertiseData::class.java)
                        if (item != null) {
                            itemList.add(item)
                        }
                        Log.d("DetailsFragment","ItemId from advertised data : ${item?.itemId}")
                        if(item?.itemId == gotItemId ){
                            val imageUrl = item.itemImageUrl
                            context?.let { Glide.with(it).load(imageUrl).into(itemImageUrl) }
                            itemName.text = item.itemName
                            itemPrice.text = item.itemPrice
                            itemDescription.text = item.itemDescription
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    if(snapshot.key == gotUserId){
                        userContact.text = snapshot.child("contact").value.toString()
                        postedByUser.text = snapshot.child("emailId").value.toString()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }
}

