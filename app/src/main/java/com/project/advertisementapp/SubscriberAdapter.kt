package com.project.advertisementapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.function.LongFunction

class SubscriberAdapter(private val itemList : ArrayList<UserData>) :
    RecyclerView.Adapter<SubscriberAdapter.MyViewHolder>(){

    lateinit var auth: FirebaseAuth
    var subCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.subscriber_item,
            parent, false
        )
        auth = Firebase.auth

        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = itemList[position]

        holder.itemView.setOnCreateContextMenuListener { menu, v, menuInfo ->
            menu?.add("Unsubscribe")?.setOnMenuItemClickListener {
                // Delete Item
                val userRef = FirebaseDatabase.getInstance().getReference("SubscriberData").child(auth.currentUser?.uid.toString())
                userRef.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            subCount = snapshot.childrenCount.toInt()
                            Log.d("SubscriberAdapter", "SubCount : $subCount")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })

                for(i in 1..(subCount+1)) {
                    val subRef = FirebaseDatabase.getInstance().getReference("SubscriberData")
                        .child(auth.currentUser?.uid.toString()).child(i.toString())
                        .child("subscriberID")
                    subRef.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                if (snapshot.value == currentItem.uuid) {
                                    val delRef = FirebaseDatabase.getInstance()
                                        .getReference("SubscriberData")
                                        .child(auth.currentUser?.uid.toString())
                                        .child(i.toString())
                                    delRef.removeValue()
                                }

                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                }

                true
            }
        }

        holder.userName.text = currentItem.userName
        holder.emailS.text = currentItem.emailId
        holder.contactS.text = currentItem.contact
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName : TextView = itemView.findViewById(R.id.usernameS)
        val emailS : TextView = itemView.findViewById(R.id.emailS)
        val contactS : TextView = itemView.findViewById(R.id.contactS)
    }

}
