package com.project.advertisementapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AddPostFragment : Fragment() {

    //firebase
    lateinit var db: FirebaseDatabase
    lateinit var auth : FirebaseAuth

    lateinit var button: Button
    lateinit var image: ImageView


    //item info
    lateinit var itemNameEditText: EditText
    lateinit var itemPriceEditText: EditText
    lateinit var itemDescriptionEditText: EditText
    lateinit var itemIdEditText: EditText
    lateinit var imageUploadProgressBar: ProgressBar

    //item image
    lateinit var uri : Uri
    var imageUrl : String =""
    var imageName : String =""

    //subscriber default count
    var subCount = 0



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
        return inflater.inflate(R.layout.fragment_add_post, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // initiating Database
        db = Firebase.database

        // user based details
        auth = Firebase.auth

        // button onclick to upload the data on firebase
        button =view.findViewById(R.id.submitB)

        // item details to be uploaded -- in the orderly manner
        itemNameEditText = view.findViewById(R.id.itemNameE)
        itemPriceEditText = view.findViewById(R.id.itemPriceE)
        itemDescriptionEditText = view.findViewById(R.id.itemDescriptionE)
        itemIdEditText = view.findViewById(R.id.itemId)
        imageUploadProgressBar = view.findViewById(R.id.imageUploadProgress)

        imageUploadProgressBar.visibility = View.INVISIBLE
        button.visibility = View.INVISIBLE

        image = view.findViewById(R.id.ivPhoto)

        button.setOnClickListener{

            Intent(Intent.ACTION_GET_CONTENT).also{
                addItemInfo(
                    itemIdEditText,
                    itemNameEditText,
                    itemPriceEditText,
                    itemDescriptionEditText
                )
            }
            findNavController().navigate(R.id.action_addPostFragment_to_listFragment)
        }

        // imageview clickListener to upload photo from device itself

        image.setOnClickListener{
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type ="image/*"
                startActivityForResult(it,0)
            }
        }


        super.onViewCreated(view, savedInstanceState)
    }


    private fun uploadImage(uri: Uri?) {
        val auth = auth.currentUser.toString()
        Log.d("AddPostFragment","CurrentUser : $auth")
        imageName = uri?.lastPathSegment?.removePrefix("raw:/storage/emulated/0/Download/").toString()
        Log.d("AddFragment","Image name to be uploaded in firebase storage database : $imageName")
        val storageReference = FirebaseStorage.getInstance().reference.child("image/$imageName")

        if (uri != null) {
            storageReference.putFile(uri)
                .addOnSuccessListener {
                    it.metadata?.reference?.downloadUrl?.addOnSuccessListener {
                        imageUrl = it.toString()
                        Log.d("AddFragment","ImageUrl generated: $imageUrl")
                    }
                    imageUploadProgressBar.visibility = View.INVISIBLE
                    button.visibility = View.VISIBLE
                    Toast.makeText(context,"Your post has been sent to admin for approval",Toast.LENGTH_LONG).show()
                }.addOnFailureListener{
                    Toast.makeText(context,"Image Upload failed",Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun addItemInfo(itemIdEditText: EditText?, itemNameEditText: EditText?, itemPriceEditText: EditText?, itemDescriptionEditText: EditText?) {
        val itemId = itemIdEditText?.text.toString()
        val itemName = itemNameEditText?.text.toString()
        val itemPrice = itemPriceEditText?.text.toString()
        val itemDescription = itemDescriptionEditText?.text.toString()


        val userUuid = auth.currentUser?.uid.toString()

        val advertisementDetails = AdvertiseData(imageName, imageUrl,
            itemId, itemName, itemPrice, itemDescription,"Not Verified",userUuid)

        val itemDetails = ItemPosted(itemId,userUuid)


        val addRef = db.getReference("AddDetails")
        addRef.child("Details").child(itemId).setValue(advertisementDetails)

        val itemRef = db.getReference("ItemData")
        itemRef.child(itemId).setValue(itemDetails)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageUploadProgressBar.visibility = View.VISIBLE
        if(resultCode== Activity.RESULT_OK && requestCode ==0){
            uri = data?.data!! //this data refer to data of our intent

            image.setImageURI(uri)
            uploadImage(uri)
        }
    }
}