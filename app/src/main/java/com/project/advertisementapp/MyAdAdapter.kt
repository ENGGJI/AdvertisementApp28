package com.project.advertisementapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class MyAdAdapter(private val myAdList : ArrayList<AdvertiseData>) :
    RecyclerView.Adapter<MyAdAdapter.MyAdViewHolder>(){

    lateinit var auth: FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.myaditemlist,parent,false)
        auth = Firebase.auth
        return MyAdViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyAdViewHolder, position: Int) {

        val adCurrentItem = myAdList[position]

        holder.itemView.setOnCreateContextMenuListener { menu, v, menuInfo ->

            // Edit Data Nodes
            menu?.add("Edit")?.setOnMenuItemClickListener {
                Log.d("Context", "Clicked at position $position & $it")
                val userEditBundle = bundleOf("ItemName" to adCurrentItem.itemName, "ImageName" to adCurrentItem.itemImage, "ItemDescription" to adCurrentItem.itemDescription, "ItemPrice" to adCurrentItem.itemPrice, "ItemId" to adCurrentItem.itemId, "ItemImageUrl" to adCurrentItem.itemImageUrl)

                holder.itemView.findNavController().navigate(R.id.action_myAddsFragment_to_updateItemFragment,userEditBundle)
                true
            }

            // Delete Data Nodes
            menu?.add("Delete")?.setOnMenuItemClickListener {
                Log.d("Context","Clicked at position $position & $it")
                // Delete Item

                val ref = FirebaseDatabase.getInstance().getReference("AddDetails").child("Details").child(adCurrentItem.itemId.toString())
                ref.removeValue()

                val itemRef = FirebaseDatabase.getInstance().getReference("ItemData").child(adCurrentItem.itemId.toString())
                itemRef.removeValue()

                val userRef = FirebaseDatabase.getInstance().getReference("UserData").child(auth.currentUser?.uid.toString()).child("itemPosted").child(adCurrentItem.itemId.toString())
                userRef.removeValue()

                true
            }
        }

        Log.d("ListAdapter", "Got : ${adCurrentItem.itemImageUrl}")
        val imageUrl = "${adCurrentItem.itemImageUrl}"
        Glide.with(holder.itemView.context).load(imageUrl).into(holder.adImages)

        holder.adTitles.text = adCurrentItem.itemName
        holder.adDescription.text = adCurrentItem.itemDescription
        holder.adPrice.text = adCurrentItem.itemPrice
        holder.adVerified.text = adCurrentItem.verified
    }

    override fun getItemCount(): Int {
        return myAdList.size
    }

    class MyAdViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val adImages : ImageView = itemView.findViewById(R.id.myAdImage)
        val adTitles : TextView = itemView.findViewById(R.id.myAdTitle)
        val adDescription : TextView = itemView.findViewById(R.id.myAdDesc)
        val adPrice : TextView = itemView.findViewById(R.id.myAdMoney)
        val adVerified : TextView = itemView.findViewById(R.id.myAdVerified)
    }
}