package com.project.advertisementapp

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import nl.dionsegijn.konfetti.KonfettiView
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size

class SubscriberFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    private lateinit var detailRecyclerView: RecyclerView
    val subscriberList = arrayListOf<UserData>()
    var userSubbedList = mutableListOf<String>()
    var subCount = 0

    lateinit var viewKonfetti : KonfettiView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userSubbedList.clear()
            userSubbedList = it.getStringArrayList("currentUserId")!!
            Log.d("SubscriberFragment", "Item list Received  : $userSubbedList")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_subscriber, container, false)
        auth = Firebase.auth


        viewKonfetti = view.findViewById(R.id.viewKonfetti)
        viewKonfetti.build()
            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .setTimeToLive(2000L)
            .addShapes(Shape.Square, Shape.Circle)
            .addSizes(Size(12))
            .setPosition(-50f, viewKonfetti.width + 50f, -50f, -50f)
            .streamFor(300, 2000L)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        detailRecyclerView = view.findViewById(R.id.recyclerViewD)
        detailRecyclerView.layoutManager = LinearLayoutManager(context)
        detailRecyclerView.setHasFixedSize(true)

        getSubscriberData()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getSubscriberData() {

        val userDataRef = FirebaseDatabase.getInstance().getReference("UserData")
        userDataRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    subscriberList.clear()
                    for (user in snapshot.children) {
                        for (i in userSubbedList) {
                            if (i == user?.key) {
                                Log.d("subsrrer", i)
                                Log.d("Subscriber Fragment", i)
                                Log.d("Subscriber Fragment", snapshot.value.toString())
                                val subbedUserDetails = user.getValue(UserData::class.java)
                                subscriberList.add(subbedUserDetails!!)
                            }
                        }
                    }
                    userSubbedList.clear()
                    detailRecyclerView.adapter = SubscriberAdapter(subscriberList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

