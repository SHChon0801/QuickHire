package my.edu.tarc.quickhire.ui.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isNotEmpty
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import my.edu.tarc.quickhire.R
import my.edu.tarc.quickhire.User
import my.edu.tarc.quickhire.databinding.ActivityRegisterBinding
import my.edu.tarc.quickhire.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReferenceOrg: DatabaseReference
    private lateinit var databaseReferenceEmp: DatabaseReference
    private lateinit var database: FirebaseDatabase

    //Test
    private lateinit var profilePic:String
    private lateinit var name:String
    private lateinit var about:String
    private lateinit var state:String
    private lateinit var currentJob:String
    private lateinit var phone:String
    private lateinit var timePrefer:String
    private lateinit var education:String
    private lateinit var skill:String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReferenceOrg = database.getReference("Employees")
        databaseReferenceEmp = database.getReference("Organizations")

        //Testing
        val currentUser = FirebaseAuth.getInstance().currentUser




        binding.btnRegister.setOnClickListener {
            val email = binding.editTextEmail.text.toString()
            val password = binding.editTextPassword.text.toString()
            val confirmPassword = binding.editTextConfirmPassword.text.toString()
            val roleRadioGroup = binding.rgGroup
            val userRole = roleRadioGroup.checkedRadioButtonId
            var role = ""

            when(userRole) {
                R.id.rbEmp ->{
                    role = "Employee"
                }
                R.id.rbOrg ->{
                    role = "Organization"
                }
            }



            if(email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty() && roleRadioGroup.checkedRadioButtonId != -1){
                if(password == confirmPassword){

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                        if(it.isSuccessful){

                            profilePic=""
                            name=""
                            about=""
                            state=""
                            currentJob=""
                            phone=""
                            timePrefer=""
                            education=""
                            skill=""

                            val newUser = User(name,about,state,currentJob,phone,timePrefer,
                                education,skill,email,password,role,profilePic)

                            if(currentUser!=null) {
                                when (userRole) {
                                    R.id.rbEmp -> {
                                        databaseReferenceEmp.child(currentUser.uid)
                                            .setValue(newUser)
                                    }
                                    R.id.rbOrg -> {
                                        databaseReferenceOrg.child(password).setValue(newUser)

                                    }
                                }
                            }










                            val intent = Intent(this,LoginActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else{
                    Toast.makeText(this,"Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                Toast.makeText(this,"Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textViewLoginPage.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }


    }
}


































