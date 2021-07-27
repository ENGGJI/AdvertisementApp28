package com.project.advertisementapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase


class AccountsFragment : Fragment() {

    lateinit var auth: FirebaseAuth

    lateinit var logoutButton: Button
    lateinit var subscriberButton : ImageView

    // current user deatils
    lateinit var uname: TextView
    lateinit var email: TextView
    lateinit var contact: TextView

    lateinit var db : FirebaseDatabase

    var userSubbedList = mutableListOf<String>()
    var subCount = 0
    lateinit var userRef:DatabaseReference


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
        return inflater.inflate(R.layout.fragment_accounts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth

        subscriberButton = view.findViewById(R.id.subscriberListB)
        logoutButton = view.findViewById(R.id.logoutB)

        uname = view.findViewById(R.id.unameT)
        email = view.findViewById(R.id.emailT)
        contact = view.findViewById(R.id.contactT)

        db = FirebaseDatabase.getInstance()



        account()

        subscriberButton.setOnClickListener {

            userRef = FirebaseDatabase.getInstance().getReference("SubscriberData").child(auth.currentUser?.uid.toString())
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        subCount = snapshot.childrenCount.toInt()
                        Log.d("Details Fragment", "subCount : $subCount")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })

            val subRef = FirebaseDatabase.getInstance().getReference("SubscriberData")
            subRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userAd in snapshot.children){
                            if (userAd.key == auth.currentUser?.uid) {
                                for (i in 1..(subCount+1)) {
                                    Log.d("AccountsFragment", snapshot.child(auth.currentUser?.uid.toString()).child(i.toString()).child("subscriberUserID").value.toString())
                                    val subbedUserId = subRef.child(auth.currentUser?.uid.toString()).child(i.toString())
                                    subbedUserId.addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                for (user in snapshot.children) {
                                                    userSubbedList.add(snapshot.child("subscriberID").value.toString())
                                                    Log.d("AccountsFragment","SubscriberList $userSubbedList")
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

            val currentSubscribedUserId = bundleOf("currentUserId" to userSubbedList)
            findNavController().navigate(R.id.action_accountsFragment_to_subscriberFragment,currentSubscribedUserId)
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            findNavController().navigate(R.id.action_accountsFragment_to_authenticationFragment)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun account() {
        val user = auth.currentUser
        val userReference = db.getReference("UserData").child(user?.uid!!)
        userReference.addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    uname.text = snapshot.child("userName").value.toString()
                    email.text = snapshot.child("emailId").value.toString()
                    contact.text = snapshot.child("contact").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}

