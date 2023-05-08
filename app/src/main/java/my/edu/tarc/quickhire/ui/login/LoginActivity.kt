package my.edu.tarc.quickhire.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import my.edu.tarc.quickhire.MainActivity
import my.edu.tarc.quickhire.databinding.ActivityLoginBinding
import my.edu.tarc.quickhire.ui.register.NewRegisterActivity
import my.edu.tarc.quickhire.ui.register.RegisterActivity
import androidx.navigation.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import my.edu.tarc.quickhire.Employee
import my.edu.tarc.quickhire.R

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()




        val database = FirebaseDatabase.getInstance()



        binding.btnLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            Log.d("TEST","DEEZ")

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        val user = FirebaseAuth.getInstance().currentUser
                        val uid = user?.uid
                        val employeesRef = FirebaseDatabase.getInstance().reference.child("Employees").child(uid?:"")
                        val organizationsRef = FirebaseDatabase.getInstance().reference.child("Organizations").child(uid?:"")

                        employeesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(employeesSnapshot: DataSnapshot) {
                                if (employeesSnapshot.exists()) {
                                    val role = employeesSnapshot.child("role").getValue(String::class.java)
                                    Log.d("TAG", "Employee role: $role")
                                    binding.textView17.text = role.toString()
                                } else {
                                    organizationsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(organizationsSnapshot: DataSnapshot) {
                                            if (organizationsSnapshot.exists()) {
                                                val role = organizationsSnapshot.child("role").getValue(String::class.java)
                                                Log.d("TAG", "Organization role: $role")
                                                binding.textView17.text = role.toString()
                                            } else {
                                                // Handle the case where the user does not exist in either node
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Log.e("TAG", "Failed to read value.", error.toException())
                                        }
                                    })
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("TAG", "Failed to read value.", error.toException())
                            }
                        })




                    }else{
                        Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }

                }
            }else{
                Toast.makeText(this,"Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }

        }



        binding.textViewRegisterPage.setOnClickListener {
            val intent = Intent(this, NewRegisterActivity::class.java)
            startActivity(intent)

        }
    }
}





































