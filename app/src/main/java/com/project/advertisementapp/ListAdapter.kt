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

class ListAdapter(private val itemList: ArrayList<AdvertiseData>):
    RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    lateinit var itemView: View
    var itemIdPassed =""
    var userId =""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.list_items,
            parent, false
        )
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = itemList[position]

        holder.itemView.setOnClickListener {
            itemIdPassed = currentItem.itemId.toString()
            userId= currentItem.userID
            val selectedItemIdBundle = bundleOf("itemId" to itemIdPassed,"userId" to userId)
            it.findNavController().navigate(R.id.action_listFragment_to_detailsFragment,selectedItemIdBundle)
        }

        Log.d("ListAdapter", "Got : ${currentItem.itemImageUrl}")
        val imageUrl = "${currentItem.itemImageUrl}"
        Log.d("ListAdapter","imgUrl :$imageUrl")
        Glide.with(holder.itemView.context).load(imageUrl).into(holder.itemImage)

        holder.itemName.text = currentItem.itemName
        holder.itemPrice.text = currentItem.itemPrice
        holder.itemVerified.text = currentItem.verified
    }

    override fun getItemCount(): Int {

        return itemList.size
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemImage: ImageView = itemView.findViewById(R.id.item_Image)
        val itemName: TextView = itemView.findViewById(R.id.item_Name)
        val itemPrice: TextView = itemView.findViewById(R.id.item_Price)
        val itemVerified : TextView = itemView.findViewById(R.id.item_Verified)
    }

}