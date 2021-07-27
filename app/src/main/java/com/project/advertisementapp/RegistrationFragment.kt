package com.project.advertisementapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class RegistrationFragment : Fragment() {


    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseDatabase

    lateinit var userNameEditText : EditText
    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var passwordEditText2: EditText
    lateinit var contactNumberText : EditText
    lateinit var registerButton: Button

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
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



        auth = Firebase.auth
        db = Firebase.database

        userNameEditText = view.findViewById(R.id.userNameRE)
        emailEditText = view.findViewById(R.id.emailRE)
        passwordEditText = view.findViewById(R.id.passRE)
        passwordEditText2 = view.findViewById(R.id.passRE2)
        contactNumberText = view.findViewById(R.id.contactRE)
        registerButton = view.findViewById(R.id.registerB)


        val t = view.findViewById<TextView>(R.id.goback)
        t.setOnClickListener {
            // execute action
            findNavController().navigate(R.id.action_registrationFragment_to_authenticationFragment)
        }

        registerButton.setOnClickListener {
            registerClicked(view)
        }
    }

    private fun registerClicked(view: View) {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()
        val password2 = passwordEditText2.text.toString()
        val userName = userNameEditText.text.toString()
        val contact = contactNumberText.text.toString()

        if(email.isNotEmpty() && password.isNotEmpty() && password2.isNotEmpty() && userName.isNotEmpty() && contact.isNotEmpty() && password == password2) {
            registerUser(userName,email, password,contact)
        }
        else if(password != password2){
            Toast.makeText(context,"Password Mismatch, Try Re-entering the password",Toast.LENGTH_LONG).show()
            Log.d("RegistrationFragment","Registration Failed - Try again")
        }
        else{
            Toast.makeText(context,"Please enter all fields",Toast.LENGTH_LONG).show()
            Log.d("RegistrationFragment","Registration Failed - email and password empty")
        }
    }

    private fun registerUser(username: String, email: String, password: String, contact: String) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener() {task ->
            if(task.isSuccessful){
                Log.d("RegistrationFragment","Created New User $email")
                Toast.makeText(context, "Account Creation Successful",
                    Toast.LENGTH_SHORT).show()

                val uuid = auth.currentUser?.uid.toString()
                val userDetails =UserData(username,email,uuid,contact)

                val userRef = db.getReference("UserData")
                userRef.child(uuid).setValue(userDetails)

                findNavController().navigate(R.id.action_registrationFragment_to_authenticationFragment)
            }
            else{
                Log.d("RegistrationFragment", "Account creation failed", task.exception)
                Toast.makeText(context, "Already a User",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}