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

class AdminAdapter(private val itemList : ArrayList<AdvertiseData>):
    RecyclerView.Adapter<AdminAdapter.MyViewHolder>() {

    var itemIdPassed =""
    var userId =""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.admin_item,
            parent, false
        )
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentItem = itemList[position]
        Log.d("AdminAdapter", "Get current image : ${currentItem.itemImageUrl}")
        val imageUrl = "${currentItem.itemImageUrl}"
        Log.d("AdminAdapter","ImgUrl Fetched :$imageUrl")
        Glide.with(holder.itemView.context).load(imageUrl).into(holder.itemImageA)

        with(holder.itemView){
            setOnClickListener {
                itemIdPassed = currentItem.itemId.toString()
                userId= currentItem.userID
                val selectedItemIdBundle = bundleOf("itemId" to itemIdPassed,"userId" to userId)
                findNavController().navigate(R.id.action_adminFragment_to_adminDescriptionFragment,selectedItemIdBundle)
            }
        }
        holder.itemNameA.text = currentItem.itemName
        holder.itemPriceA.text = currentItem.itemPrice
        holder.itemVerifiedA.text = currentItem.verified

    }
    override fun getItemCount(): Int {
        return itemList.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val itemImageA: ImageView = itemView.findViewById(R.id.item_ImageA)
        val itemNameA: TextView = itemView.findViewById(R.id.item_NameA)
        val itemPriceA: TextView = itemView.findViewById(R.id.item_PriceA)
        val itemVerifiedA : TextView = itemView.findViewById(R.id.item_VerifiedA)
    }

}