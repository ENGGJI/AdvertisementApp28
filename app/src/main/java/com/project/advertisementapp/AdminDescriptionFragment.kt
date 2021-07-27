package com.project.advertisementapp
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class AdminDescriptionFragment : Fragment() {
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

    lateinit var Button: Button

    var gotItemId = ""
    var gotUserId =""


    var itemList = mutableListOf<AdvertiseData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            gotItemId = it.getString("itemId")!!
            Log.d("AdminDescriptionFrag","Current itemId : $gotItemId")
            gotUserId = it.getString("userId")!!
            Log.d("AdminDescriptionFrag","Current userId : $gotUserId")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_description, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = Firebase.auth

        itemImageUrl = view.findViewById(R.id.itemImageAD)
        itemName = view.findViewById(R.id.itemNameAD)
        itemPrice = view.findViewById(R.id.itemPriceAD)
        itemDescription = view.findViewById(R.id.itemDescriptionAD)
        postedByUser = view.findViewById(R.id.postedByUserAD)
        userContact = view.findViewById(R.id.userContactAD)

        Button = view.findViewById(R.id.verifyAD)

        val db = FirebaseDatabase.getInstance()
        itemRef = db.reference.child("AddDetails").child("Details")
        userRef = FirebaseDatabase.getInstance().reference.child("UserData").child(gotUserId)
        subRef = FirebaseDatabase.getInstance().reference.child("UserData")

        getData()

        Button.setOnClickListener {
            itemRef.child(gotItemId).child("verified").setValue("Verified")
            findNavController().navigate(R.id.action_adminDescriptionFragment_to_adminFragment)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun getData() {
        itemRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(itemSnapshot in snapshot.children){
                        val item = itemSnapshot.getValue(AdvertiseData::class.java)
                        if (item != null) {
                            itemList.add(item)
                        }
                        Log.d("AdminDescriptionFrag","ItemId fetched advertised data : ${item?.itemId}")
                        if(item?.itemId == gotItemId ){
                            val imageUrl = item.itemImageUrl
                            Glide.with(this@AdminDescriptionFragment).load(imageUrl).into(itemImageUrl)
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

        userRef.addValueEventListener(object : ValueEventListener {
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