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
import com.google.firebase.ktx.Firebase

class AuthenticationFragment : Fragment() {

    // authentication
    lateinit var auth: FirebaseAuth

    lateinit var emailEditText: EditText
    lateinit var passwordEditText: EditText
    lateinit var loginbutton: Button

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
        return inflater.inflate(R.layout.fragment_authentication, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        createNotificationChannel()

        emailEditText = view.findViewById(R.id.emailLE)
        passwordEditText = view.findViewById(R.id.passLE)
        loginbutton = view.findViewById(R.id.loginB)

        auth = Firebase.auth
        val currentUser = auth.currentUser

        if(currentUser != null){
            if(currentUser.email =="test@gmail.com"){
                findNavController().navigate(R.id.action_authenticationFragment_to_adminFragment)
            }
            else {
                findNavController().navigate(R.id.action_authenticationFragment_to_listFragment)
            }
        }
        else{
            reload()
        }

        val t = view.findViewById<TextView>(R.id.clickhere)
        t.setOnClickListener {
            // execute action
            findNavController().navigate(R.id.action_authenticationFragment_to_registrationFragment)
        }
    }

    private fun reload(){
        loginbutton.setOnClickListener{

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if(email.isEmpty() && password.isEmpty()) {
                Toast.makeText(context,"Please enter all fields", Toast.LENGTH_LONG).show()
                Log.d("AuthActivity","LoginClicked")
            }
            else{
                signIn(email,password)
            }
        }
    }


    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Log.d("AuthActivity","Sign In complete")
                Toast.makeText(context, "Authentication Successful",
                    Toast.LENGTH_SHORT).show()
                if(email=="test@gmail.com" && password =="123456"){
                    findNavController().navigate(R.id.action_authenticationFragment_to_adminFragment)
                }
                else {
                    findNavController().navigate(R.id.action_authenticationFragment_to_listFragment)
                }
            }
            else{
                Log.d("AuthActivity", "Account creation failed", task.exception)
                Toast.makeText(context, "Authentication failed check your password or email",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

}